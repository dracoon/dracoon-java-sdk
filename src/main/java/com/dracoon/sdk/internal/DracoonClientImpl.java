package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonApiBuilder;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.http.HttpClientBuilder;
import com.dracoon.sdk.internal.http.HttpHelper;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import com.dracoon.sdk.internal.service.ServiceLocator;
import okhttp3.OkHttpClient;

public class DracoonClientImpl extends DracoonClient {

    private final AuthHolder mAuthHolder = new AuthHolder();

    private char[] mEncryptionPassword;

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig = new DracoonHttpConfig();

    private OAuthClient mOAuthClient;

    private OkHttpClient mHttpClient;
    protected HttpHelper mHttpHelper;

    private DracoonApi mDracoonApi;
    protected DracoonErrorParser mDracoonErrorParser;

    protected CryptoWrapper mCryptoWrapper;
    protected ThreadHelper mThreadHelper;
    protected FileStreamHelper mFileStreamHelper;

    protected ServiceLocator mServiceLocator;
    private DynamicServiceProxy mServiceProxy;

    public DracoonClientImpl(URL serverUrl) {
        super(serverUrl);
    }

    public DracoonAuth getAuth() {
        return mAuthHolder.get();
    }

    public void setAuth(DracoonAuth auth) {
        mAuthHolder.set(auth);
    }

    public char[] getEncryptionPassword() {
        return mEncryptionPassword;
    }

    public void setEncryptionPassword(char[] encryptionPassword) {
        mEncryptionPassword = encryptionPassword;
    }

    public char[] getEncryptionPasswordOrAbort() throws DracoonCryptoException {
        char[] encryptionPassword = getEncryptionPassword();
        if (encryptionPassword == null) {
            throw new DracoonCryptoException(DracoonCryptoCode.MISSING_PASSWORD_ERROR);
        }
        return encryptionPassword;
    }

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public void setHttpConfig(DracoonHttpConfig httpConfig) {
        mHttpConfig = httpConfig != null ? httpConfig : new DracoonHttpConfig();
    }

    public long getChunkSize() {
        return ((long) mHttpConfig.getChunkSize()) * DracoonConstants.KIB;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public HttpHelper getHttpHelper() {
        return mHttpHelper;
    }

    public DracoonApi getDracoonApi() {
        return mDracoonApi;
    }

    public DracoonErrorParser getDracoonErrorParser() {
        return mDracoonErrorParser;
    }

    public CryptoWrapper getCryptoWrapper() {
        return mCryptoWrapper;
    }

    public ThreadHelper getThreadHelper() {
        return mThreadHelper;
    }

    public FileStreamHelper getFileStreamHelper() {
        return mFileStreamHelper;
    }

    public ServiceLocator getServiceLocator() {
        return mServiceLocator;
    }

    // --- Initialization methods ---

    public void init() {
        initOAuthClient();

        initHttpClient();
        initHttpHelper();

        initDracoonApi();
        initDracoonErrorParser();

        mCryptoWrapper = new CryptoWrapper(mLog);
        mThreadHelper = new ThreadHelper();
        mFileStreamHelper = new FileStreamHelper();

        initServiceLocator();
        initServiceProxy();
    }

    protected void initOAuthClient() {
        mOAuthClient = new OAuthClient(mServerUrl);
        mOAuthClient.setLog(mLog);
        mOAuthClient.setHttpConfig(mHttpConfig);
        mOAuthClient.init();
    }

    protected void initHttpClient() {
        mHttpClient = new HttpClientBuilder().build(mHttpConfig);
    }

    protected void initHttpHelper() {
        mHttpHelper = new HttpHelper();
        mHttpHelper.setLog(mLog);
        mHttpHelper.setRetryEnabled(mHttpConfig.isRetryEnabled());
        mHttpHelper.setRateLimitingEnabled(mHttpConfig.isRateLimitingEnabled());
        mHttpHelper.init();
    }

    protected void initDracoonApi() {
        AuthInterceptor authInterceptor = new AuthInterceptor(mOAuthClient, mAuthHolder);
        mDracoonApi = new DracoonApiBuilder().build(mServerUrl, mHttpClient, authInterceptor);
    }

    private void initDracoonErrorParser() {
        mDracoonErrorParser = new DracoonErrorParser();
        mDracoonErrorParser.setLog(mLog);
    }

    private void initServiceLocator() {
        mServiceLocator = new ServiceLocator(this);
    }

    private void initServiceProxy() {
        mServiceProxy = new DynamicServiceProxy(mServiceLocator);
        mServiceProxy.prepare();
    }

    public void checkApiVersionSupported() throws DracoonNetIOException, DracoonApiException {
        mServiceLocator.getServerInfoService().checkVersionSupported();
    }

    public void retrieveAuthTokens() throws DracoonApiException, DracoonNetIOException {
        DracoonAuth auth = mAuthHolder.get();
        if (auth == null || !auth.getMode().equals(DracoonAuth.Mode.AUTHORIZATION_CODE)) {
            return;
        }

        // Try to retrieve auth tokens
        OAuthTokens tokens = mOAuthClient.retrieveTokens(auth.getClientId(), auth.getClientSecret(),
                auth.getAuthorizationCode());

        // Update auth data
        auth = new DracoonAuth(auth.getClientId(), auth.getClientSecret(), tokens.accessToken,
                tokens.refreshToken);
        mAuthHolder.set(auth);
    }

    @Override
    public boolean isAuthValid() throws DracoonNetIOException, DracoonApiException {
        try {
            mServiceLocator.getAccountService().pingUser();
        } catch (DracoonApiException e) {
            if (e.getCode().isAuthError()) {
                return false;
            } else {
                throw e;
            }
        }
        return true;
    }

    @Override
    public void checkAuthValid() throws DracoonNetIOException, DracoonApiException {
        mServiceLocator.getAccountService().pingUser();
    }

    // --- Methods to get public handlers ---

    @Override
    public Server server() {
        return mServiceProxy.server();
    }

    @Override
    public Account account() {
        return mServiceProxy.account();
    }

    @Override
    public Users users() {
        return mServiceProxy.users();
    }

    @Override
    public Groups groups() {
        return mServiceProxy.groups();
    }

    @Override
    public Nodes nodes() {
        return mServiceProxy.nodes();
    }

    @Override
    public Shares shares() {
        return mServiceProxy.shares();
    }

    // --- Helper methods ---

    public String buildApiUrl(String... pathSegments) {
        StringBuilder sb = new StringBuilder();
        sb.append(mServerUrl);
        sb.append(DracoonConstants.API_PATH);
        for (String pathSegment : pathSegments) {
            sb.append("/").append(pathSegment);
        }
        return sb.toString();
    }

}
