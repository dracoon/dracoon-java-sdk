package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.http.HttpHelper;

public class TestHttpHelper extends HttpHelper {

    private static final String LOG_TAG = TestHttpHelper.class.getSimpleName();

    private boolean mSimulateInterruptedThread;

    public TestHttpHelper() {

    }

    @Override
    public void init() {

    }

    public void setSimulateInterruptedThread(boolean simulate) {
        mSimulateInterruptedThread = simulate;
    }

    // --- Executor methods ---

    @Override
    protected Object executeRequestInternally(Object call) throws DracoonNetIOException,
            InterruptedException {
        try {
            return executeCall(call);
        } catch (IOException e) {
            if (mSimulateInterruptedThread) {
                throw new InterruptedException();
            }
            String errorText = "Server communication failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        }
    }

}
