package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.internal.BaseApiTest;
import com.dracoon.sdk.internal.DracoonClientImplMock;
import com.dracoon.sdk.internal.TestServiceLocator;
import org.junit.jupiter.api.BeforeEach;

abstract class BaseServiceTest extends BaseApiTest {

    @FunctionalInterface
    protected interface Executable {
        void execute() throws Exception;
    }

    protected DracoonClientImplMock mDracoonClientImpl;
    protected TestServiceLocator mServiceLocator = new TestServiceLocator();

    @BeforeEach
    protected void setup() throws Exception {
        mDracoonClientImpl = new DracoonClientImplMock(mServerUrl);
        mDracoonClientImpl.setHttpConfig(mHttpConfig);
        mDracoonClientImpl.setAuth(mAuth);
        mDracoonClientImpl.init();
        mDracoonClientImpl.setLog(mLog);
        mDracoonClientImpl.setDracoonErrorParser(mDracoonErrorParser);
        mDracoonClientImpl.setServiceLocator(mServiceLocator);
    }

}
