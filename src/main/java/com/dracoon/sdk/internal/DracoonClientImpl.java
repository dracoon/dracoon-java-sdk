package com.dracoon.sdk.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;
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
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import com.dracoon.sdk.internal.util.DateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DracoonClientImpl extends DracoonClient {

    private static final int HTTP_SOCKET_BUFFER_SIZE = 16 * DracoonConstants.KIB;

    private static final long AUTH_REFRESH_SKIP_INTERVAL = 15 * DracoonConstants.SECOND;

    private DracoonAuth mAuth;

    private final Interceptor mAuthInterceptor = new Interceptor() {

        private long mLastRefreshTime = 0L;
        private InterceptedIOException mLastRefreshException = null;

        @Override
        public Response intercept(Chain chain) throws IOException {
            // Get request
            Request request = chain.request();

            // If no authorization is needed: Send unchanged request
            if (isPublicRequest(request)) {
                return chain.proceed(request);
            }
            // If no authorization data was provided: Send unchanged request
            if (mAuth == null) {
                return chain.proceed(request);
            }

            // Try to send request
            Response response = chain.proceed(addAuthorizationHeader(request));

            // If request was successful: Return response
            if (isSuccessfulResponse(response)) {
                return response;
            }
            // If no refresh token was provided: Return response
            if (mAuth.getRefreshToken() == null) {
                return response;
            }

            // Close old response
            response.close();

            // Try to refresh tokens
            refreshAuthTokens();

            // Try to resend request
            return chain.proceed(addAuthorizationHeader(request));
        }

        private boolean isPublicRequest(Request request) {
            return request.url().encodedPath().startsWith(DracoonConstants.API_PATH + "/public/");
        }

        private boolean isSuccessfulResponse(Response response) {
            return response.code() != HttpStatus.UNAUTHORIZED.getNumber();
        }

        private Request addAuthorizationHeader(Request request) {
            return request.newBuilder().header(DracoonConstants.AUTHORIZATION_HEADER,
                    DracoonConstants.AUTHORIZATION_TYPE + " " + mAuth.getAccessToken()).build();
        }

        private synchronized void refreshAuthTokens() throws InterceptedIOException {
            // If refresh is not overdue: Abort
            if (mLastRefreshTime + AUTH_REFRESH_SKIP_INTERVAL > System.currentTimeMillis()) {
                if (mLastRefreshException != null) {
                    throw mLastRefreshException;
                }
                return;
            }

            // Try to refresh tokens
            try {
                OAuthTokens tokens = mOAuthClient.refreshTokens(mAuth.getRefreshToken());
                mAuth = new DracoonAuth(mAuth.getClientId(), mAuth.getClientSecret(),
                        tokens.accessToken, tokens.refreshToken);
                mLastRefreshTime = System.currentTimeMillis();
                mLastRefreshException = null;
            } catch (DracoonNetIOException e) {
                throw new InterceptedIOException(e);
            } catch (DracoonApiException e) {
                mLastRefreshTime = System.currentTimeMillis();
                mLastRefreshException = new InterceptedIOException(e);
                throw mLastRefreshException;
            }
        }

    };

    private String mEncryptionPassword;

    protected Log mLog = new NullLog();

    private DracoonHttpConfig mHttpConfig;
    private OkHttpClient mHttpClient;
    protected HttpHelper mHttpHelper;

    protected CryptoWrapper mCryptoWrapper;
    protected ThreadHelper mThreadHelper;
    protected FileStreamHelper mFileStreamHelper;

    private OAuthClient mOAuthClient;

    protected DracoonService mDracoonService;
    protected DracoonErrorParser mDracoonErrorParser;

    protected DracoonServerImpl mServer;
    protected DracoonServerSettingsImpl mServerSettings;
    protected DracoonServerPoliciesImpl mServerPolicies;
    protected DracoonAccountImpl mAccount;
    protected Users mUsers;
    protected Groups mGroups;
    protected DracoonNodesImpl mNodes;
    protected DracoonSharesImpl mShares;

    protected FileKeyFetcher mFileKeyFetcher;
    protected FileKeyGenerator mFileKeyGenerator;
    protected AvatarDownloader mAvatarDownloader;

    protected String mApiVersion = null;

    public DracoonClientImpl(URL serverUrl) {
        super(serverUrl);
    }

    public DracoonAuth getAuth() {
        return mAuth;
    }

    public void setAuth(DracoonAuth auth) {
        mAuth = auth;
    }

    public String getEncryptionPassword() {
        return mEncryptionPassword;
    }

    public String getEncryptionPasswordOrAbort() throws DracoonCryptoException {
        String encryptionPassword = getEncryptionPassword();
        if (encryptionPassword == null) {
            throw new DracoonCryptoException(DracoonCryptoCode.MISSING_PASSWORD_ERROR);
        }
        return encryptionPassword;
    }

    public void setEncryptionPassword(String encryptionPassword) {
        mEncryptionPassword = encryptionPassword;
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
        mHttpConfig = httpConfig;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public DracoonService getDracoonService() {
        return mDracoonService;
    }

    public DracoonErrorParser getDracoonErrorParser() {
        return mDracoonErrorParser;
    }

    public HttpHelper getHttpHelper() {
        return mHttpHelper;
    }

    public int getS3DefaultChunkSize() {
        return DracoonConstants.S3_DEFAULT_CHUNK_SIZE;
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

    // --- Initialization methods ---

    public void init() throws DracoonNetIOException, DracoonApiException {
        initOAuthClient();

        initHttpClient();
        initHttpHelper();
        initDracoonService();
        initDracoonErrorParser();

        mCryptoWrapper = new CryptoWrapper(mLog);
        mThreadHelper = new ThreadHelper();
        mFileStreamHelper = new FileStreamHelper();

        mServer = new DracoonServerImpl(this);
        mServerSettings = new DracoonServerSettingsImpl(this);
        mServerPolicies = new DracoonServerPoliciesImpl(this);
        mAccount = new DracoonAccountImpl(this);
        mUsers = new DracoonUsersImpl(this);
        mNodes = new DracoonNodesImpl(this);
        mShares = new DracoonSharesImpl(this);

        mFileKeyFetcher = new FileKeyFetcher(this);
        mFileKeyGenerator = new FileKeyGenerator(this);
        mAvatarDownloader = new AvatarDownloader(this);

        assertApiVersionSupported();

        retrieveAuthTokens();
    }

    protected void initOAuthClient() {
        if (mAuth == null) {
            return;
        }

        mOAuthClient = new OAuthClient(mServerUrl, mAuth.getClientId(), mAuth.getClientSecret());
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

    protected void initDracoonService() {
        OkHttpClient httpClient = mHttpClient.newBuilder()
                .addInterceptor(mAuthInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(Void.class, new TypeAdapter<Void>() {
                    @Override
                    public void write(JsonWriter out, Void value) {
                        // SONAR: Empty method body is intentional
                    }

                    @Override
                    public Void read(JsonReader in) {
                        return null;
                    }
                })
                .registerTypeAdapter(Date.class, new TypeAdapter<Date>() {
                    @Override
                    public void write(JsonWriter out, Date value) throws IOException {
                        out.value(DateUtils.formatTime(value));
                    }

                    @Override
                    public Date read(JsonReader in) throws IOException {
                        return DateUtils.parseTime(in.nextString());
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl.toString())
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mDracoonService = retrofit.create(DracoonService.class);
    }

    protected void initDracoonErrorParser() {
        mDracoonErrorParser = new DracoonErrorParser();
        mDracoonErrorParser.setLog(mLog);
    }

    public void assertApiVersionSupported() throws DracoonNetIOException, DracoonApiException {
        if (mApiVersion != null) {
            return;
        }

        if (!isApiVersionGreaterEqual(DracoonConstants.API_MIN_VERSION)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUPPORTED);
        }
    }

    public void assertUserKeyPairVersionSupported(UserKeyPair.Version version)
            throws DracoonNetIOException, DracoonApiException {
        if (version == null) {
            throw new IllegalArgumentException("Version can't be null.");
        }

        if (!isApiVersionGreaterEqual(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS)) {
            if (version != UserKeyPair.Version.RSA2048) {
                throw new DracoonApiException(DracoonApiCode.SERVER_CRYPTO_VERSION_NOT_SUPPORTED);
            }
            return;
        }

        List<UserKeyPair.Version> versions = mServerSettings.getAvailableUserKeyPairVersions();
        boolean apiSupportsVersion = versions.stream().anyMatch(v -> v == version);
        if (!apiSupportsVersion) {
            throw new DracoonApiException(DracoonApiCode.SERVER_CRYPTO_VERSION_NOT_SUPPORTED);
        }
    }

    public boolean isApiVersionGreaterEqual(String minApiVersion) throws DracoonNetIOException,
            DracoonApiException {
        if (mApiVersion == null) {
            mApiVersion = mServer.getVersion();
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

    private void retrieveAuthTokens() throws DracoonApiException, DracoonNetIOException {
        if (mAuth == null || !mAuth.getMode().equals(DracoonAuth.Mode.AUTHORIZATION_CODE)) {
            return;
        }

        // Try to retrieve auth tokens
        OAuthTokens tokens = mOAuthClient.retrieveTokens(mAuth.getAuthorizationCode());

        // Update auth data
        mAuth = new DracoonAuth(mAuth.getClientId(), mAuth.getClientSecret(), tokens.accessToken,
                tokens.refreshToken);
    }

    @Override
    public boolean isAuthValid() throws DracoonNetIOException, DracoonApiException {
        try {
            mAccount.pingUser();
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
        mAccount.pingUser();
    }

    // --- Methods to get public handlers ---

    @Override
    public Server server() {
        return mServer;
    }

    @Override
    public Account account() {
        return mAccount;
    }

    @Override
    public Users users() {
        return mUsers;
    }

    @Override
    public Groups groups() {
        return mGroups;
    }

    @Override
    public Nodes nodes() {
        return mNodes;
    }

    @Override
    public Shares shares() {
        return mShares;
    }

    // --- Methods to get internal handlers ---

    public DracoonServerImpl getServerImpl() {
        return mServer;
    }

    public DracoonServerSettingsImpl getServerSettingsImpl() {
        return mServerSettings;
    }

    public DracoonServerPoliciesImpl getServerPoliciesImpl() {
        return mServerPolicies;
    }

    public DracoonAccountImpl getAccountImpl() {
        return mAccount;
    }

    public DracoonNodesImpl getNodesImpl() {
        return mNodes;
    }

    public DracoonSharesImpl getSharesImpl() {
        return mShares;
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
