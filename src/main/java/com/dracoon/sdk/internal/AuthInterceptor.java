package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.http.HttpStatus;
import com.dracoon.sdk.internal.http.InterceptedIOException;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class AuthInterceptor implements Interceptor {

    private static final long AUTH_REFRESH_SKIP_INTERVAL = 15 * DracoonConstants.SECOND;

    private final OAuthClient mOAuthClient;
    private final AuthHolder mAuthHolder;

    private long mLastRefreshTime = 0L;
    private InterceptedIOException mLastRefreshException = null;

    public AuthInterceptor(OAuthClient oAuthClient, AuthHolder authHolder) {
        mOAuthClient = oAuthClient;
        mAuthHolder = authHolder;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        // Get request
        Request request = chain.request();

        // If no authorization is needed: Send unchanged request
        if (isPublicRequest(request)) {
            return chain.proceed(request);
        }
        // If no authorization data was provided: Send unchanged request
        if (!hasAccessToken()) {
            return chain.proceed(request);
        }

        // Try to send request
        Response response = chain.proceed(addAuthorizationHeader(request));

        // If request was successful: Return response
        if (isSuccessfulResponse(response)) {
            return response;
        }
        // If no refresh token was provided: Return response
        if (!hasRefreshToken()) {
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

    private boolean hasAccessToken() {
        DracoonAuth auth = mAuthHolder.get();
        return auth != null && auth.getAccessToken() != null;
    }

    private boolean hasRefreshToken() {
        DracoonAuth auth = mAuthHolder.get();
        return auth != null && auth.getRefreshToken() != null;
    }

    private boolean isSuccessfulResponse(Response response) {
        return response.code() != HttpStatus.UNAUTHORIZED.getNumber();
    }

    private Request addAuthorizationHeader(Request request) {
        DracoonAuth auth = mAuthHolder.get();
        return request.newBuilder().header(DracoonConstants.AUTHORIZATION_HEADER,
                DracoonConstants.AUTHORIZATION_TYPE + " " + auth.getAccessToken()).build();
    }

    private synchronized void refreshAuthTokens() throws InterceptedIOException {
        // If refresh is not overdue: Abort
        if (mLastRefreshTime + AUTH_REFRESH_SKIP_INTERVAL > System.currentTimeMillis()) {
            if (mLastRefreshException != null) {
                throw mLastRefreshException;
            }
            return;
        }

        // Get current auth
        DracoonAuth auth = mAuthHolder.get();

        // Try to refresh tokens
        OAuthTokens tokens;
        try {
            tokens = mOAuthClient.refreshTokens(auth.getClientId(), auth.getClientSecret(),
                    auth.getRefreshToken());
            mLastRefreshTime = System.currentTimeMillis();
            mLastRefreshException = null;
        } catch (DracoonNetIOException e) {
            throw new InterceptedIOException(e);
        } catch (DracoonApiException e) {
            mLastRefreshTime = System.currentTimeMillis();
            mLastRefreshException = new InterceptedIOException(e);
            throw mLastRefreshException;
        }

        // Update auth
        auth = new DracoonAuth(auth.getClientId(), auth.getClientSecret(), tokens.accessToken,
                tokens.refreshToken);
        mAuthHolder.set(auth);
    }

}
