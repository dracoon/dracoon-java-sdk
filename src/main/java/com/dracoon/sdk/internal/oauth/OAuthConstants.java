package com.dracoon.sdk.internal.oauth;

public abstract class OAuthConstants {

    private OAuthConstants() {}

    public static final String OAUTH_PATH = "/oauth";

    public static final String OAUTH_AUTHORIZE_PATH = "/authorize";
    public static final String OAUTH_TOKEN_PATH = "/token";
    public static final String OAUTH_REVOKE_PATH = "/revoke";

    public static final String OAUTH_FLOW = "code";

    public static abstract class OAuthGrantTypes {

        private OAuthGrantTypes() {}

        public static final String AUTHORIZATION_CODE = "authorization_code";
        public static final String REFRESH_TOKEN = "refresh_token";

    }

    public static abstract class OAuthTokenTypes {

        private OAuthTokenTypes() {}

        public static final String ACCESS_TOKEN = "access_token";
        public static final String REFRESH_TOKEN = "refresh_token";

    }

}
