package com.dracoon.sdk.internal.oauth;

public interface OAuthConstants {

    String OAUTH_PATH = "/oauth";

    String OAUTH_AUTHORIZE_PATH = "/authorize";
    String OAUTH_TOKEN_PATH = "/token";
    String OAUTH_REVOKE_PATH = "/revoke";

    String OAUTH_FLOW = "code";

    interface OAuthGrantTypes {
        String AUTHORIZATION_CODE = "authorization_code";
        String REFRESH_TOKEN = "refresh_token";
    }

    interface OAuthTokenTypes {
        String ACCESS_TOKEN = "access_token";
        String REFRESH_TOKEN = "refresh_token";
    }

}
