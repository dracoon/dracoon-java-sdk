package com.dracoon.sdk;

import com.dracoon.sdk.internal.validator.ValidatorUtils;

/**
 * The Dracoon SDK uses OAuth 2.0 for client authorization. See <a href="https://tools.ietf.org/
 * html/rfc6749">RFC 6749</a> for a detailed description of OAuth 2.0. Because OAuth can be
 * difficult to implement for beginners, the Dracoon SDK can handle the OAuth authorization steps to
 * obtain and refresh tokens.<br>
 * <br>
 * This class is used to configure which steps of the OAuth authorization are made by the Dracoon
 * SDK.<br>
 * <br>
 * Following three modes are supported:<br>
 * <br>
 * - Authorization Code Mode: ({@link #DracoonAuth(String clientId, String clientSecret,
 * String authorizationCode)})<br>
 *   This is the most common mode. Your application must request authorization and obtain an
 *   authorization code and the retrieval of the access and refresh tokens with the authorization
 *   code as well as the automatic token refresh is handled by the Dracoon SDK.<br>
 *   <br>
 *   The authorization is done within the user's browser or a web view. After the user has logged in
 *   and authorized your application you receive the authorization code via a callback to a
 *   pre-defined redirect URI. Depending on the type of your application you must open a local TCP
 *   port, register the redirect URI at your OS or provide an HTTP endpoint which receives the
 *   callback.<br>
 *   <br>
 *   (You can use {@link OAuthHelper} to create the Authorization URL which must be opened in the
 *   user's browser or web view and to extract the state and code from the redirect URI.)<br>
 * <br>
 * - Access Token Mode: ({@link #DracoonAuth(String accessToken)})<br>
 *   This is a simple mode. You can use it at the development or for terminal applications and
 *   scripts where a specific user account is used.<br>
 * <br>
 * - Access and Refresh Token Mode: ({@link #DracoonAuth(String clientId,
 * String clientSecret, String accessToken, String refreshToken)})<br>
 *   This mode can be used to obtain access and refresh token yourself.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DracoonAuth {

    /**
     * Enumeration of authorization modes.
     */
    public enum Mode {
        AUTHORIZATION_CODE,
        ACCESS_TOKEN,
        ACCESS_REFRESH_TOKEN
    }

    private final Mode mMode;

    private String mClientId;
    private String mClientSecret;
    private String mAuthorizationCode;
    private String mAccessToken;
    private String mRefreshToken;

    /**
     * Constructs a new configuration for the Authorization Code Mode.
     *
     * @param clientId          The OAuth client ID.
     * @param clientSecret      The OAuth client secret.
     * @param authorizationCode The OAuth authorization code.
     */
    public DracoonAuth(String clientId, String clientSecret, String authorizationCode) {
        mMode = Mode.AUTHORIZATION_CODE;
        ValidatorUtils.validateString("Client ID", clientId, false);
        mClientId = clientId;
        ValidatorUtils.validateString("Client secret", clientSecret, false);
        mClientSecret = clientSecret;
        ValidatorUtils.validateString("Authorization code", clientSecret, false);
        mAuthorizationCode = authorizationCode;
    }

    /**
     * Constructs a new configuration for the Access Token Mode.
     *
     * @param accessToken The OAuth access token.
     */
    public DracoonAuth(String accessToken) {
        mMode = Mode.ACCESS_TOKEN;
        ValidatorUtils.validateString("Access token", accessToken, false);
        mAccessToken = accessToken;
    }

    /**
     * Constructs a new configuration for the Access and Refresh Token Mode.
     *
     * @param clientId     The OAuth client ID.
     * @param clientSecret The OAuth client secret.
     * @param accessToken  The OAuth access token.
     * @param refreshToken The OAuth refresh token.
     */
    public DracoonAuth(String clientId, String clientSecret, String accessToken,
            String refreshToken) {
        mMode = Mode.ACCESS_REFRESH_TOKEN;
        ValidatorUtils.validateString("Client ID", clientId, false);
        mClientId = clientId;
        ValidatorUtils.validateString("Client secret", clientSecret, false);
        mClientSecret = clientSecret;
        ValidatorUtils.validateString("Access token", accessToken, false);
        mAccessToken = accessToken;
        ValidatorUtils.validateString("Refresh token", refreshToken, true);
        mRefreshToken = refreshToken;
    }

    /**
     * Returns the used authorization mode.<br>
     * <br>
     * See: {@link DracoonAuth.Mode}
     *
     * @return the used authorization mode
     */
    public Mode getMode() {
        return mMode;
    }

    /**
     * Returns the OAuth client ID.
     *
     * @return the OAuth client ID
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * Returns the OAuth client secret.
     *
     * @return the OAuth client secret
     */
    public String getClientSecret() {
        return mClientSecret;
    }

    /**
     * Returns the OAuth authorization code.
     *
     * @return the OAuth authorization code
     */
    public String getAuthorizationCode() {
        return mAuthorizationCode;
    }

    /**
     * Returns the OAuth access token.
     *
     * @return the OAuth access token
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Returns the OAuth access token.
     *
     * @return the OAuth access token.
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }

}
