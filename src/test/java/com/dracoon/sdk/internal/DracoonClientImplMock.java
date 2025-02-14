package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.service.ServiceLocator;

@SuppressWarnings("unused")
public class DracoonClientImplMock extends DracoonClientImpl {

    public DracoonClientImplMock(URL serverUrl) {
        super(serverUrl);
    }

    public void setDracoonErrorParser(DracoonErrorParser dracoonErrorParser) {
        mDracoonErrorParser = dracoonErrorParser;
    }

    public void setCryptoWrapper(CryptoWrapper cryptoWrapper) {
        mCryptoWrapper = cryptoWrapper;
    }

    public void setThreadHelper(ThreadHelper threadHelper) {
        mThreadHelper = threadHelper;
    }

    public void setFileStreamHelper(FileStreamHelper fileStreamHelper) {
        mFileStreamHelper = fileStreamHelper;
    }

    public void setServiceLocator(ServiceLocator serviceLocator) {
        mServiceLocator = serviceLocator;
    }

    @Override
    public void init() {
        initOAuthClient();

        initHttpClient();
        initHttpHelper();

        initAuthHelpers();

        initDracoonApi();
    }

    @Override
    protected void initHttpHelper() {
        mHttpHelper = new TestHttpHelper();
        mHttpHelper.setLog(getLog());
        mHttpHelper.init();
    }

}
