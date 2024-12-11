package com.dracoon.sdk.internal.auth;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;

public class AuthChecker {

    public interface CheckCall {
        void execute() throws DracoonNetIOException, DracoonApiException;
    }

    private final CheckCall mCheckCall;

    public AuthChecker(CheckCall checkCall) {
        mCheckCall = checkCall;
    }

    public boolean isAuthValid() throws DracoonNetIOException, DracoonApiException {
        try {
            mCheckCall.execute();
        } catch (DracoonApiException e) {
            if (e.getCode().isAuthError()) {
                return false;
            } else {
                throw e;
            }
        }
        return true;
    }

    public void checkAuthValid() throws DracoonNetIOException, DracoonApiException {
        mCheckCall.execute();
    }

}
