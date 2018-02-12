package com.dracoon.sdk.internal.oauth;

public interface OAuthConstants {

    String OAUTH_PATH = "/oauth";

    String OAUTH_AUTHORIZE_PATH = "/authorize";
    String OAUTH_TOKEN_PATH = "/token";

    String OAUTH_FLOW = "code";

    interface OAuthGrantTypes {
        String AUTHORIZATION_CODE = "authorization_code";
        String REFRESH_TOKEN = "refresh_token";
    }

}
