package com.dracoon.sdk.internal.oauth;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.HttpHelper;
import com.dracoon.sdk.internal.NullLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthClient {

    private static final String LOG_TAG = OAuthClient.class.getSimpleName();

    private URL mServerUrl;
    private String mClientId;
    private String mClientSecret;

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig;

    private OAuthService mOAuthService;
    private OAuthErrorParser mOAuthErrorParser;
    private HttpHelper mHttpHelper;

    public OAuthClient(URL serverUrl, String clientId, String clientSecret) {
        mServerUrl = serverUrl;
        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public void setHttpConfig(DracoonHttpConfig httpConfig) {
        mHttpConfig = httpConfig;
    }

    // --- Initialization methods ---

    public void init() {
        initOAuthService();
        initOAuthErrorParser();
        initHttpHelper();
    }

    private void initOAuthService() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(mHttpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(mHttpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(mHttpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl.toString())
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mOAuthService = retrofit.create(OAuthService.class);
    }

    private void initOAuthErrorParser() {
        mOAuthErrorParser = new OAuthErrorParser();
        mOAuthErrorParser.setLog(mLog);
    }

    private void initHttpHelper() {
        mHttpHelper = new HttpHelper();
        mHttpHelper.setLog(mLog);
        mHttpHelper.setRetryEnabled(mHttpConfig.isRetryEnabled());
    }

    // --- Methods to retrieve an refresh tokens ---

    public OAuthTokens getAccessToken(String code) throws DracoonNetIOException,
            DracoonApiException {
        String auth = Credentials.basic(mClientId, mClientSecret);

        Call<OAuthTokens> call = mOAuthService.getOAuthToken(auth,
                OAuthConstants.OAuthGrantTypes.AUTHORIZATION_CODE, code);
        Response<OAuthTokens> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiException e = mOAuthErrorParser.parseTokenError(response);
            mLog.d(LOG_TAG, String.format("Retrieval of OAuth tokens failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        return response.body();
    }

    public OAuthTokens refreshAccessToken(String refreshToken) throws DracoonNetIOException,
            DracoonApiException {
        String auth = Credentials.basic(mClientId, mClientSecret);

        Call<OAuthTokens> call = mOAuthService.refreshOAuthToken(auth,
                OAuthConstants.OAuthGrantTypes.REFRESH_TOKEN, refreshToken);
        Response<OAuthTokens> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiException e = mOAuthErrorParser.parseOAuthRefreshError(response);
            mLog.d(LOG_TAG, String.format("Refresh of OAuth tokens failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        return response.body();
    }

}
