package com.dracoon.sdk.internal;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.model.UserKeyPair;
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

    private DynamicServiceProxy mServiceProxy;

    protected ServerInfoService mServerInfoService;
    protected ServerSettingsService mServerSettingsService;
    protected ServerPoliciesService mServerPoliciesService;
    protected AccountService mAccountService;
    protected UsersService mUsersService;
    protected NodesService mNodesService;
    protected SharesService mSharesService;

    protected FileKeyFetcher mFileKeyFetcher;
    protected FileKeyGenerator mFileKeyGenerator;
    protected AvatarDownloader mAvatarDownloader;

    protected String mApiVersion = null;

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

    CryptoWrapper getCryptoWrapper() {
        return mCryptoWrapper;
    }

    ThreadHelper getThreadHelper() {
        return mThreadHelper;
    }

    FileStreamHelper getFileStreamHelper() {
        return mFileStreamHelper;
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

        initServices();
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

    protected void initDracoonErrorParser() {
        mDracoonErrorParser = new DracoonErrorParser();
        mDracoonErrorParser.setLog(mLog);
    }

    private void initServices() {
        mServerInfoService = new ServerInfoService(this);
        mServerSettingsService = new ServerSettingsService(this);
        mServerPoliciesService = new ServerPoliciesService(this);
        mAccountService = new AccountService(this);
        mUsersService = new UsersService(this);
        mNodesService = new NodesService(this);
        mSharesService = new SharesService(this);

        mFileKeyFetcher = new FileKeyFetcher(this);
        mFileKeyGenerator = new FileKeyGenerator(this);
        mAvatarDownloader = new AvatarDownloader(this);
    }

    private void initServiceProxy() {
        mServiceProxy = new DynamicServiceProxy();
        mServiceProxy.addServices(
                mServerInfoService,
                mServerSettingsService,
                mServerPoliciesService,
                mAccountService,
                mUsersService,
                mNodesService,
                mSharesService);
        mServiceProxy.prepare();
    }

    public void checkApiVersionSupported() throws DracoonNetIOException, DracoonApiException {
        if (mApiVersion != null) {
            return;
        }

        if (!isApiVersionGreaterEqual(DracoonConstants.API_MIN_VERSION)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUPPORTED);
        }
    }

    public void checkApiVersionGreaterEqual(String apiVersion) throws DracoonNetIOException,
            DracoonApiException {
        if (!isApiVersionGreaterEqual(apiVersion)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUFFICIENT);
        }
    }

    public boolean isApiVersionGreaterEqual(String minApiVersion) throws DracoonNetIOException,
            DracoonApiException {
        if (mApiVersion == null) {
            mApiVersion = mServerInfoService.getVersion();
        }

        if (mApiVersion == null || mApiVersion.isEmpty()) {
            return false;
        }

        String[] av = mApiVersion.split("\\-")[0].split("\\.");
        String[] mav = minApiVersion.split("\\.");

        for (int i = 0; i < 3; i++) {
            int v;
            int mv;

            try {
                v = Integer.valueOf(av[i]);
                mv = Integer.valueOf(mav[i]);
            } catch (Exception e) {
                throw new RuntimeException("Can't parse server API version.", e);
            }

            if (v > mv) {
                break;
            } else if (v < mv) {
                return false;
            }
        }

        return true;
    }

    public void checkUserKeyPairVersionSupported(UserKeyPair.Version version)
            throws DracoonNetIOException, DracoonApiException {
        if (version == null) {
            throw new IllegalArgumentException("Version can't be null.");
        }

        List<UserKeyPair.Version> versions = mServerSettingsService.getAvailableUserKeyPairVersions();
        boolean apiSupportsVersion = versions.stream().anyMatch(v -> v == version);
        if (!apiSupportsVersion) {
            throw new DracoonApiException(DracoonApiCode.SERVER_CRYPTO_VERSION_NOT_SUPPORTED);
        }
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
            mAccountService.pingUser();
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
        mAccountService.pingUser();
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

    // --- Methods to get internal services ---

    public ServerSettingsService getServerSettingsImpl() {
        return mServerSettingsService;
    }

    public AccountService getAccountImpl() {
        return mAccountService;
    }

    public NodesService getNodesImpl() {
        return mNodesService;
    }

    public FileKeyFetcher getFileKeyFetcher() {
        return mFileKeyFetcher;
    }

    public FileKeyGenerator getFileKeyGenerator() {
        return mFileKeyGenerator;
    }

    public AvatarDownloader getAvatarDownloader() {
        return mAvatarDownloader;
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
