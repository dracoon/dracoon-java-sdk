package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.http.HttpHelper;

public abstract class BaseService implements Service {

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonApi mApi;
    protected final HttpHelper mHttpHelper;
    protected final DracoonErrorParser mErrorParser;
    protected final ServiceLocator mServiceLocator;

    protected BaseService(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mApi = client.getDracoonApi();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();
        mServiceLocator = client.getServiceLocator();
    }

}
