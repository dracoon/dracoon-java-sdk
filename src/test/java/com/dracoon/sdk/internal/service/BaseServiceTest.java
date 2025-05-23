package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.internal.BaseApiTest;
import com.dracoon.sdk.internal.TestServiceLocator;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

abstract class BaseServiceTest extends BaseApiTest {

    @FunctionalInterface
    protected interface Executable {
        void execute() throws Exception;
    }

    private final EncryptionPasswordHolder mEncPasswordHolder = new EncryptionPasswordHolder();

    @Mock
    protected CryptoWrapper mCryptoWrapper;
    @Mock
    protected ThreadHelper mThreadHelper;
    @Mock
    protected FileStreamHelper mFileStreamHelper;

    protected ServiceDependencies mServiceDependencies;
    protected TestServiceLocator mServiceLocator;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mServiceLocator = new TestServiceLocator();

        mServiceDependencies = new ServiceDependenciesImpl.Builder()
                .setLog(mLog)
                .setHttpConfig(mHttpConfig)
                .setHttpClient(mHttpClient)
                .setHttpHelper(mHttpHelper)
                .setServerUrl(mServerUrl)
                .setDracoonApi(mDracoonApi)
                .setDracoonErrorParser(mDracoonErrorParser)
                .setEncryptionPasswordHolder(mEncPasswordHolder)
                .setCryptoWrapper(mCryptoWrapper)
                .setThreadHelper(mThreadHelper)
                .setFileStreamHelper(mFileStreamHelper)
                .build();
    }

    protected void setEncryptionPassword(char[] encryptionPassword) {
        mEncPasswordHolder.set(encryptionPassword);
    }

}
