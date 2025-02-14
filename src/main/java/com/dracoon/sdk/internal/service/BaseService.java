package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;

public abstract class BaseService implements Service {

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonApi mApi;
    protected final HttpHelper mHttpHelper;
    protected final DracoonErrorParser mErrorParser;

    protected final EncryptionPasswordHolder mEncPasswordHolder;
    protected final CryptoWrapper mCryptoWrapper;

    protected final ServiceLocator mServiceLocator;

    protected ThreadHelper mThreadHelper;
    protected FileStreamHelper mFileStreamHelper;

    protected BaseService(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mApi = client.getDracoonApi();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();

        mEncPasswordHolder = client.getEncryptionPasswordHolder();
        mCryptoWrapper = client.getCryptoWrapper();

        mServiceLocator = client.getServiceLocator();

        mThreadHelper = new ThreadHelper();
        mFileStreamHelper = new FileStreamHelper();
    }

    public void setThreadHelper(ThreadHelper threadHelper) {
        mThreadHelper = threadHelper;
    }

    public void setFileStreamHelper(FileStreamHelper fileStreamHelper) {
        mFileStreamHelper = fileStreamHelper;
    }

}
