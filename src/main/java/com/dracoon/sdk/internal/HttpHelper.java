package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.net.ssl.SSLHandshakeException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.error.DracoonNetIOInterruptedException;
import com.dracoon.sdk.error.DracoonNetInsecureException;
import retrofit2.Call;
import retrofit2.Response;

public class HttpHelper {

    private static final String LOG_TAG = HttpHelper.class.getSimpleName();

    private Log mLog = new NullLog();

    private boolean mIsRetryEnabled;

    private Executor mExecutor;

    public HttpHelper() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public void setRetryEnabled(boolean isRetryEnabled) {
        mIsRetryEnabled = isRetryEnabled;
    }

    public void init() {
        mExecutor = new NetworkExecutor();
        if (mIsRetryEnabled) {
            mExecutor = new RetryExecutor(mExecutor);
        }
        mExecutor = new InterceptionErrorHandlingExecutor(mExecutor);
    }

    // --- Methods for REST calls ---

    @SuppressWarnings("unchecked")
    public <T> Response<T> executeRequest(Call<T> call) throws DracoonNetIOException,
            DracoonApiException {
        try {
            return (Response<T>) executeRequestInternally(call);
        } catch (InterruptedException e) {
            String errorText = "Server communication was interrupted.";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOInterruptedException(errorText, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Response<T> executeRequest(Call<T> call, Thread thread)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
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

    public okhttp3.Response executeRequest(okhttp3.Call call) throws DracoonNetIOException,
            DracoonApiException {
        try {
            return (okhttp3.Response) executeRequestInternally(call);
        } catch (InterruptedException e) {
            String errorText = "Server communication was interrupted.";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOInterruptedException(errorText, e);
        }
    }

    public okhttp3.Response executeRequest(okhttp3.Call call, Thread thread)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
        try {
            return (okhttp3.Response) executeRequestInternally(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }
    }

    // --- Executor methods ---

    private Object executeRequestInternally(Object call) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        try {
            return mExecutor.execute(call);
        } catch (InterceptedIOException e) {
            String errorText = "Server communication failed due to an unknown error!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        }
    }

    private static abstract class Executor {

        protected Executor mNextExecutor;

        public abstract Object execute(Object call) throws DracoonNetIOException, DracoonApiException,
                InterceptedIOException, InterruptedException;

    }

    private class InterceptionErrorHandlingExecutor extends Executor {

        public InterceptionErrorHandlingExecutor(Executor nextExecutor) {
            mNextExecutor = nextExecutor;
        }

        @Override
        public Object execute(Object call) throws DracoonNetIOException, DracoonApiException,
                InterceptedIOException, InterruptedException {
            // Try to execute call
            try {
                return mNextExecutor.execute(call);
            // Handle intercepted IO errors
            } catch (InterceptedIOException e) {
                mLog.d(LOG_TAG, "Server communication was intercepted.");
                Throwable c = e.getCause();
                if (c != null) {
                    if (c.getClass().equals(DracoonNetIOException.class)) {
                        throw (DracoonNetIOException) c;
                    } else if (c.getClass().equals(DracoonApiException.class)) {
                        throw (DracoonApiException) c;
                    }
                }
                throw e;
            }
        }
    }

    private class RetryExecutor extends Executor {

        public RetryExecutor(Executor nextExecutor) {
            mNextExecutor = nextExecutor;
        }

        @Override
        public Object execute(Object call) throws DracoonNetIOException, DracoonApiException,
                InterceptedIOException, InterruptedException {
            int retryCnt = 0;

            while (true) {
                // Try to execute call
                try {
                    return mNextExecutor.execute(call);
                // Handle network IO errors
                } catch (DracoonNetIOException e) {
                    if (retryCnt < 3) {
                        int sleepSeconds = retryCnt;
                        mLog.d(LOG_TAG, String.format("Next retry in %d seconds.", sleepSeconds));
                        Thread.sleep(sleepSeconds * DracoonConstants.SECOND);
                        call = cloneCall(call);
                        retryCnt++;
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    private class NetworkExecutor extends Executor {

        public NetworkExecutor() {

        }

        @Override
        public Object execute(Object call) throws DracoonNetIOException, InterceptedIOException,
                InterruptedException {
            try {
                // Try to execute call
                return executeCall(call);
            } catch (SSLHandshakeException e) {
                // Throw network insecure exception
                String errorText = "Server SSL handshake failed!";
                mLog.e(LOG_TAG, errorText, e);
                throw new DracoonNetInsecureException(errorText, e);
            } catch (IOException e) {
                // If network IO was interrupted: Throw interrupted exception
                if (e.getClass().equals(InterruptedIOException.class)) {
                    throw new InterruptedException();
                }
                // If network IO was intercepted: Throw intercepted exception
                if (e.getClass().equals(InterceptedIOException.class)) {
                    throw (InterceptedIOException) e;
                }
                // Throw network IO exception
                String errorText = "Server communication failed!";
                mLog.d(LOG_TAG, errorText);
                throw new DracoonNetIOException(errorText, e);
            }
        }
    }

    // --- Helper methods ---

    private static Object executeCall(Object call) throws IOException {
        if (call instanceof Call) {
            return ((Call<?>) call).execute();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).execute();
        } else {
            throw new RuntimeException("Can't execute request. Invalid call object.");
        }
    }

    private static Object cloneCall(Object call) {
        if (call instanceof Call) {
            return ((Call<?>) call).clone();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).clone();
        } else {
            throw new RuntimeException("Can't clone request. Invalid call object.");
        }
    }

}
