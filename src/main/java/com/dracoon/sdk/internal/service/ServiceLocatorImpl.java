package com.dracoon.sdk.internal.service;

import java.util.List;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.internal.DracoonConstants;

public class ServiceLocatorImpl implements ServiceLocator {

    private final ServerInfoService mServerInfoService;
    private final ServerSettingsService mServerSettingsService;
    private final ServerPoliciesService mServerPoliciesService;
    private final AccountService mAccountService;
    private final UsersService mUsersService;
    private final NodesService mNodesService;
    private final SharesService mSharesService;

    private final FileKeyFetcher mFileKeyFetcher;
    private final FileKeyGenerator mFileKeyGenerator;

    private final AvatarDownloader mAvatarDownloader;

    private final DownloadStream.Factory mDownloadStreamFactory;
    private final DownloadThread.Factory mDownloadThreadFactory;
    private final UploadStream.Factory mUploadStreamFactory;
    private final UploadThread.Factory mUploadThreadFactory;

    public ServiceLocatorImpl(ServiceDependencies dependencies) {
        mServerInfoService = new ServerInfoService(this, dependencies);
        mServerSettingsService = new ServerSettingsService(this, dependencies);
        mServerPoliciesService = new ServerPoliciesService(this, dependencies);
        mAccountService = new AccountService(this, dependencies);
        mUsersService = new UsersService(this, dependencies);
        mNodesService = new NodesService(this, dependencies);
        mSharesService = new SharesService(this, dependencies);

        mFileKeyFetcher = new FileKeyFetcher(this, dependencies);
        mFileKeyGenerator = new FileKeyGenerator(this, dependencies);

        mAvatarDownloader = new AvatarDownloader(dependencies.getLog(), dependencies.getHttpClient(),
                dependencies.getHttpHelper(), dependencies.getDracoonErrorParser());

        DracoonHttpConfig httpConfig = dependencies.getHttpConfig();
        long chunkSize = ((long) httpConfig.getChunkSize()) * DracoonConstants.KIB;

        mDownloadStreamFactory = new DownloadStream.Factory(dependencies.getLog(),
                dependencies.getDracoonApi(), dependencies.getHttpClient(),
                dependencies.getHttpHelper(), dependencies.getDracoonErrorParser(),
                dependencies.getCryptoWrapper(), chunkSize);
        mDownloadThreadFactory = new DownloadThread.Factory(dependencies.getLog(),
                mDownloadStreamFactory);

        mUploadStreamFactory = new UploadStream.Factory(dependencies.getLog(),
                dependencies.getDracoonApi(), dependencies.getHttpClient(),
                dependencies.getHttpHelper(), dependencies.getDracoonErrorParser(),
                dependencies.getCryptoWrapper(), chunkSize);
        mUploadThreadFactory = new UploadThread.Factory(dependencies.getLog(),
                mUploadStreamFactory);
    }

    @Override
    public ServerInfoService getServerInfoService() {
        return mServerInfoService;
    }

    @Override
    public ServerSettingsService getServerSettingsService() {
        return mServerSettingsService;
    }

    @Override
    public ServerPoliciesService getServerPoliciesService() {
        return mServerPoliciesService;
    }

    @Override
    public AccountService getAccountService() {
        return mAccountService;
    }

    @Override
    public UsersService getUsersService() {
        return mUsersService;
    }

    @Override
    public NodesService getNodesService() {
        return mNodesService;
    }

    @Override
    public SharesService getSharesService() {
        return mSharesService;
    }

    @Override
    public FileKeyFetcher getFileKeyFetcher() {
        return mFileKeyFetcher;
    }

    @Override
    public FileKeyGenerator getFileKeyGenerator() {
        return mFileKeyGenerator;
    }

    @Override
    public AvatarDownloader getAvatarDownloader() {
        return mAvatarDownloader;
    }

    @Override
    public DownloadStream.Factory getDownloadStreamFactory() {
        return mDownloadStreamFactory;
    }

    @Override
    public DownloadThread.Factory getDownloadThreadFactory() {
        return mDownloadThreadFactory;
    }

    @Override
    public UploadStream.Factory getUploadStreamFactory() {
        return mUploadStreamFactory;
    }

    @Override
    public UploadThread.Factory getUploadThreadFactory() {
        return mUploadThreadFactory;
    }

    @Override
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
