package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.error.DracoonNetInsecureException;
import retrofit2.Call;
import retrofit2.Response;

import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.io.IOException;

@SuppressWarnings("Duplicates")
public class DracoonHttpHelper {

    private static final String LOG_TAG = DracoonHttpHelper.class.getSimpleName();

    private final Log mLog;

    private boolean mIsRetryEnabled;

    public DracoonHttpHelper(Log log) {
        mLog = log;
    }

    public void setRetryEnabled(boolean isRetryEnabled) {
        mIsRetryEnabled = isRetryEnabled;
    }

    // --- Methods for REST calls ---

    public <T> Response<T> executeRequest(Call<T> call) throws DracoonNetIOException {
        Response<T> response = null;

        try {
            response = executeRequestInternally(call);
        } catch (InterruptedException e) {
            // Nothing to do here
        }

        return response;
    }

    public <T> Response<T> executeRequest(Call<T> call, Thread thread)
            throws DracoonNetIOException, InterruptedException {
        try {
            return executeRequestInternally(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }
    }

    private <T> Response<T> executeRequestInternally(Call<T> call) throws DracoonNetIOException,
            InterruptedException {
        int retryCnt = 0;

        while (true) {
            Response<T> response = null;
            Exception exception = null;

            try {
                response = call.execute();
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
                    call = call.clone();
                    retryCnt++;
                    continue;
                } else {
                    throw new DracoonNetIOException(errorText, exception);
                }
            }

            return response;
        }
    }

    // --- Methods for HTTP calls ---

    public okhttp3.Response executeRequest(okhttp3.Call call) throws DracoonNetIOException {
        okhttp3.Response response = null;

        try {
            response = executeRequestInternally(call);
        } catch (InterruptedException e) {
            // Nothing to do here
        }

        return response;
    }

    public okhttp3.Response executeRequest(okhttp3.Call call, Thread thread)
            throws DracoonNetIOException, InterruptedException {
        try {
            return executeRequestInternally(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }
    }

    private okhttp3.Response executeRequestInternally(okhttp3.Call call) throws DracoonNetIOException,
            InterruptedException {
        int retryCnt = 0;

        while (true) {
            okhttp3.Response response = null;
            Exception exception = null;

            try {
                response = call.execute();
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
                    call = call.clone();
                    retryCnt++;
                    continue;
                } else {
                    throw new DracoonNetIOException(errorText, exception);
                }
            }

            return  response;
        }
    }

}
