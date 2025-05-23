package com.dracoon.sdk.internal.auth;

import java.io.IOException;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.http.InterceptedIOException;
import com.dracoon.sdk.internal.oauth.OAuthClient;
import com.dracoon.sdk.internal.oauth.OAuthTokens;

public class AuthTokenRefresherImpl implements AuthTokenRefresher {

    private static final long AUTH_REFRESH_SKIP_INTERVAL = 15 * DracoonConstants.SECOND;

    private final OAuthClient mOAuthClient;
    private final AuthHolder mAuthHolder;

    private long mLastRefreshTime = 0L;
    private InterceptedIOException mLastRefreshException = null;

    public AuthTokenRefresherImpl(OAuthClient oAuthClient, AuthHolder authHolder) {
        mOAuthClient = oAuthClient;
        mAuthHolder = authHolder;
    }

    @Override
    public synchronized void refresh() throws IOException {
        // Get current auth
        DracoonAuth auth = mAuthHolder.get();
        if (auth == null) {
            return;
        }

        // Try to refresh tokens
        DracoonAuth newAuth = refresh(auth);

        // Update auth
        if (newAuth != auth) {
            mAuthHolder.set(auth);
        }
    }

    private DracoonAuth refresh(DracoonAuth auth) throws IOException {
        // If refresh is not overdue: Abort
        if (mLastRefreshTime + AUTH_REFRESH_SKIP_INTERVAL > System.currentTimeMillis()) {
            if (mLastRefreshException != null) {
                throw mLastRefreshException;
            }
            return auth;
        }

        // Try to refresh tokens
        OAuthTokens tokens;
        try {
            tokens = mOAuthClient.refreshTokens(auth.getClientId(), auth.getClientSecret(),
                    auth.getRefreshToken());
            mLastRefreshTime = System.currentTimeMillis();
            mLastRefreshException = null;
        } catch (DracoonNetIOException e) {
            throw new InterceptedIOException(e);
        } catch (DracoonApiException e) {
            mLastRefreshTime = System.currentTimeMillis();
            mLastRefreshException = new InterceptedIOException(e);
            throw mLastRefreshException;
        }

        // Update auth
        return new DracoonAuth(auth.getClientId(), auth.getClientSecret(), tokens.accessToken,
                tokens.refreshToken);
    }

}
