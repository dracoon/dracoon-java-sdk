package com.dracoon.sdk.internal;

import java.net.URL;

@SuppressWarnings("unused")
public class DracoonClientImplMock extends DracoonClientImpl {

    private int mS3DefaultChunkSize = 0;

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
    public int getS3DefaultChunkSize() {
        if (mS3DefaultChunkSize > 0) {
            return mS3DefaultChunkSize;
        } else {
            return super.getS3DefaultChunkSize();
        }
    }

    public void setS3DefaultChunkSize(int s3DefaultChunkSize) {
        if (s3DefaultChunkSize >= 0) {
            mS3DefaultChunkSize = s3DefaultChunkSize;
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

    public void setAvatarDownloader(AvatarDownloader avatarDownloader) {
        mAvatarDownloader = avatarDownloader;
    }

}
