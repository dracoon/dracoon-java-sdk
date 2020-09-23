package com.dracoon.sdk.internal.oauth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.dracoon.sdk.internal.oauth.OAuthConstants.OAUTH_PATH;
import static com.dracoon.sdk.internal.oauth.OAuthConstants.OAUTH_TOKEN_PATH;
import static com.dracoon.sdk.internal.oauth.OAuthConstants.OAUTH_REVOKE_PATH;

public interface OAuthService {

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_TOKEN_PATH)
    Call<OAuthTokens> getOAuthToken(@Header("Authorization") String authorization,
            @Field("grant_type") String grantType,
            @Field("code") String code);

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_TOKEN_PATH)
    Call<OAuthTokens> refreshOAuthToken(@Header("Authorization") String authorization,
            @Field("grant_type") String grantType,
            @Field("refresh_token") String refreshToken);

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_REVOKE_PATH)
    Call<Void> revokeOAuthToken(@Header("Authorization") String authorization,
            @Field("token_type_hint") String tokenTypeHint,
            @Field("token") String token);

}
