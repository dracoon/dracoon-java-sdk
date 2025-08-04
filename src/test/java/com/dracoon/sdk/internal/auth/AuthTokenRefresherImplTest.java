package com.dracoon.sdk.internal.auth;

import java.io.IOException;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenRefresherImplTest {

    @Mock
    protected AuthHolder mAuthHolder;
    @Mock
    protected OAuthClient mOAuthClient;

    private AuthTokenRefresherImpl mRefresher;

    @BeforeEach
    void setup() {
        mRefresher = new AuthTokenRefresherImpl(mOAuthClient, mAuthHolder);
    }

    @Test
    void testRefreshWithNoAuth() throws Exception {
        // Setup mocking
        when(mAuthHolder.get()).thenReturn(null);

        // Execute method to test
        mRefresher.refresh();

        // Verify calls
        verify(mAuthHolder, times(0)).set(any());
    }

    @Test
    void testRefresh() throws Exception {
        // Create data
        DracoonAuth auth = buildAuth();
        OAuthTokens oAuthTokens = buildOAuthTokens();

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(auth);
        when(mOAuthClient.refreshTokens(any(), any(), any())).thenReturn(oAuthTokens);

        // Execute method to test
        mRefresher.refresh();

        // Verify calls
        verify(mOAuthClient)
                .refreshTokens(auth.getClientId(), auth.getClientSecret(), auth.getRefreshToken());
        verify(mAuthHolder)
                .set(assertArg(a -> {
                    assertEquals(auth.getClientId(), a.getClientId());
                    assertEquals(auth.getClientSecret(), a.getClientSecret());
                    assertEquals(oAuthTokens.accessToken, a.getAccessToken());
                    assertEquals(oAuthTokens.refreshToken, a.getRefreshToken());
                }));
    }

    @Test
    void testRefreshNotOverdue() throws Exception {
        // Setup mocking
        when(mAuthHolder.get()).thenReturn(buildAuth());

        // Setup refresher
        mRefresher.setLastRefreshTime(System.currentTimeMillis() - 14500L);

        // Execute method to test
        mRefresher.refresh();

        // Verify calls
        verify(mAuthHolder, times(0)).set(any());
    }

    @Test
    void testRefreshError() throws Exception {
        // Setup mocking
        when(mAuthHolder.get()).thenReturn(buildAuth());
        when(mOAuthClient.refreshTokens(any(), any(), any())).thenThrow(new DracoonApiException());

        // Execute method to test
        IOException thrown1 = assertThrows(IOException.class, mRefresher::refresh);
        IOException thrown2 = assertThrows(IOException.class, mRefresher::refresh);

        // Assert exceptions match
        assertEquals(thrown1, thrown2);

        // Verify calls
        verify(mOAuthClient, times(1)).refreshTokens(any(), any(), any());
        verify(mAuthHolder, times(0)).set(any());
    }

    private static DracoonAuth buildAuth() {
        return new DracoonAuth("CLIENT-ID", "CLIENT-SECRET", "access-token", "refresh-token");
    }

    private static OAuthTokens buildOAuthTokens() {
        OAuthTokens tokens = new OAuthTokens();
        tokens.accessToken = "new-access-token";
        tokens.refreshToken = "new-refresh-token";
        return tokens;
    }

}
