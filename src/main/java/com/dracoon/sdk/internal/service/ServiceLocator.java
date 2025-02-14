package com.dracoon.sdk.internal.service;

import java.util.List;

public interface ServiceLocator {

    ServerInfoService getServerInfoService();
    ServerSettingsService getServerSettingsService();
    ServerPoliciesService getServerPoliciesService();
    AccountService getAccountService();
    UsersService getUsersService();
    NodesService getNodesService();
    SharesService getSharesService();
    FileKeyFetcher getFileKeyFetcher();
    FileKeyGenerator getFileKeyGenerator();
    AvatarDownloader getAvatarDownloader();
    DownloadStream.Factory getDownloadStreamFactory();
    DownloadThread.Factory getDownloadThreadFactory();
    UploadStream.Factory getUploadStreamFactory();
    UploadThread.Factory getUploadThreadFactory();
    List<Service> getServices();

}
