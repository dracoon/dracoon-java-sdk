package com.dracoon.sdk.internal;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.http.BufferedSocketFactory;
import com.dracoon.sdk.internal.http.HttpHelper;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import com.dracoon.sdk.internal.service.ServiceLocator;
import com.dracoon.sdk.internal.util.GsonCharArrayTypeAdapter;
import com.dracoon.sdk.internal.util.GsonDateTypeAdapter;
import com.dracoon.sdk.internal.util.GsonVoidTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DracoonClientImpl extends DracoonClient {

    private static final int HTTP_SOCKET_BUFFER_SIZE = 16 * DracoonConstants.KIB;

    private final AuthHolder mAuthHolder = new AuthHolder();

    private char[] mEncryptionPassword;

    protected Log mLog = new NullLog();

    private DracoonHttpConfig mHttpConfig = new DracoonHttpConfig();
    private OkHttpClient mHttpClient;
    protected HttpHelper mHttpHelper;

    protected CryptoWrapper mCryptoWrapper;
    protected ThreadHelper mThreadHelper;
    protected FileStreamHelper mFileStreamHelper;

    private OAuthClient mOAuthClient;

    private DracoonApi mDracoonApi;
    protected DracoonErrorParser mDracoonErrorParser;

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

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public DracoonApi getDracoonApi() {
        return mDracoonApi;
    }

    public DracoonErrorParser getDracoonErrorParser() {
        return mDracoonErrorParser;
    }

    public HttpHelper getHttpHelper() {
        return mHttpHelper;
    }

    public long getChunkSize() {
        return ((long) mHttpConfig.getChunkSize()) * DracoonConstants.KIB;
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(mHttpConfig.getConnectTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(mHttpConfig.getReadTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(mHttpConfig.getWriteTimeout(), TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.socketFactory(new BufferedSocketFactory(HTTP_SOCKET_BUFFER_SIZE));
        if (mHttpConfig.isProxyEnabled()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    mHttpConfig.getProxyAddress(), mHttpConfig.getProxyPort()));
            builder.proxy(proxy);
        }
        for (Interceptor interceptor : mHttpConfig.getOkHttpApplicationInterceptors()) {
            builder.addInterceptor(interceptor);
        }
        builder.addNetworkInterceptor(new UserAgentInterceptor(mHttpConfig.getUserAgent()));
        for (Interceptor interceptor : mHttpConfig.getOkHttpNetworkInterceptors()) {
            builder.addNetworkInterceptor(interceptor);
        }
        mHttpClient = builder.build();
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

        OkHttpClient httpClient = mHttpClient.newBuilder()
                .followRedirects(false)
                .addInterceptor(authInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GsonVoidTypeAdapter.TYPE, new GsonVoidTypeAdapter())
                .registerTypeAdapter(GsonDateTypeAdapter.TYPE, new GsonDateTypeAdapter())
                .registerTypeAdapter(GsonCharArrayTypeAdapter.TYPE, new GsonCharArrayTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl.toString())
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mDracoonApi = retrofit.create(DracoonApi.class);
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
