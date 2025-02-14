package com.dracoon.sdk.internal;

import com.dracoon.sdk.internal.service.AccountService;
import com.dracoon.sdk.internal.service.AvatarDownloader;
import com.dracoon.sdk.internal.service.DownloadStream;
import com.dracoon.sdk.internal.service.DownloadThread;
import com.dracoon.sdk.internal.service.FileKeyFetcher;
import com.dracoon.sdk.internal.service.FileKeyGenerator;
import com.dracoon.sdk.internal.service.NodesService;
import com.dracoon.sdk.internal.service.ServerInfoService;
import com.dracoon.sdk.internal.service.ServerPoliciesService;
import com.dracoon.sdk.internal.service.ServerSettingsService;
import com.dracoon.sdk.internal.service.ServiceLocator;
import com.dracoon.sdk.internal.service.SharesService;
import com.dracoon.sdk.internal.service.UploadStream;
import com.dracoon.sdk.internal.service.UploadThread;
import com.dracoon.sdk.internal.service.UsersService;

public class TestServiceLocator extends ServiceLocator {

    public TestServiceLocator() {
        super(null);
    }

    protected void init(DracoonClientImpl client) {

    }

    public void setServerInfoService(ServerInfoService service) {
        mServerInfoService = service;
    }

    public void setServerSettingsService(ServerSettingsService service) {
        mServerSettingsService = service;
    }

    public void setServerPoliciesService(ServerPoliciesService service) {
        mServerPoliciesService = service;
    }

    public void setAccountService(AccountService service) {
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

    public void setDownloadStreamFactory(DownloadStream.Factory factory) {
        mDownloadStreamFactory = factory;
    }

    public void setDownloadThreadFactory(DownloadThread.Factory factory) {
        mDownloadThreadFactory = factory;
    }

    public void setUploadStreamFactory(UploadStream.Factory factory) {
        mUploadStreamFactory = factory;
    }

    public void setUploadThreadFactory(UploadThread.Factory factory) {
        mUploadThreadFactory = factory;
    }

}
