package com.dracoon.sdk.internal.service;

import java.util.List;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.DracoonConstants;

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

    protected DownloadStream.Factory mDownloadStreamFactory;
    protected DownloadThread.Factory mDownloadThreadFactory;
    protected UploadStream.Factory mUploadStreamFactory;
    protected UploadThread.Factory mUploadThreadFactory;

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

        DracoonHttpConfig httpConfig = client.getHttpConfig();
        long chunkSize = ((long) httpConfig.getChunkSize()) * DracoonConstants.KIB;

        mDownloadStreamFactory = new DownloadStream.Factory(client.getLog(), client.getDracoonApi(),
                client.getHttpClient(), client.getHttpHelper(), client.getDracoonErrorParser(),
                client.getCryptoWrapper(), chunkSize);
        mDownloadThreadFactory = new DownloadThread.Factory(client.getLog(), mDownloadStreamFactory);

        mUploadStreamFactory = new UploadStream.Factory(client.getLog(), client.getDracoonApi(),
                client.getHttpClient(), client.getHttpHelper(), client.getDracoonErrorParser(),
                client.getCryptoWrapper(), chunkSize);
        mUploadThreadFactory = new UploadThread.Factory(client.getLog(), mUploadStreamFactory);
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

    public DownloadStream.Factory getDownloadStreamFactory() {
        return mDownloadStreamFactory;
    }

    public DownloadThread.Factory getDownloadThreadFactory() {
        return mDownloadThreadFactory;
    }

    public UploadStream.Factory getUploadStreamFactory() {
        return mUploadStreamFactory;
    }

    public UploadThread.Factory getUploadThreadFactory() {
        return mUploadThreadFactory;
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
