package com.dracoon.sdk.internal.oauth;

import java.util.stream.Stream;

import com.dracoon.sdk.BaseHttpTest;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OAuthClientTest extends BaseHttpTest {

    private static final String CLIENT_ID = "qPYluU3S";
    private static final String CLIENT_SECRET = "rmOU0GVL";
    private static final String USER_AGENT = "Java-SDK-Unit-Test";

    private OAuthClient mOAuthClient;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        DracoonHttpConfig httpConfig = new DracoonHttpConfig();
        httpConfig.setUserAgent(USER_AGENT);

        mOAuthClient = new OAuthClient(mServerUrl, CLIENT_ID, CLIENT_SECRET);
        mOAuthClient.setHttpConfig(httpConfig);
        mOAuthClient.init();
    }

    // --- Tests for retrieving tokens ---

    @Nested
    class RetrieveTokensTests {

        private final String DATA_PATH = "/oauth/retrieve_tokens/";

        @Test
        void testRequestValid() throws Exception {
            retrieveTokens();

            checkRequest(DATA_PATH + "retrieve_tokens_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            OAuthTokens tokens = retrieveTokens();

            assertNotNull(tokens);
            assertEquals("bearer", tokens.tokenType);
            assertEquals("L3O1eDsLxDgJhLaQbzOSmm8xr48mxPoW", tokens.accessToken);
            assertEquals("DhEg0fM0ehrlMV8RzXhpgSsSQJvmb6jM", tokens.refreshToken);
            assertEquals(43200, tokens.expiresIn);
            assertEquals("all", tokens.scope);
        }

        private OAuthTokens retrieveTokens() throws Exception {
            enqueueResponse(DATA_PATH + "retrieve_tokens_response.json");
            return mOAuthClient.retrieveTokens("4lHrris4kqh8zAZgcLcb");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.oauth.OAuthClientTest#"+
                "createTestRetrieveTokenErrorHandlingArguments")
        void testErrorHandling(String resourceName, DracoonApiCode code) {
            enqueueResponse(resourceName);

            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mOAuthClient.retrieveTokens("4lHrris4kqh8zAZgcLcb"));
            assertEquals(code, thrown.getCode());
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestRetrieveTokenErrorHandlingArguments() {
        return Stream.of(
                Arguments.of("/oauth/common/invalid_request_response.json",
                        DracoonApiCode.AUTH_OAUTH_TOKEN_REQUEST_INVALID),
                Arguments.of("/oauth/common/invalid_client_response.json",
                        DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED),
                Arguments.of("/oauth/common/invalid_grant_response.json",
                        DracoonApiCode.AUTH_OAUTH_TOKEN_CODE_INVALID),
                Arguments.of("/oauth/common/unauthorized_client_response.json",
                        DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED),
                Arguments.of("/oauth/common/internal_error_response.json",
                        DracoonApiCode.AUTH_UNKNOWN_ERROR)
        );
    }

    // --- Tests for refreshing tokens ---

    @Nested
    class RefreshTokensTests {

        private final String DATA_PATH = "/oauth/refresh_tokens/";

        @Test
        void testRequestValid() throws Exception {
            refreshTokens();

            checkRequest(DATA_PATH + "refresh_tokens_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            OAuthTokens tokens = refreshTokens();

            assertNotNull(tokens);
            assertEquals("bearer", tokens.tokenType);
            assertEquals("2nU9Ea5dh1lPQ6RiffqMlSAXa5DTIgXE", tokens.accessToken);
            assertEquals("vOOLoh1BlBrxq0UKuzqitp9eBd5hjEaW", tokens.refreshToken);
            assertEquals(43200, tokens.expiresIn);
            assertEquals("all", tokens.scope);
        }

        private OAuthTokens refreshTokens() throws Exception {
            enqueueResponse(DATA_PATH + "refresh_tokens_response.json");
            return mOAuthClient.refreshTokens("G6626ZHERgVTTpA2WuGxg3EKnGKoAIgh");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.oauth.OAuthClientTest#"+
                "createTestRefreshTokensErrorHandlingArguments")
        void testErrorHandling(String resourceName, DracoonApiCode code) {
            enqueueResponse(resourceName);

            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mOAuthClient.refreshTokens("G6626ZHERgVTTpA2WuGxg3EKnGKoAIgh"));
            assertEquals(code, thrown.getCode());
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestRefreshTokensErrorHandlingArguments() {
        return Stream.of(
                Arguments.of("/oauth/common/invalid_request_response.json",
                        DracoonApiCode.AUTH_OAUTH_REFRESH_REQUEST_INVALID),
                Arguments.of("/oauth/common/invalid_client_response.json",
                        DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED),
                Arguments.of("/oauth/common/invalid_grant_response.json",
                        DracoonApiCode.AUTH_OAUTH_REFRESH_TOKEN_INVALID),
                Arguments.of("/oauth/common/unauthorized_client_response.json",
                        DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED),
                Arguments.of("/oauth/common/internal_error_response.json",
                        DracoonApiCode.AUTH_UNKNOWN_ERROR)
        );
    }

    // --- Tests for revoking tokens ---

    @Nested
    class RevokeTokensTests {

        private final String DATA_PATH = "/oauth/revoke_tokens/";

        @Test
        void testAccessTokenRequestValid() throws Exception {
            enqueueResponse(DATA_PATH + "revoke_access_token_response.json");

            mOAuthClient.revokeAccessToken("1j9PZ7OWy8IO9sDTf7f1koGPCvfwg083");

            checkRequest(DATA_PATH + "revoke_access_token_request.json");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.oauth.OAuthClientTest#"+
                "createTestRevokeTokensErrorHandlingArguments")
        void testRevokeAccessTokenErrorHandling(String resourceName, DracoonApiCode code) {
            enqueueResponse(resourceName);

            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mOAuthClient.revokeAccessToken("1j9PZ7OWy8IO9sDTf7f1koGPCvfwg083"));
            assertEquals(code, thrown.getCode());
        }

        @Test
        void testRefreshTokenRequestValid() throws Exception {
            enqueueResponse(DATA_PATH + "revoke_refresh_token_response.json");

            mOAuthClient.revokeRefreshToken("TQeXE4EZZypxX8pAsz91JEDg7sNvbLiR");

            checkRequest(DATA_PATH + "revoke_refresh_token_request.json");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.oauth.OAuthClientTest#"+
                "createTestRevokeTokensErrorHandlingArguments")
        void testRevokeRefreshTokenErrorHandling(String resourceName, DracoonApiCode code) {
            enqueueResponse(resourceName);

            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mOAuthClient.revokeRefreshToken("TQeXE4EZZypxX8pAsz91JEDg7sNvbLiR"));
            assertEquals(code, thrown.getCode());
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestRevokeTokensErrorHandlingArguments() {
        return Stream.of(
                Arguments.of("/oauth/common/invalid_request_response.json",
                        DracoonApiCode.AUTH_OAUTH_REVOKE_REQUEST_INVALID),
                Arguments.of("/oauth/common/unauthorized_client_response.json",
                        DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED),
                Arguments.of("/oauth/common/internal_error_response.json",
                        DracoonApiCode.AUTH_UNKNOWN_ERROR)
        );
    }

}
