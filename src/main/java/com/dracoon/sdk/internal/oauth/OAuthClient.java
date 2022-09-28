package com.dracoon.sdk.internal.oauth;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.HttpHelper;
import com.dracoon.sdk.internal.NullLog;
import com.dracoon.sdk.internal.UserAgentInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthClient {

    private static final String LOG_TAG = OAuthClient.class.getSimpleName();

    private final URL mServerUrl;
    private final String mClientId;
    private final String mClientSecret;

    private Log mLog = new NullLog();
    private DracoonHttpConfig mHttpConfig;
    private OkHttpClient mHttpClient;

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
        initHttpClient();
        initOAuthService();
        initOAuthErrorParser();
        initHttpHelper();
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

    private void initOAuthService() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl.toString())
                .client(mHttpClient)
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
        mHttpHelper.setRateLimitingEnabled(mHttpConfig.isRateLimitingEnabled());
        mHttpHelper.init();
    }

    // --- Methods to retrieve, refresh and revoke tokens ---

    public OAuthTokens retrieveTokens(String code) throws DracoonNetIOException,
            DracoonApiException {
        String auth = Credentials.basic(mClientId, mClientSecret);

        mLog.i(LOG_TAG, "Trying to retrieve OAuth tokens ...");

        Call<OAuthTokens> call = mOAuthService.getOAuthToken(auth,
                OAuthConstants.OAuthGrantTypes.AUTHORIZATION_CODE, code);
        Response<OAuthTokens> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiException e = mOAuthErrorParser.parseTokenError(response);
            mLog.d(LOG_TAG, String.format("Retrieval of OAuth tokens failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        mLog.i(LOG_TAG, "Successfully retrieved OAuth tokens.");

        return response.body();
    }

    public OAuthTokens refreshTokens(String refreshToken) throws DracoonNetIOException,
            DracoonApiException {
        String auth = Credentials.basic(mClientId, mClientSecret);

        mLog.i(LOG_TAG, "Trying to refresh OAuth tokens ...");

        Call<OAuthTokens> call = mOAuthService.refreshOAuthToken(auth,
                OAuthConstants.OAuthGrantTypes.REFRESH_TOKEN, refreshToken);
        Response<OAuthTokens> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiException e = mOAuthErrorParser.parseOAuthRefreshError(response);
            mLog.d(LOG_TAG, String.format("Refresh of OAuth tokens failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        mLog.i(LOG_TAG, "Successfully refreshed OAuth tokens.");

        return response.body();
    }

    public void revokeAccessToken(String accessToken) throws DracoonNetIOException,
            DracoonApiException {
        mLog.i(LOG_TAG, "Trying to revoke OAuth access token ...");

        try {
            revokeToken(OAuthConstants.OAuthTokenTypes.ACCESS_TOKEN, accessToken);
        } catch (DracoonApiException e) {
            mLog.d(LOG_TAG, String.format("Revocation of OAuth access token failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        mLog.i(LOG_TAG, "Successfully revoked OAuth access token.");
    }

    public void revokeRefreshToken(String refreshToken) throws DracoonNetIOException,
            DracoonApiException {
        mLog.i(LOG_TAG, "Trying to revoke OAuth refresh token ...");

        try {
            revokeToken(OAuthConstants.OAuthTokenTypes.REFRESH_TOKEN, refreshToken);
        } catch (DracoonApiException e) {
            mLog.d(LOG_TAG, String.format("Revocation of OAuth refresh token failed with '%s'!",
                    e.getCode().name()));
            throw e;
        }

        mLog.i(LOG_TAG, "Successfully revoked OAuth refresh token.");
    }

    private void revokeToken(String tokenType, String token) throws DracoonNetIOException,
            DracoonApiException {
        String auth = Credentials.basic(mClientId, mClientSecret);

        Call<Void> call = mOAuthService.revokeOAuthToken(auth, tokenType, token);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw mOAuthErrorParser.parseOAuthRevokeError(response);
        }
    }

}
