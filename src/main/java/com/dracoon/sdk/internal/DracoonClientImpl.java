package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonApiBuilder;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.auth.AuthChecker;
import com.dracoon.sdk.internal.auth.AuthHolder;
import com.dracoon.sdk.internal.auth.AuthInterceptor;
import com.dracoon.sdk.internal.auth.AuthInterceptorImpl;
import com.dracoon.sdk.internal.auth.AuthTokenRefresher;
import com.dracoon.sdk.internal.auth.AuthTokenRefresherImpl;
import com.dracoon.sdk.internal.auth.AuthTokenRetriever;
import com.dracoon.sdk.internal.auth.AuthTokenRetrieverImpl;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpClientBuilder;
import com.dracoon.sdk.internal.http.HttpHelper;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.service.ServiceLocator;
import okhttp3.OkHttpClient;

public class DracoonClientImpl extends DracoonClient {

    private final AuthHolder mAuthHolder = new AuthHolder();
    private final EncryptionPasswordHolder mEncPasswordHolder = new EncryptionPasswordHolder();

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig = new DracoonHttpConfig();

    private OAuthClient mOAuthClient;

    private OkHttpClient mHttpClient;
    protected HttpHelper mHttpHelper;

    private AuthChecker mAuthChecker;
    private AuthTokenRetriever mAuthTokenRetriever;
    private AuthTokenRefresher mAuthTokenRefresher;

    private DracoonApi mDracoonApi;
    protected DracoonErrorParser mDracoonErrorParser;

    protected CryptoWrapper mCryptoWrapper;

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

    public EncryptionPasswordHolder getEncryptionPasswordHolder() {
        return mEncPasswordHolder;
    }

    public char[] getEncryptionPassword() {
        return mEncPasswordHolder.get();
    }

    public void setEncryptionPassword(char[] encryptionPassword) {
        mEncPasswordHolder.set(encryptionPassword);
    }

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public DracoonHttpConfig getHttpConfig() {
        return mHttpConfig;
    }

    public void setHttpConfig(DracoonHttpConfig httpConfig) {
        mHttpConfig = httpConfig != null ? httpConfig : new DracoonHttpConfig();
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

    public ServiceLocator getServiceLocator() {
        return mServiceLocator;
    }

    // --- Initialization methods ---

    public void init() {
        initOAuthClient();

        initHttpClient();
        initHttpHelper();

        initAuthHelpers();

        initDracoonApi();
        initDracoonErrorParser();

        mCryptoWrapper = new CryptoWrapper(mLog);

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

    protected void initAuthHelpers() {
        mAuthChecker = new AuthChecker(() -> mServiceLocator.getAccountService().pingUser());

        mAuthTokenRetriever = new AuthTokenRetrieverImpl(mOAuthClient, mAuthHolder);
        mAuthTokenRefresher = new AuthTokenRefresherImpl(mOAuthClient, mAuthHolder);
    }

    protected void initDracoonApi() {
        AuthInterceptor authInterceptor = new AuthInterceptorImpl(mAuthHolder, mAuthTokenRefresher);
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
        mAuthTokenRetriever.retrieve();
    }

    @Override
    public boolean isAuthValid() throws DracoonNetIOException, DracoonApiException {
        return mAuthChecker.isAuthValid();
    }

    @Override
    public void checkAuthValid() throws DracoonNetIOException, DracoonApiException {
        mAuthChecker.checkAuthValid();
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
