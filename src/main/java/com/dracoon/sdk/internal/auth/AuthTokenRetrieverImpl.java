package com.dracoon.sdk.internal.auth;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;

public class AuthTokenRetrieverImpl implements AuthTokenRetriever {

    private final OAuthClient mOAuthClient;
    private final AuthHolder mAuthHolder;

    public AuthTokenRetrieverImpl(OAuthClient oAuthClient, AuthHolder authHolder) {
        mOAuthClient = oAuthClient;
        mAuthHolder = authHolder;
    }

    @Override
    public synchronized void retrieve() throws DracoonApiException, DracoonNetIOException {
        DracoonAuth auth = mAuthHolder.get();
        if (auth == null || !auth.getMode().equals(DracoonAuth.Mode.AUTHORIZATION_CODE)) {
            return;
        }

        OAuthTokens tokens = mOAuthClient.retrieveTokens(auth.getClientId(), auth.getClientSecret(),
                auth.getAuthorizationCode());

        auth = new DracoonAuth(auth.getClientId(), auth.getClientSecret(), tokens.accessToken,
                tokens.refreshToken);
        mAuthHolder.set(auth);
    }

}
