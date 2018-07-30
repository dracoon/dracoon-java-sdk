package com.dracoon.sdk.internal.oauth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.dracoon.sdk.internal.oauth.OAuthConstants.OAUTH_PATH;
import static com.dracoon.sdk.internal.oauth.OAuthConstants.OAUTH_TOKEN_PATH;

public interface OAuthService {

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_TOKEN_PATH)
    Call<OAuthTokens> getOAuthToken(@Header("Authorization") String authorization,
                                    @Field("grant_type") String grantType,
                                    @Field("code") String code);

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_TOKEN_PATH)
    Call<OAuthTokens> getOAuthToken(@Header("Authorization") String authorization,
                                    @Field("grant_type") String grantType,
                                    @Field("username") String username,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST(OAUTH_PATH + OAUTH_TOKEN_PATH)
    Call<OAuthTokens> refreshOAuthToken(@Header("Authorization") String authorization,
                                        @Field("grant_type") String grantType,
                                        @Field("refresh_token") String refreshToken);

}
