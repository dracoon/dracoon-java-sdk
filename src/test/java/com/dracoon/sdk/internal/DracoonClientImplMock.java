package com.dracoon.sdk.internal;

import java.net.URL;

@SuppressWarnings("unused")
public class DracoonClientImplMock extends DracoonClientImpl {

    private long mOverwrittenChunkSize = 0;

    public DracoonClientImplMock(URL serverUrl) {
        super(serverUrl);
        mApiVersion = DracoonConstants.API_MIN_VERSION;
    }

    public void setDracoonService(DracoonService dracoonService) {
        mDracoonService = dracoonService;
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
        initDracoonService();
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

    public void setServerImpl(DracoonServerImpl server) {
        mServer = server;
    }

    public void setServerSettingsImpl(DracoonServerSettingsImpl serverSettings) {
        mServerSettings = serverSettings;
    }

    public void setServerPoliciesImpl(DracoonServerPoliciesImpl serverPolicies) {
        mServerPolicies = serverPolicies;
    }

    public void setAccountImpl(DracoonAccountImpl account) {
        mAccount = account;
    }

    public void setUsersImpl(Users users) {
        mUsers = users;
    }

    public void setGroupsImpl(Groups groups) {
        mGroups = groups;
    }

    public void setNodesImpl(DracoonNodesImpl nodes) {
        mNodes = nodes;
    }

    public void setSharesImpl(DracoonSharesImpl shares) {
        mShares = shares;
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
