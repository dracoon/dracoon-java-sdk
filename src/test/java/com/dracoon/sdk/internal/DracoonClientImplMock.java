package com.dracoon.sdk.internal;

import java.net.URL;

@SuppressWarnings("unused")
public class DracoonClientImplMock extends DracoonClientImpl {

    public DracoonClientImplMock(URL serverUrl) {
        super(serverUrl);
    }

    public void setDracoonService(DracoonService dracoonService) {
        mDracoonService = dracoonService;
    }

    public void setDracoonErrorParser(DracoonErrorParser dracoonErrorParser) {
        mDracoonErrorParser = dracoonErrorParser;
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

}
