package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.service.AccountService;
import com.dracoon.sdk.internal.service.AvatarDownloader;
import com.dracoon.sdk.internal.service.FileKeyFetcher;
import com.dracoon.sdk.internal.service.FileKeyGenerator;
import com.dracoon.sdk.internal.service.NodesService;
import com.dracoon.sdk.internal.service.ServerInfoService;
import com.dracoon.sdk.internal.service.ServerPoliciesService;
import com.dracoon.sdk.internal.service.ServerSettingsService;
import com.dracoon.sdk.internal.service.SharesService;
import com.dracoon.sdk.internal.service.UsersService;

@SuppressWarnings("unused")
public class DracoonClientImplMock extends DracoonClientImpl {

    private long mOverwrittenChunkSize = 0;

    public DracoonClientImplMock(URL serverUrl) {
        super(serverUrl);
        mApiVersion = DracoonConstants.API_MIN_VERSION;
    }

    public void setDracoonErrorParser(DracoonErrorParser dracoonErrorParser) {
        mDracoonErrorParser = dracoonErrorParser;
    }

    public void setApiVersion(String apiVersion) {
        mApiVersion = apiVersion;
    }

    @Override
    public long getChunkSize() {
        if (mOverwrittenChunkSize > 0) {
            return mOverwrittenChunkSize;
        } else {
            return super.getChunkSize();
        }
    }

    public void setChunkSize(long chunkSize) {
        if (chunkSize >= 0) {
            mOverwrittenChunkSize = chunkSize;
        }
    }

    // --- Initialization methods ---

    @Override
    public void init() {
        initOAuthClient();

        initHttpClient();
        initHttpHelper();
        initDracoonApi();
        initDracoonErrorParser();
    }

    @Override
    protected void initHttpHelper() {
        mHttpHelper = new TestHttpHelper();
        mHttpHelper.setLog(mLog);
        mHttpHelper.init();
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

    // --- Methods to set internal handlers ---

    public void setServerInfoService(ServerInfoService service) {
        mServerInfoService = service;
    }

    public void setServerSettingsService(ServerSettingsService service) {
        mServerSettingsService = service;
    }

    public void setServerPoliciesService(ServerPoliciesService service) {
        mServerPoliciesService = service;
    }

    public void setUserService(AccountService service) {
        mAccountService = service;
    }

    public void setUsersService(UsersService service) {
        mUsersService = service;
    }

    public void setNodesService(NodesService service) {
        mNodesService = service;
    }

    public void setSharesService(SharesService service) {
        mSharesService = service;
    }

    public void setFileKeyFetcher(FileKeyFetcher fileKeyFetcher) {
        mFileKeyFetcher = fileKeyFetcher;
    }

    public void setFileKeyGenerator(FileKeyGenerator fileKeyGenerator) {
        mFileKeyGenerator = fileKeyGenerator;
    }

    public void setAvatarDownloader(AvatarDownloader avatarDownloader) {
        mAvatarDownloader = avatarDownloader;
    }

}
