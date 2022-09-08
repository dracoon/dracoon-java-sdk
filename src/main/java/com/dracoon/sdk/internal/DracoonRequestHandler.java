package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;

public abstract class DracoonRequestHandler {

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonService mService;
    protected final HttpHelper mHttpHelper;
    protected final DracoonErrorParser mErrorParser;

    public DracoonRequestHandler(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mService = client.getDracoonService();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();
    }

}
