package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.error.DracoonNetInsecureException;
import retrofit2.Call;
import retrofit2.Response;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;

public class DracoonServiceHelper {

    private static final String LOG_TAG = DracoonServiceHelper.class.getSimpleName();

    @SuppressWarnings("unchecked")
    public static <T> Response<T> executeRequest(Call<T> call) throws DracoonException {
        Response response;
        try {
            response = call.execute();
        } catch (SSLHandshakeException e) {
            String errorText = "Server SSL handshake failed!";
            Log.e(LOG_TAG, errorText, e);
            throw new DracoonNetInsecureException(errorText, e);
        } catch (IOException e) {
            String errorText = "Server communication failed!";
            Log.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        }

        return (Response<T>) response;
    }

    @SuppressWarnings("unchecked")
    public static <T> Response<T> executeRequest(Call<T> call, Thread thread)
            throws DracoonException, InterruptedException {
        Response response;
        try {
            response = executeRequest(call);
        } catch (DracoonNetIOException e) {
            if (thread.isInterrupted()) {
                throw new InterruptedException();
            }
            throw e;
        }

        return (Response<T>) response;
    }

}
