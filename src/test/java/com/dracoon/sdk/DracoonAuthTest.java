package com.dracoon.sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DracoonAuthTest {

    private DracoonAuth mAuth;

    // --- Tests of configuration for Authorization Code Mode ---

    @Nested
    class CreateAuthConfigAuthorizationCodeModeTests {

        private final String CLIENT_ID = "xGcYjy2R";
        private final String CLIENT_SECRET = "OCz812WX";
        private final String AUTHORIZATION_CODE = "whdLferi";

        @BeforeEach
        void setup() {
             mAuth = new DracoonAuth(CLIENT_ID, CLIENT_SECRET, AUTHORIZATION_CODE);
        }

        @Test
        void testModeCorrect() {
            assertEquals(DracoonAuth.Mode.AUTHORIZATION_CODE, mAuth.getMode(),
                    "Authorization mode is incorrect!");
        }

        @Test
        void testClientIdCorrect() {
            assertEquals(CLIENT_ID, mAuth.getClientId(), "Client ID does not match!");
        }

        @Test
        void testClientSecretCorrect() {
            assertEquals(CLIENT_SECRET, mAuth.getClientSecret(), "Client secret does not match!");
        }

        @Test
        void testAuthorizationCodeCorrect() {
            assertEquals(AUTHORIZATION_CODE, mAuth.getAuthorizationCode(),
                    "Authorization code does not match!");
        }

        @Test
        void testAccessTokenCorrect() {
            assertNull(mAuth.getAccessToken(), "Access token is not null!");
        }

        @Test
        void testRefreshTokenCorrect() {
            assertNull(mAuth.getRefreshToken(), "Refresh token is not null!");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(clientId,
                    CLIENT_SECRET, AUTHORIZATION_CODE));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientSecretValidation(String clientSecret) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(CLIENT_ID,
                    clientSecret, AUTHORIZATION_CODE));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testAuthorizationCodeValidation(String authorizationCode) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(CLIENT_ID,
                    CLIENT_SECRET, authorizationCode));
        }

    }

    // --- Tests of configuration for Access Token Mode ---

    @Nested
    class CreateAuthConfigAccessTokenModeTests {

        private final String ACCESS_TOKEN = "jZokGKAx05dwm59X";

        @BeforeEach
        void setup() {
            mAuth = new DracoonAuth(ACCESS_TOKEN);
        }

        @Test
        void testModeCorrect() {
            assertEquals(DracoonAuth.Mode.ACCESS_TOKEN, mAuth.getMode(),
                    "Authorization mode is incorrect!");
        }

        @Test
        void testAccessTokenCorrect() {
            assertEquals(ACCESS_TOKEN, mAuth.getAccessToken(), "Access token does not match!");
        }

        @Test
        void testClientIdCorrect() {
            assertNull(mAuth.getClientId(), "Client ID is not null!");
        }

        @Test
        void testClientSecretCorrect() {
            assertNull(mAuth.getClientSecret(), "Client secret is not null!");
        }

        @Test
        void testAuthorizationCodeCorrect() {
            assertNull(mAuth.getAuthorizationCode(), "Authorization code is not null!");
        }

        @Test
        void testRefreshTokenCorrect() {
            assertNull(mAuth.getRefreshToken(), "Refresh token is not null!");
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

        private final String CLIENT_ID = "xGcYjy2R";
        private final String CLIENT_SECRET = "OCz812WX";
        private final String ACCESS_TOKEN = "jZokGKAx05dwm59X";
        private final String REFRESH_TOKEN = "hvOIDqcRrP5ppdyt";

        @BeforeEach
        void setup() {
            mAuth = new DracoonAuth(CLIENT_ID, CLIENT_SECRET, ACCESS_TOKEN, REFRESH_TOKEN);
        }

        @Test
        void testModeCorrect() {
            assertEquals(DracoonAuth.Mode.ACCESS_REFRESH_TOKEN, mAuth.getMode(),
                    "Authorization mode is incorrect!");
        }

        @Test
        void testClientIdCorrect() {
            assertEquals(CLIENT_ID, mAuth.getClientId(), "Client ID does not match!");
        }

        @Test
        void testClientSecretCorrect() {
            assertEquals(CLIENT_SECRET, mAuth.getClientSecret(), "Client secret does not match!");
        }

        @Test
        void testAccessTokenCorrect() {
            assertEquals(ACCESS_TOKEN, mAuth.getAccessToken(), "Access token does not match!");
        }

        @Test
        void testRefreshTokenCorrect() {
            assertEquals(REFRESH_TOKEN, mAuth.getRefreshToken(), "Refresh token does not match!");
        }

        @Test
        void testAuthorizationCodeCorrect() {
            assertNull(mAuth.getAuthorizationCode(), "Authorization code is not null!");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(clientId,
                    CLIENT_SECRET, ACCESS_TOKEN, REFRESH_TOKEN));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientSecretValidation(String clientSecret) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(CLIENT_ID,
                    clientSecret, ACCESS_TOKEN, REFRESH_TOKEN));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testAccessTokenValidation(String accessToken) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(CLIENT_ID,
                    CLIENT_SECRET, accessToken, REFRESH_TOKEN));
        }

        @ParameterizedTest
        @EmptySource
        void testRefreshTokenValidation(String refreshToken) {
            assertThrows(IllegalArgumentException.class, () -> new DracoonAuth(CLIENT_ID,
                    CLIENT_SECRET, ACCESS_TOKEN, refreshToken));
        }

    }

}
