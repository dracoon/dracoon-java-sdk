package com.dracoon.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.oauth.OAuthConstants;
import com.dracoon.sdk.internal.oauth.OAuthErrorParser;
import com.dracoon.sdk.internal.validator.ValidatorUtils;

/**
 * The Dracoon SDK uses OAuth 2.0 for client authorization. See <a href="https://tools.ietf.org/
 * html/rfc6749">RFC 6749</a> for a detailed description of OAuth 2.0.<br>
 * <br>
 * OAuthHelper is helper class for the first part of the OAuth Authorization Code flow.<br>
 * <br>
 * The class provides methods to:<br>
 * - Create the authorization URL (Which must be opened user's browser.)
 * ({@link #createAuthorizationUrl(URL serverUrl, String clientId, String state)})<br>
 * - Extract authorization state from redirect URI
 * ({@link #extractAuthorizationStateFromUri(URI uri)})<br>
 * - Extract authorization code from redirect URI
 * ({@link #extractAuthorizationCodeFromUri(URI uri)})<br>
 */
@SuppressWarnings("unused")
public class OAuthHelper {

    private static final OAuthErrorParser sErrorParser = new OAuthErrorParser();

    private OAuthHelper() {

    }

    /**
     * Creates the authorization URL which must be open in the user's browser.
     *
     * @param serverUrl The URL of the Dracoon server.
     * @param clientId  The ID of the OAuth client.
     * @param state     The state identifier which is used to track running authorizations.
     *
     * @return the authorization URL
     */
    public static String createAuthorizationUrl(URL serverUrl, String clientId, String state) {
        return createAuthorizationUrlInternally(serverUrl, clientId, state, null);
    }

    /**
     * Creates the authorization URL which must be open in the user's browser.
     *
     * @param serverUrl   The URL of the Dracoon server.
     * @param clientId    The ID of the OAuth client.
     * @param state       The state identifier which is used to track running authorizations.
     * @param redirectUri The redirect URI which is used to receive the authorization callback.
     *
     * @return the authorization URL
     */
    public static String createAuthorizationUrl(URL serverUrl, String clientId, String state,
            URI redirectUri) {
        ValidatorUtils.validateNotNull("Redirect URI", redirectUri);
        return createAuthorizationUrlInternally(serverUrl, clientId, state, redirectUri);
    }

    private static String createAuthorizationUrlInternally(URL serverUrl, String clientId,
            String state, URI redirectUri) {
        ValidatorUtils.validateServerURL(serverUrl);
        ValidatorUtils.validateString("Client ID", clientId, false);
        ValidatorUtils.validateString("State", state, false);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(serverUrl)
                .append(OAuthConstants.OAUTH_PATH)
                .append(OAuthConstants.OAUTH_AUTHORIZE_PATH);
        urlBuilder.append("?");
        urlBuilder.append("response_type=")
                .append(OAuthConstants.OAUTH_FLOW);
        urlBuilder.append("&client_id=")
                .append(clientId);
        urlBuilder.append("&state=")
                .append(state);
        if (redirectUri != null) {
            try {
                String encodedRedirectUri = URLEncoder.encode(redirectUri.toString(),
                        StandardCharsets.UTF_8.toString());
                urlBuilder.append("&redirect_uri=")
                        .append(encodedRedirectUri);
            } catch (UnsupportedEncodingException e) {
                // A failed encoding is ignored
            }
        }

        return urlBuilder.toString();
    }

    /**
     * Extracts the authorization state from the called redirect URI.
     *
     * @param uri The called redirect URI.
     *
     * @return the authorization state
     *
     * @throws DracoonApiException If a OAuth error occurred.
     */
    public static String extractAuthorizationStateFromUri(URI uri) throws DracoonApiException {
        return extractAuthorizationDataFromUri(uri, "state");
    }

    /**
     * Extracts the authorization code from the called redirect URI.
     *
     * @param uri The called redirect URI.
     *
     * @return the authorization code
     *
     * @throws DracoonApiException If a OAuth error occurred.
     */
    public static String extractAuthorizationCodeFromUri(URI uri) throws DracoonApiException {
        return extractAuthorizationDataFromUri(uri, "code");
    }

    private static String extractAuthorizationDataFromUri(URI uri, String name)
            throws DracoonApiException {
        ValidatorUtils.validateNotNull("Redirect URI", uri);

        String query = uri.getQuery();
        Map<String, String> queryParams = parseQuery(query);

        String error = queryParams.get("error");
        if (error != null) {
            throw sErrorParser.parseAuthorizeError(error);
        }

        return queryParams.get(name);
    }

    // --- Helper methods ---

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        String[] params = query != null ? query.split("&") : new String[]{};
        for (String param : params) {
            String[] kv = param.split("=");
            String k = kv.length > 0 ? kv[0] : null;
            String v = kv.length > 1 ? kv[1] : null;
            if (k != null) {
                result.put(k, v);
            }
        }
        return result;
    }

}
