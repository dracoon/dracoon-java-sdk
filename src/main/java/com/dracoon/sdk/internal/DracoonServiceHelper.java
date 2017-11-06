package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.error.DracoonNetInsecureException;
import retrofit2.Call;
import retrofit2.Response;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;

public class DracoonServiceHelper {

    @SuppressWarnings("unchecked")
    public static <T> Response<T> executeRequest(Call<T> call) throws DracoonException {
        Response response;
        try {
            response = call.execute();
        } catch (SSLHandshakeException e) {
            throw new DracoonNetInsecureException("Server SSL handshake failed!", e);
        } catch (IOException e) {
            throw new DracoonNetIOException("Server communication failed!", e);
        }

        return (Response<T>) response;
    }

}
