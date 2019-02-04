package com.dracoon.sdk.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
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

    private static final String LOG_TAG = DracoonClientImpl.class.getSimpleName();

    private static final long AUTH_EXPIRE_TOLERANCE = 5 * DracoonConstants.SECOND;

    private static class UserAgentInterceptor implements Interceptor {

        private String mUserAgent;

        public UserAgentInterceptor(String userAgent) {
            mUserAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request requestWithUserAgent = chain.request().newBuilder()
                    .header("User-Agent", mUserAgent)
                    .build();

            return chain.proceed(requestWithUserAgent);
        }

    }

    private DracoonAuth mAuth;
    private long mAuthExpireTime = 0L;
    private String mEncryptionPassword;

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig;
    private OkHttpClient mHttpClient;

    private OAuthClient mOAuthClient;

    private DracoonService mDracoonService;
    private DracoonErrorParser mDracoonErrorParser;
    private HttpHelper mHttpHelper;

    private DracoonServerImpl mServer;
    private DracoonAccountImpl mAccount;
    private Users mUsers;
    private Groups mGroups;
    private DracoonNodesImpl mNodes;
    private DracoonSharesImpl mShares;

    private String mApiVersion = null;

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

    // --- Initialization methods ---

    public void init() throws DracoonNetIOException, DracoonApiException {
        initOAuthClient();

        initHttpClient();
        initDracoonService();
        initDracoonErrorParser();
        initHttpHelper();

        mServer = new DracoonServerImpl(this);
        mAccount = new DracoonAccountImpl(this);
        mNodes = new DracoonNodesImpl(this);
        mShares = new DracoonSharesImpl(this);

        assertApiVersionSupported();

        if (mAuth != null) {
            mAccount.pingUser();
        }
    }

    private void initOAuthClient() {
        if (mAuth == null) {
            return;
        }

        mOAuthClient = new OAuthClient(mServerUrl, mAuth.getClientId(), mAuth.getClientSecret());
        mOAuthClient.setLog(mLog);
        mOAuthClient.setHttpConfig(mHttpConfig);
        mOAuthClient.init();
    }

    private void initHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(mHttpConfig.getConnectTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(mHttpConfig.getReadTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(mHttpConfig.getWriteTimeout(), TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
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

    private void initDracoonService() {
        Gson mGson = new GsonBuilder()
                .registerTypeAdapter(Void.class, new TypeAdapter<Void>() {
                    @Override
                    public void write(JsonWriter out, Void value) {

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

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl.toString())
                .client(mHttpClient)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();

        mDracoonService = mRetrofit.create(DracoonService.class);
    }

    private void initDracoonErrorParser() {
        mDracoonErrorParser = new DracoonErrorParser();
        mDracoonErrorParser.setLog(mLog);
    }

    private void initHttpHelper() {
        mHttpHelper = new HttpHelper();
        mHttpHelper.setLog(mLog);
        mHttpHelper.setRetryEnabled(mHttpConfig.isRetryEnabled());
    }

    public void assertApiVersionSupported() throws DracoonNetIOException, DracoonApiException {
        if (mApiVersion != null) {
            return;
        }

        String apiVersion = mServer.getVersion();

        if (!isApiVersionGreaterEqual(DracoonConstants.API_MIN_VERSION)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUPPORTED);
        }

        mApiVersion = apiVersion;
    }

    public boolean isApiVersionGreaterEqual(String minApiVersion)
            throws DracoonNetIOException, DracoonApiException {
        if (mApiVersion == null) {
            mApiVersion = mServer.getVersion();
        }

        String[] av = mApiVersion.split("\\.");
        String[] mav = minApiVersion.split("\\.");

        for (int i = 0; i < 3; i++) {
            int v;
            int mv;

            try {
                v = Integer.valueOf(av[i]);
                mv = Integer.valueOf(mav[i]);
            } catch (NumberFormatException e) {
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

    @Override
    public boolean checkAuth() throws DracoonNetIOException, DracoonApiException {
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

    public DracoonAccountImpl getAccountImpl() {
        return mAccount;
    }

    public DracoonNodesImpl getNodesImpl() {
        return mNodes;
    }

    public DracoonSharesImpl getSharesImpl() {
        return mShares;
    }

    // --- OAuth authorization methods ---

    public synchronized String buildAuthString() throws DracoonApiException, DracoonNetIOException {
        // If no authorization information was provided: Abort
        if (mAuth == null) {
            return null;
        }

        // If expire time is not exceeded: Return current access token
        if (mAuth.getMode().equals(DracoonAuth.Mode.ACCESS_TOKEN) ||
                mAuthExpireTime - AUTH_EXPIRE_TOLERANCE > System.currentTimeMillis()) {
            return DracoonConstants.AUTHORIZATION_TYPE + " " + mAuth.getAccessToken();
        }

        // If mode AUTHORIZATION_CODE: Retrieve new tokens
        if (mAuth.getMode().equals(DracoonAuth.Mode.AUTHORIZATION_CODE)) {
            retrieveAuthTokens();
        // If mode ACCESS_REFRESH_TOKEN: Refresh existing tokens
        } else if (mAuth.getMode().equals(DracoonAuth.Mode.ACCESS_REFRESH_TOKEN)) {
            refreshAuthTokens();
        }

        // Build authorization header string
        return DracoonConstants.AUTHORIZATION_TYPE + " " + mAuth.getAccessToken();
    }

    private void retrieveAuthTokens() throws DracoonApiException, DracoonNetIOException {
        String authorizationCode = mAuth.getAuthorizationCode();

        // Try to retrieve auth tokens
        OAuthTokens tokens = mOAuthClient.retrieveTokens(authorizationCode);

        // Update auth data
        mAuth = new DracoonAuth(mAuth.getClientId(), mAuth.getClientSecret(),
                tokens.accessToken, tokens.refreshToken);
        // Update auth expire time
        mAuthExpireTime = System.currentTimeMillis() + tokens.expiresIn;
    }

    private void refreshAuthTokens() throws DracoonApiException, DracoonNetIOException {
        String accessToken = mAuth.getAccessToken();
        String refreshToken = mAuth.getRefreshToken();

        String newAccessToken;
        long expiresIn;
        String newRefreshToken;

        // TODO: Rework following logic when OAuth server is refactored
        // Try to refresh auth tokens
        try {
            while (true) {
                OAuthTokens tokens = mOAuthClient.refreshTokens(refreshToken);

                newAccessToken = tokens.accessToken;
                expiresIn = tokens.expiresIn * DracoonConstants.SECOND;
                newRefreshToken = tokens.refreshToken;

                // If new auth tokens have been issued: Abort
                if (!Objects.equals(accessToken, newAccessToken)) {
                    mLog.d(LOG_TAG, "A new auth token was created.");
                    break;
                }

                // If auth tokens are still valid: Abort
                if (expiresIn > AUTH_EXPIRE_TOLERANCE) {
                    mLog.d(LOG_TAG, "Auth token is still valid.");
                    break;
                }

                // Sleep some time before next try
                mLog.d(LOG_TAG, "Old auth token is still valid. Trying again in 1 second.");
                Thread.sleep(DracoonConstants.SECOND);
            }
        } catch (InterruptedException e) {
            return;
        }

        // Update auth data
        if (newRefreshToken != null) {
            mAuth = new DracoonAuth(mAuth.getClientId(), mAuth.getClientSecret(),
                    newAccessToken, newRefreshToken);
        } else {
            mAuth = new DracoonAuth(newAccessToken);
        }

        // Update auth expire time
        mAuthExpireTime = System.currentTimeMillis() + expiresIn;
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
