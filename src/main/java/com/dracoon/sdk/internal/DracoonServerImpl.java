package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;

import java.util.Date;

class DracoonServerImpl extends DracoonRequestHandler implements DracoonClient.Server {

    private static final String LOG_TAG = DracoonServerImpl.class.getSimpleName();

    DracoonServerImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public String getVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerVersion();
    }

    @Override
    public Date getTime() throws DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        return getServerTime();
    }

}
