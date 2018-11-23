package com.dracoon.sdk.internal;

import java.io.InterruptedIOException;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.error.DracoonNetIOInterruptedException;
import com.dracoon.sdk.error.DracoonNetInsecureException;
import retrofit2.Call;
import retrofit2.Response;

public class HttpHelper {

    private static final String LOG_TAG = HttpHelper.class.getSimpleName();

    private Log mLog = new NullLog();

    private boolean mIsRetryEnabled;

    public HttpHelper() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public void setRetryEnabled(boolean isRetryEnabled) {
        mIsRetryEnabled = isRetryEnabled;
    }

    // --- Methods for REST calls ---

    @SuppressWarnings("unchecked")
    public <T> Response<T> executeRequest(Call<T> call) throws DracoonNetIOException {
        try {
            return (Response<T>) executeRequestInternally(call);
        } catch (InterruptedException e) {
            String errorText = "Server communication interrupted.";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOInterruptedException(errorText, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Response<T> executeRequest(Call<T> call, Thread thread)
            throws DracoonNetIOException, InterruptedException {
        try {
            return (Response<T>) executeRequestInternally(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }
    }

    // --- Methods for HTTP calls ---

    @SuppressWarnings("unchecked")
    public okhttp3.Response executeRequest(okhttp3.Call call) throws DracoonNetIOException {
        try {
            return (okhttp3.Response) executeRequestInternally(call);
        } catch (InterruptedException e) {
            String errorText = "Server communication interrupted.";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOInterruptedException(errorText, e);
        }
    }

    @SuppressWarnings("unchecked")
    public okhttp3.Response executeRequest(okhttp3.Call call, Thread thread)
            throws DracoonNetIOException, InterruptedException {
        try {
            return (okhttp3.Response) executeRequestInternally(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }
    }

    // --- Helper methods ---

    private Object executeRequestInternally(Object call) throws DracoonNetIOException,
            InterruptedException {
        int retryCnt = 0;

        while (true) {
            Object response = null;
            Exception exception = null;

            try {
                response = executeCallInternally(call);
            } catch (SSLHandshakeException e) {
                String errorText = "Server SSL handshake failed!";
                mLog.e(LOG_TAG, errorText, e);
                throw new DracoonNetInsecureException(errorText, e);
            } catch (IOException e) {
                if (e.getClass().equals(InterruptedIOException.class)) {
                    throw new InterruptedException();
                }
                exception = e;
            }

            if (exception != null) {
                String errorText = "Server communication failed!";
                mLog.d(LOG_TAG, errorText);

                if (mIsRetryEnabled && retryCnt < 3) {
                    mLog.d(LOG_TAG, String.format("Next retry in %d seconds.", retryCnt));
                    Thread.sleep(retryCnt * 1000);
                    call = cloneCallInternally(call);
                    retryCnt++;
                    continue;
                } else {
                    throw new DracoonNetIOException(errorText, exception);
                }
            }

            return  response;
        }
    }

    private Object executeCallInternally(Object call) throws IOException {
        if (call instanceof Call) {
            return ((Call) call).execute();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).execute();
        } else {
            throw new RuntimeException("Can't execute request. Invalid call object.");
        }
    }

    private Object cloneCallInternally(Object call) {
        if (call instanceof Call) {
            return ((Call) call).clone();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).clone();
        } else {
            throw new RuntimeException("Can't clone request. Invalid call object.");
        }
    }

}
