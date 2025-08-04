package com.dracoon.sdk.internal.auth;

import java.util.List;

import com.dracoon.sdk.DracoonAuth;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorImplTest {

    @Mock
    protected AuthHolder mAuthHolder;
    @Mock
    protected AuthTokenRefresher mAuthTokenRefresher;

    @Mock
    protected Interceptor.Chain mChain;

    private AuthInterceptor mInterceptor;

    @BeforeEach
    void setup() {
        mInterceptor = new AuthInterceptorImpl(mAuthHolder, mAuthTokenRefresher);
    }

    @Test
    void testInterceptPublicRequest() throws Exception {
        // Create data
        Request request = buildPublicRequest();
        Response expectedOkResponse = buildOkResponse(request);

        // Setup mocking
        when(mChain.request()).thenReturn(request);
        when(mChain.proceed(any())).thenReturn(expectedOkResponse);

        // Execute method to test
        Response actualResponse = mInterceptor.intercept(mChain);

        // Assert response is valid
        assertEquals(expectedOkResponse, actualResponse);

        // Verify calls
        verifyProceedCalls(request);
    }

    @Test
    void testInterceptWithNoAccessToken() throws Exception {
        // Create data
        Request request = buildProtectedRequest();
        Response expectedOkResponse = buildOkResponse(request);

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(null);
        when(mChain.request()).thenReturn(request);
        when(mChain.proceed(any())).thenReturn(expectedOkResponse);

        // Execute method to test
        Response actualResponse = mInterceptor.intercept(mChain);

        // Assert response is valid
        assertEquals(expectedOkResponse, actualResponse);

        // Verify calls
        verifyProceedCalls(request);
    }

    @Test
    void testInterceptWithValidAccessToken() throws Exception {
        // Create data
        DracoonAuth auth = buildAuth();
        Request request = buildProtectedRequest();
        Request requestWithAuthHeader = buildProtectedRequestWithAuthHeader(auth);
        Response expectedOkResponse = buildOkResponse(requestWithAuthHeader);

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(auth);
        when(mChain.request()).thenReturn(request);
        when(mChain.proceed(any())).thenReturn(expectedOkResponse);

        // Execute method to test
        Response actualResponse = mInterceptor.intercept(mChain);

        // Assert response is valid
        assertEquals(expectedOkResponse, actualResponse);

        // Verify calls
        verifyProceedCalls(requestWithAuthHeader);
    }

    @Test
    void testInterceptWithInvalidAccessTokenAndNoRefreshToken() throws Exception {
        // Create data
        DracoonAuth auth = buildAuth();
        Request request = buildProtectedRequest();
        Request requestWithAuthHeader = buildProtectedRequestWithAuthHeader(auth);
        Response expectedUnauthorizedResponse = buildUnauthorizedResponse(requestWithAuthHeader);

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(auth);
        when(mChain.request()).thenReturn(request);
        when(mChain.proceed(any())).thenReturn(expectedUnauthorizedResponse);

        // Execute method to test
        Response actualResponse = mInterceptor.intercept(mChain);

        // Assert response is valid
        assertEquals(expectedUnauthorizedResponse, actualResponse);

        // Verify calls
        verifyProceedCalls(requestWithAuthHeader);
    }

    @Test
    void testInterceptWithInvalidAccessTokenAndValidRefreshToken() throws Exception {
        // Create data
        DracoonAuth auth1 = buildAuth("access_token_1", "refresh_token_1");
        DracoonAuth auth2 = buildAuth("access_token_2", "refresh_token_2");
        Request request = buildProtectedRequest();
        Request requestWithAuth1Header = buildProtectedRequestWithAuthHeader(auth1);
        Request requestWithAuth2Header = buildProtectedRequestWithAuthHeader(auth2);
        Response expectedUnauthorizedResponse = buildUnauthorizedResponse(requestWithAuth1Header);
        Response expectedOkResponse = buildOkResponse(requestWithAuth2Header);

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(auth1, auth2);
        when(mChain.request()).thenReturn(request);
        when(mChain.proceed(any())).thenReturn(expectedUnauthorizedResponse, expectedOkResponse);

        // Execute method to test
        Response actualResponse = mInterceptor.intercept(mChain);

        // Assert response is valid
        assertEquals(expectedOkResponse, actualResponse);

        // Verify calls
        verifyRefreshCall();
        verifyProceedCalls(requestWithAuth1Header, requestWithAuth2Header);
    }

    private void verifyRefreshCall() throws Exception {
        verify(mAuthTokenRefresher).refresh();
    }

    private void verifyProceedCalls(Request... requests) throws Exception {
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mChain, times(requests.length)).proceed(captor.capture());
        List<Request> capturedRequests = captor.getAllValues();
        assertEquals(requests.length, capturedRequests.size());
        for (int i = 0; i < requests.length; i++) {
            assertRequestEquals(requests[i], capturedRequests.get(i));
        }
    }

    private static void assertRequestEquals(Request expectedRequest, Request actualRequest) {
        assertEquals(expectedRequest.method(), actualRequest.method());
        assertEquals(expectedRequest.url(), actualRequest.url());
        assertEquals(expectedRequest.headers().toString(), actualRequest.headers().toString());
    }

    private static DracoonAuth buildAuth() {
        return new DracoonAuth("access-token");
    }

    private static DracoonAuth buildAuth(String accessToken, String refreshToken) {
        return new DracoonAuth("CLIENT-ID", "CLIENT-SECRET", accessToken, refreshToken);
    }

    private static Request buildPublicRequest() {
        return new Request.Builder()
                .url("https://dracoon.team/api/v4/public/software/version")
                .build();
    }

    private static Request buildProtectedRequest() {
        return new Request.Builder()
                .url("https://dracoon.team/api/v4/user/account")
                .build();
    }

    private static Request buildProtectedRequestWithAuthHeader(DracoonAuth auth) {
        return new Request.Builder()
                .url("https://dracoon.team/api/v4/user/account")
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .build();
    }

    private static Response buildOkResponse(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("OK")
                .build();
    }

    private static Response buildUnauthorizedResponse(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(401)
                .message("Unauthorized")
                .build();
    }

}
