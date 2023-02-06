package com.dracoon.sdk;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OAuthHelperTest {

    private static final URL sDefaultServerUrl;
    private static final URI sDefaultRedirectUri;

    static {
        try {
            sDefaultServerUrl = new URL("https://dracoon.team");
            sDefaultRedirectUri = URI.create("http://localhost:10000");
        } catch (Exception e) {
            throw new RuntimeException("Invalid test data!");
        }
    }

    // --- Tests for authorization URL creation ---

    @Nested
    class CreateAuthorizationUrlTests {

        @Test
        void testAuthorizationUrlCorrect() {
            String url = OAuthHelper.createAuthorizationUrl(sDefaultServerUrl, "prgj5uzb",
                    "b0fQ8iGQUONL4BHt");

            assertEquals("https://dracoon.team/oauth/authorize?"
                    + "response_type=code&"
                    + "client_id=prgj5uzb&"
                    + "state=b0fQ8iGQUONL4BHt",
                    url);
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.OAuthHelperTest#createTestServerUrlValidationArguments")
        void testServerUrlValidation(URL serverUrl) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    serverUrl, "prgj", "b0fQ8iGQUONL4BHt"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    sDefaultServerUrl, clientId, "b0fQ8iGQ"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testStateValidation(String state) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    sDefaultServerUrl, "prgj", state));
        }

    }

    @Nested
    class CreateAuthorizationUrlWithRedirectUriTests {

        @Test
        void testAuthorizationUrlCorrect() {
            String url = OAuthHelper.createAuthorizationUrl(sDefaultServerUrl, "prgj5uzb",
                    "b0fQ8iGQUONL4BHt", sDefaultRedirectUri);

            assertEquals("https://dracoon.team/oauth/authorize?"
                    + "response_type=code&"
                    + "client_id=prgj5uzb&"
                    + "state=b0fQ8iGQUONL4BHt&"
                    + "redirect_uri=http%3A%2F%2Flocalhost%3A10000",
                    url);
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.OAuthHelperTest#createTestServerUrlValidationArguments")
        void testServerUrlValidation(URL serverUrl) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    serverUrl, "prgj", "b0fQ8iGQUONL4BHt", sDefaultRedirectUri));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testClientIdValidation(String clientId) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    sDefaultServerUrl, clientId, "b0fQ8iGQ", sDefaultRedirectUri));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void testStateValidation(String state) {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    sDefaultServerUrl, "prgj", state, sDefaultRedirectUri));
        }

        @Test
        void testRedirectUriValidation() {
            assertThrows(IllegalArgumentException.class, () -> OAuthHelper.createAuthorizationUrl(
                    sDefaultServerUrl, "prgj", "b0fQ8iGQ", null));
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestServerUrlValidationArguments()
            throws MalformedURLException {
        return Stream.of(
                Arguments.of((URL) null),
                Arguments.of(new URL("ftp://dracoon.team")),
                Arguments.of(new URL("https://user@dracoon.team")),
                Arguments.of(new URL("https://dracoon.team/test")),
                Arguments.of(new URL("https://dracoon.team?test=1"))
        );
    }

    // --- Tests for redirect URI parsing ---

    @Nested
    class ExtractAuthorizationStateFromUriTests {

        @Test
        void testAuthorizationStateCorrect() throws URISyntaxException, DracoonApiException {
            String state = OAuthHelper.extractAuthorizationStateFromUri(new URI(
                    "http://localhost:10000?state=b0fQ8iGQUONL4BHt&code=oDjzJU1j9xFH0kZv"));
            assertEquals("b0fQ8iGQUONL4BHt", state);
        }

        @Test
        void testAuthorizationStateNull() throws URISyntaxException, DracoonApiException {
            String state = OAuthHelper.extractAuthorizationStateFromUri(new URI(
                    "http://localhost:10000?&code=oDjzJU1j9xFH0kZv"));
            assertNull(state);
        }

        @Test
        void testUriValidation() {
            assertThrows(IllegalArgumentException.class, () ->
                    OAuthHelper.extractAuthorizationStateFromUri(null));
        }

    }

    @Nested
    class ExtractAuthorizationCodeFromUriTests {

        @Test
        void testAuthorizationCodeCorrect() throws URISyntaxException, DracoonApiException {
            String code = OAuthHelper.extractAuthorizationCodeFromUri(new URI(
                    "http://localhost:10000?state=b0fQ8iGQUONL4BHt&code=oDjzJU1j9xFH0kZv"));
            assertEquals("oDjzJU1j9xFH0kZv", code);
        }

        @Test
        void testAuthorizationCodeNull() throws URISyntaxException, DracoonApiException {
            String code = OAuthHelper.extractAuthorizationCodeFromUri(new URI(
                    "http://localhost:10000?state=b0fQ8iGQUONL4BHt"));
            assertNull(code);
        }

        @Test
        void testUriValidation() {
            assertThrows(IllegalArgumentException.class, () ->
                    OAuthHelper.extractAuthorizationCodeFromUri(null));
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.OAuthHelperTest#createTestErrorParsingArguments")
        void testErrorParsing(URI uri, DracoonApiCode code) {
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    OAuthHelper.extractAuthorizationStateFromUri(uri));
            assertEquals(code, thrown.getCode());
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestErrorParsingArguments() throws URISyntaxException {
        String uriTemplate = "http://localhost:10000?error=%s";
        return Stream.of(
                Arguments.of(new URI(String.format(uriTemplate, "unsupported_response_type")),
                        DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_REQUEST_INVALID),
                Arguments.of(new URI(String.format(uriTemplate, "invalid_client")),
                        DracoonApiCode.AUTH_OAUTH_CLIENT_UNKNOWN),
                Arguments.of(new URI(String.format(uriTemplate, "invalid_grant")),
                        DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED),
                Arguments.of(new URI(String.format(uriTemplate, "invalid_scope")),
                        DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_SCOPE_INVALID),
                Arguments.of(new URI(String.format(uriTemplate, "access_denied")),
                        DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_ACCESS_DENIED),
                Arguments.of(new URI(String.format(uriTemplate, "x")),
                        DracoonApiCode.AUTH_UNKNOWN_ERROR)
        );
    }

}
