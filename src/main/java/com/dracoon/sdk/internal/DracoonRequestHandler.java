package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;

public abstract class DracoonRequestHandler {

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonService mService;
    protected final DracoonErrorParser mErrorParser;
    protected final HttpHelper mHttpHelper;

    public DracoonRequestHandler(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mService = client.getDracoonService();
        mErrorParser = client.getDracoonErrorParser();
        mHttpHelper = client.getHttpHelper();
    }

}
