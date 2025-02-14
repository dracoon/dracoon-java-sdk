package com.dracoon.sdk.internal.service;

import java.net.URL;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;
import okhttp3.OkHttpClient;

public final class ServiceDependenciesImpl implements ServiceDependencies {

    private Log mLog;
    private DracoonHttpConfig mHttpConfig;

    private OkHttpClient mHttpClient;
    private HttpHelper mHttpHelper;

    private URL mServerUrl;
    private DracoonApi mApi;
    private DracoonErrorParser mErrorParser;

    private EncryptionPasswordHolder mEncPasswordHolder;
    private CryptoWrapper mCryptoWrapper;

    private ThreadHelper mThreadHelper = new ThreadHelper();
    private FileStreamHelper mFileStreamHelper = new FileStreamHelper();

    private ServiceDependenciesImpl() {}

    @Override
    public Log getLog() {
        return mLog;
    }

    @Override
    public DracoonHttpConfig getHttpConfig() {
        return mHttpConfig;
    }

    @Override
    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    @Override
    public HttpHelper getHttpHelper() {
        return mHttpHelper;
    }

    @Override
    public URL getServerUrl() {
        return mServerUrl;
    }

    @Override
    public DracoonApi getDracoonApi() {
        return mApi;
    }

    @Override
    public DracoonErrorParser getDracoonErrorParser() {
        return mErrorParser;
    }

    @Override
    public EncryptionPasswordHolder getEncryptionPasswordHolder() {
        return mEncPasswordHolder;
    }

    @Override
    public CryptoWrapper getCryptoWrapper() {
        return mCryptoWrapper;
    }

    @Override
    public ThreadHelper getThreadHelper() {
        return mThreadHelper;
    }

    @Override
    public FileStreamHelper getFileStreamHelper() {
        return mFileStreamHelper;
    }

    public static class Builder {

        private final ServiceDependenciesImpl mDependencies;

        public Builder() {
            mDependencies = new ServiceDependenciesImpl();
        }

        public Builder setLog(Log log) {
            mDependencies.mLog = log;
            return this;
        }

        public Builder setHttpConfig(DracoonHttpConfig httpConfig) {
            mDependencies.mHttpConfig = httpConfig;
            return this;
        }

        public Builder setHttpClient(OkHttpClient httpClient) {
            mDependencies.mHttpClient = httpClient;
            return this;
        }

        public Builder setHttpHelper(HttpHelper httpHelper) {
            mDependencies.mHttpHelper = httpHelper;
            return this;
        }

        public Builder setServerUrl(URL serverUrl) {
            mDependencies.mServerUrl = serverUrl;
            return this;
        }

        public Builder setDracoonApi(DracoonApi api) {
            mDependencies.mApi = api;
            return this;
        }

        public Builder setDracoonErrorParser(DracoonErrorParser errorParser) {
            mDependencies.mErrorParser = errorParser;
            return this;
        }

        public Builder setEncryptionPasswordHolder(EncryptionPasswordHolder encPasswordHolder) {
            mDependencies.mEncPasswordHolder = encPasswordHolder;
            return this;
        }

        public Builder setCryptoWrapper(CryptoWrapper cryptoWrapper) {
            mDependencies.mCryptoWrapper = cryptoWrapper;
            return this;
        }

        public Builder setThreadHelper(ThreadHelper threadHelper) {
            mDependencies.mThreadHelper = threadHelper;
            return this;
        }

        public Builder setFileStreamHelper(FileStreamHelper fileStreamHelper) {
            mDependencies.mFileStreamHelper = fileStreamHelper;
            return this;
        }

        public ServiceDependenciesImpl build() {
            if (mDependencies.mLog == null ||
                    mDependencies.mHttpConfig == null ||
                    mDependencies.mHttpClient == null ||
                    mDependencies.mHttpHelper == null ||
                    mDependencies.mServerUrl == null ||
                    mDependencies.mApi == null ||
                    mDependencies.mErrorParser == null ||
                    mDependencies.mEncPasswordHolder == null ||
                    mDependencies.mCryptoWrapper == null ||
                    mDependencies.mThreadHelper == null ||
                    mDependencies.mFileStreamHelper == null) {
                throw new IllegalStateException("Service dependencies cannot be built. One or " +
                        "more dependencies were not set.");
            }

            return mDependencies;
        }

    }

}
