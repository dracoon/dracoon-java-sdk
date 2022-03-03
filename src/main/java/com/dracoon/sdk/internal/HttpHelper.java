package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
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

    private static final String HEADER_RETRY_AFTER = "Retry-After";

    protected Log mLog = new NullLog();

    private boolean mIsRetryEnabled;
    private boolean mIsRateLimitingEnabled;

    private Executor mExecutor;

    public HttpHelper() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public void setRetryEnabled(boolean isRetryEnabled) {
        mIsRetryEnabled = isRetryEnabled;
    }

    public void setRateLimitingEnabled(boolean isRateLimitingEnabled) {
        mIsRateLimitingEnabled = isRateLimitingEnabled;
    }

    public void init() {
        mExecutor = new NetworkExecutor();
        if (mIsRetryEnabled) {
            mExecutor = new RetryExecutor(mExecutor);
        }
        if (mIsRateLimitingEnabled) {
            mExecutor = new RateLimitingExecutor(mExecutor);
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

    protected Object executeRequestInternally(Object call) throws DracoonNetIOException,
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

    private class RateLimitingExecutor extends Executor {

        public RateLimitingExecutor(Executor nextExecutor) {
            mNextExecutor = nextExecutor;
        }

        @Override
        public Object execute(Object call) throws DracoonNetIOException, DracoonApiException,
                InterceptedIOException, InterruptedException {
            int retryCnt = 0;

            while (true) {
                // Try to execute call
                Object response = mNextExecutor.execute(call);
                if (!isRateLimitResponse(response)) {
                    return response;
                }

                mLog.d(LOG_TAG, "Server communication failed due to rate limit!");

                // If retries are exceeded: Abort
                if (retryCnt >= 3) {
                    return response;
                }

                // Get retry after interval
                Integer retryAfterInterval = getRetryAfterInterval(response);

                // Calculate sleep interval
                int sleepSeconds = retryAfterInterval != null ? retryAfterInterval : retryCnt + 1;

                // Sleep till next try
                mLog.d(LOG_TAG, String.format("Next retry in %d seconds.", sleepSeconds));
                Thread.sleep(sleepSeconds * DracoonConstants.SECOND);
                call = cloneCall(call);
                retryCnt++;
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
                    // If retries are exceeded: Abort
                    if (retryCnt >= 3) {
                        throw e;
                    }

                    // Calculate sleep interval
                    int sleepSeconds = retryCnt;

                    // Sleep till next try
                    mLog.d(LOG_TAG, String.format("Next retry in %d seconds.", sleepSeconds));
                    Thread.sleep(sleepSeconds * DracoonConstants.SECOND);
                    call = cloneCall(call);
                    retryCnt++;
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

    protected static Object executeCall(Object call) throws IOException {
        if (call instanceof Call) {
            return ((Call<?>) call).execute();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).execute();
        } else {
            throw new RuntimeException("Can't execute request. Invalid call object.");
        }
    }

    protected static Object cloneCall(Object call) {
        if (call instanceof Call) {
            return ((Call<?>) call).clone();
        } else if (call instanceof okhttp3.Call) {
            return ((okhttp3.Call) call).clone();
        } else {
            throw new RuntimeException("Can't clone request. Invalid call object.");
        }
    }

    private static boolean isRateLimitResponse(Object response) {
        int statusCode;

        if (response instanceof Response) {
            Response<?> r = (Response<?>) response;
            statusCode = r.code();
        } else if (response instanceof okhttp3.Response) {
            okhttp3.Response r = (okhttp3.Response) response;
            statusCode = r.code();
        } else {
            throw new RuntimeException("Can't get response status code. Invalid response object.");
        }

        return HttpStatus.valueOf(statusCode) == HttpStatus.TOO_MANY_REQUESTS;
    }

    private static Integer getRetryAfterInterval(Object response) {
        String value;

        if (response instanceof Response) {
            Response<?> r = (Response<?>) response;
            List<String> vs = r.headers().values(HEADER_RETRY_AFTER);
            value = vs.size() > 0 ? vs.get(0) : null;
        } else if (response instanceof okhttp3.Response) {
            okhttp3.Response r = (okhttp3.Response) response;
            value = r.header(HEADER_RETRY_AFTER);
        } else {
            throw new RuntimeException("Can't get response header. Invalid response object.");
        }

        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
