package com.dracoon.sdk.internal.auth;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenRetrieverImplTest {

    @Mock
    protected AuthHolder mAuthHolder;
    @Mock
    protected OAuthClient mOAuthClient;

    private AuthTokenRetrieverImpl mRetriever;

    @BeforeEach
    void setup() {
        mRetriever = new AuthTokenRetrieverImpl(mOAuthClient, mAuthHolder);
    }

    @Test
    void testRetrieveWithNoAuth() throws Exception {
        // Setup mocking
        when(mAuthHolder.get()).thenReturn(null);

        // Execute method to test
        mRetriever.retrieve();

        // Verify calls
        verify(mAuthHolder, times(0)).set(any());
    }

    @Test
    void testRetrieve() throws Exception {
        // Create data
        DracoonAuth auth = buildAuth();
        OAuthTokens oAuthTokens = buildOAuthTokens();

        // Setup mocking
        when(mAuthHolder.get()).thenReturn(auth);
        when(mOAuthClient.retrieveTokens(any(), any(), any())).thenReturn(oAuthTokens);

        // Execute method to test
        mRetriever.retrieve();

        // Verify calls
        verify(mOAuthClient)
                .retrieveTokens(auth.getClientId(), auth.getClientSecret(), auth.getAuthorizationCode());
        verify(mAuthHolder)
                .set(assertArg(a -> {
                    assertEquals(auth.getClientId(), a.getClientId());
                    assertEquals(auth.getClientSecret(), a.getClientSecret());
                    assertEquals(oAuthTokens.accessToken, a.getAccessToken());
                    assertEquals(oAuthTokens.refreshToken, a.getRefreshToken());
                }));
    }

    private static DracoonAuth buildAuth() {
        return new DracoonAuth("CLIENT-ID", "CLIENT-SECRET", "auth-code");
    }

    private static OAuthTokens buildOAuthTokens() {
        OAuthTokens tokens = new OAuthTokens();
        tokens.accessToken = "new-access-token";
        tokens.refreshToken = "new-refresh-token";
        return tokens;
    }

}
