package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.internal.BaseApiTest;
import com.dracoon.sdk.internal.DracoonClientImplMock;
import com.dracoon.sdk.internal.TestServiceLocator;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
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
        super.setup();

        mDracoonClientImpl = new DracoonClientImplMock(mServerUrl);
        mDracoonClientImpl.setHttpConfig(mHttpConfig);
        mDracoonClientImpl.setAuth(mAuth);
        mDracoonClientImpl.init();
        mDracoonClientImpl.setLog(mLog);
        mDracoonClientImpl.setDracoonErrorParser(mDracoonErrorParser);
        mDracoonClientImpl.setServiceLocator(mServiceLocator);
    }

    protected void setEncryptionPassword(char[] encryptionPassword) {
        mDracoonClientImpl.setEncryptionPassword(encryptionPassword);
    }

    protected void setCryptoWrapper(CryptoWrapper cryptoWrapper) {
        mDracoonClientImpl.setCryptoWrapper(cryptoWrapper);
    }

}
