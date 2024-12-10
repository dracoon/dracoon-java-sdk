package com.dracoon.sdk.internal.service;

import java.util.List;

import com.dracoon.sdk.internal.DracoonClientImpl;

public class ServiceLocator {

    protected ServerInfoService mServerInfoService;
    protected ServerSettingsService mServerSettingsService;
    protected ServerPoliciesService mServerPoliciesService;
    protected AccountService mAccountService;
    protected UsersService mUsersService;
    protected NodesService mNodesService;
    protected SharesService mSharesService;

    protected FileKeyFetcher mFileKeyFetcher;
    protected FileKeyGenerator mFileKeyGenerator;
    protected AvatarDownloader mAvatarDownloader;

    public ServiceLocator(DracoonClientImpl client) {
        init(client);
    }

    protected void init(DracoonClientImpl client) {
        mServerInfoService = new ServerInfoService(client);
        mServerSettingsService = new ServerSettingsService(client);
        mServerPoliciesService = new ServerPoliciesService(client);
        mAccountService = new AccountService(client);
        mUsersService = new UsersService(client);
        mNodesService = new NodesService(client);
        mSharesService = new SharesService(client);

        mFileKeyFetcher = new FileKeyFetcher(client);
        mFileKeyGenerator = new FileKeyGenerator(client);
        mAvatarDownloader = new AvatarDownloader(client);
    }

    public ServerInfoService getServerInfoService() {
        return mServerInfoService;
    }

    public ServerSettingsService getServerSettingsService() {
        return mServerSettingsService;
    }

    public ServerPoliciesService getServerPoliciesService() {
        return mServerPoliciesService;
    }

    public AccountService getAccountService() {
        return mAccountService;
    }

    public UsersService getUsersService() {
        return mUsersService;
    }

    public NodesService getNodesService() {
        return mNodesService;
    }

    public SharesService getSharesService() {
        return mSharesService;
    }

    public FileKeyFetcher getFileKeyFetcher() {
        return mFileKeyFetcher;
    }

    public FileKeyGenerator getFileKeyGenerator() {
        return mFileKeyGenerator;
    }

    public AvatarDownloader getAvatarDownloader() {
        return mAvatarDownloader;
    }

    public List<Service> getServices() {
        return List.of(
                mServerInfoService,
                mServerSettingsService,
                mServerPoliciesService,
                mAccountService,
                mUsersService,
                mNodesService,
                mSharesService);
    }

}
