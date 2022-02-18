package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.error.DracoonNetIOException;

public class TestHttpHelper extends HttpHelper {

    private static final String LOG_TAG = TestHttpHelper.class.getSimpleName();

    public TestHttpHelper() {

    }

    @Override
    public void init() {

    }

    // --- Executor methods ---

    @Override
    protected Object executeRequestInternally(Object call) throws DracoonNetIOException {
        try {
            return executeCall(call);
        } catch (IOException e) {
            String errorText = "Server communication failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        }
    }

}
