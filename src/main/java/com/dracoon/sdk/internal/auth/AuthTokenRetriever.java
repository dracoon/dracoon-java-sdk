package com.dracoon.sdk.internal.auth;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;

public interface AuthTokenRetriever {

    void retrieve() throws DracoonApiException, DracoonNetIOException;

}
