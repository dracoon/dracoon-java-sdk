package com.dracoon.sdk.internal.auth;

import java.io.IOException;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.http.HttpStatus;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class AuthInterceptorImpl implements AuthInterceptor {

    private final AuthHolder mAuthHolder;
    private final AuthTokenRefresher mAuthTokenRefresher;

    public AuthInterceptorImpl(AuthHolder authHolder, AuthTokenRefresher authTokenRefresher) {
        mAuthHolder = authHolder;
        mAuthTokenRefresher = authTokenRefresher;
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
        mAuthTokenRefresher.refresh();

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

}
