package com.dracoon.sdk;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DracoonAuthTest {

    // --- Tests of configuration for Authorization Code Mode ---

    @Nested
    class CreateAuthConfigAuthorizationCodeModeTests {

        @Test
        void testAuthConfigCorrect() {
            String clientId = "xGcYjy2R";
            String clientSecret = "OCz812WX";
            String authorizationCode = "whdLferi";

            DracoonAuth auth = new DracoonAuth(clientId, clientSecret, authorizationCode);

            assertEquals(DracoonAuth.Mode.AUTHORIZATION_CODE, auth.getMode(),
                    "Authorization mode is incorrect!");
            assertEquals(clientId, auth.getClientId(),
                    "Client ID does not match!");
            assertEquals(clientSecret, auth.getClientSecret(),
                    "Client secret does not match!");
            assertEquals(authorizationCode, auth.getAuthorizationCode(),
                    "Authorization code does not match!");

            assertNull(auth.getAccessToken(), "Access token is not null!");
            assertNull(auth.getRefreshToken(), "Refresh token is not null!");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(clientId,
                    "OCz812WX", "whdLferi"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientSecretValidation(String clientSecret) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth("xGcYjy2R",
                    clientSecret, "whdLferi"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testAuthorizationCodeValidation(String authorizationCode) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth("xGcYjy2R",
                    "OCz812WX", authorizationCode));
        }

    }

    // --- Tests of configuration for Access Token Mode ---

    @Nested
    class CreateAuthConfigAccessTokenModeTests {

        @Test
        void testAuthConfigCorrect() {
            String accessToken = "jZokGKAx05dwm59X";

            DracoonAuth auth = new DracoonAuth(accessToken);

            assertEquals(DracoonAuth.Mode.ACCESS_TOKEN, auth.getMode(),
                    "Authorization mode is incorrect!");
            assertEquals(accessToken, auth.getAccessToken(),
                    "Access token does not match!");

            assertNull(auth.getClientId(), "Client ID is not null!");
            assertNull(auth.getClientSecret(), "Client secret is not null!");
            assertNull(auth.getAuthorizationCode(), "Authorization code is not null!");
            assertNull(auth.getRefreshToken(), "Refresh token is not null!");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testAccessTokenValidation(String accessToken) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(accessToken));
        }

    }

    // --- Tests of configuration for Access and Refresh Token Mode ---

    @Nested
    class CreateAuthConfigAccessRefreshTokenModeTests {

        @Test
        void testAuthConfigCorrect() {
            String clientId = "xGcYjy2R";
            String clientSecret = "OCz812WX";
            String accessToken = "jZokGKAx05dwm59X";
            String refreshToken = "hvOIDqcRrP5ppdyt";

            DracoonAuth auth = new DracoonAuth(clientId, clientSecret, accessToken, refreshToken);

            assertEquals(DracoonAuth.Mode.ACCESS_REFRESH_TOKEN, auth.getMode(),
                    "Authorization mode is incorrect!");
            assertEquals(clientId, auth.getClientId(),
                    "Client ID does not match!");
            assertEquals(clientSecret, auth.getClientSecret(),
                    "Client secret does not match!");
            assertEquals(accessToken, auth.getAccessToken(),
                    "Access token does not match!");
            assertEquals(refreshToken, auth.getRefreshToken(),
                    "Refresh token does not match!");

            assertNull(auth.getAuthorizationCode(), "Authorization code is not null!");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(clientId,
                    "OCz812WX", "jZokGKAx05dwm59X", "hvOIDqcRrP5ppdyt"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientSecretValidation(String clientSecret) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth("xGcYjy2R",
                    clientSecret, "jZokGKAx05dwm59X", "hvOIDqcRrP5ppdyt"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testAccessTokenValidation(String accessToken) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth("xGcYjy2R",
                    "OCz812WX", accessToken, "hvOIDqcRrP5ppdyt"));
        }

        @ParameterizedTest
        @EmptySource
        void testRefreshTokenValidation(String refreshToken) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth("xGcYjy2R",
                    "OCz812WX", "jZokGKAx05dwm59X", refreshToken));
        }

    }

}
