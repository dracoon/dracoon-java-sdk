package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
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

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DracoonClientImpl extends DracoonClient {

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

    private String mOAuthAccessToken;
    private String mOAuthRefreshToken;
    private long mOAuthLastRefreshTime;

    public DracoonClientImpl(URL serverUrl) {
        super(serverUrl);
    }

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
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

    public void init() {
        initHttpClient();
        initOAuthClient();
        initDracoonService();
        initDracoonErrorParser();
        initHttpHelper();

        mServer = new DracoonServerImpl(this);
        mAccount = new DracoonAccountImpl(this);
        mNodes = new DracoonNodesImpl(this);
        mShares = new DracoonSharesImpl(this);
    }

    private void initHttpClient() {
        mHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new UserAgentInterceptor(mHttpConfig.getUserAgent()))
                .connectTimeout(mHttpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(mHttpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(mHttpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    private void initOAuthClient() {
        mOAuthClient = new OAuthClient(mServerUrl, mAuth.getClientId(), mAuth.getClientSecret());
        mOAuthClient.setLog(mLog);
        mOAuthClient.setHttpConfig(mHttpConfig);
        mOAuthClient.init();
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

    /// --- Methods to get internal handlers ---

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

    public String buildAuthString() throws DracoonApiException, DracoonNetIOException {
        if (mOAuthAccessToken == null) {
            retrieveOAuthTokens();
        }
        long nextRefreshTime = mOAuthLastRefreshTime +
                DracoonConstants.AUTHORIZATION_REFRESH_INTERVAL * 1000;
        long currentTime = System.currentTimeMillis();
        if (nextRefreshTime < currentTime) {
            refreshOAuthTokens();
        }
        return DracoonConstants.AUTHORIZATION_TYPE + " " + mOAuthAccessToken;
    }

    private void retrieveOAuthTokens() throws DracoonNetIOException, DracoonApiException {
        if (mAuth != null) {
            switch (mAuth.getMode()) {
                case ACCESS_TOKEN:
                    mOAuthAccessToken = mAuth.getAccessToken();
                    break;
                case AUTHORIZATION_CODE:
                    OAuthTokens tokens = mOAuthClient.getAccessToken(mAuth.getAuthorizationCode());
                    mOAuthAccessToken = tokens.accessToken;
                    mOAuthRefreshToken = tokens.refreshToken;
                    break;
                case ACCESS_REFRESH_TOKEN:
                    mOAuthAccessToken = mAuth.getAccessToken();
                    mOAuthRefreshToken = mAuth.getRefreshToken();
                    break;
                default:
            }
        } else {
            mOAuthAccessToken = "";
        }

        mOAuthLastRefreshTime = System.currentTimeMillis();
    }

    private void refreshOAuthTokens() throws DracoonNetIOException, DracoonApiException {
        if (mOAuthRefreshToken != null) {
            OAuthTokens tokens = mOAuthClient.refreshAccessToken(mOAuthRefreshToken);
            mOAuthAccessToken = tokens.accessToken;
            mOAuthRefreshToken = tokens.refreshToken;
        }
        mOAuthLastRefreshTime = System.currentTimeMillis();
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
