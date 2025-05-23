package com.dracoon.sdk.internal.service;

import java.net.URL;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;

public abstract class BaseService implements Service {

    protected final ServiceLocator mServiceLocator;

    protected final Log mLog;

    protected final URL mServerUrl;
    protected final HttpHelper mHttpHelper;

    protected final DracoonApi mApi;
    protected final DracoonErrorParser mErrorParser;

    protected final EncryptionPasswordHolder mEncPasswordHolder;
    protected final CryptoWrapper mCryptoWrapper;

    protected ThreadHelper mThreadHelper;
    protected FileStreamHelper mFileStreamHelper;

    protected BaseService(ServiceLocator serviceLocator, ServiceDependencies serviceDependencies) {
        mServiceLocator = serviceLocator;

        mLog = serviceDependencies.getLog();

        mServerUrl = serviceDependencies.getServerUrl();
        mHttpHelper = serviceDependencies.getHttpHelper();

        mApi = serviceDependencies.getDracoonApi();
        mErrorParser = serviceDependencies.getDracoonErrorParser();

        mEncPasswordHolder = serviceDependencies.getEncryptionPasswordHolder();
        mCryptoWrapper = serviceDependencies.getCryptoWrapper();

        mThreadHelper = serviceDependencies.getThreadHelper();
        mFileStreamHelper = serviceDependencies.getFileStreamHelper();
    }

    // --- Helper methods ---

    protected String buildApiUrl(String... pathSegments) {
        StringBuilder sb = new StringBuilder();
        sb.append(mServerUrl);
        sb.append(DracoonConstants.API_PATH);
        for (String pathSegment : pathSegments) {
            sb.append("/").append(pathSegment);
        }
        return sb.toString();
    }

}
