package com.dracoon.sdk.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dracoon.sdk.internal.service.Service;
import com.dracoon.sdk.internal.service.ServiceLocator;
import com.dracoon.sdk.internal.service.SharesService;
import com.dracoon.sdk.internal.service.UploadStream;
import com.dracoon.sdk.internal.service.UploadThread;
import com.dracoon.sdk.internal.service.UsersService;

public class TestServiceLocator implements ServiceLocator {

    private final Map<Class<?>, Object> mServices = new HashMap<>();

    public <T> void set(Class<?> serviceClass, T service) {
        mServices.put(serviceClass, service);
    }

    private <T> T get(Class<T> serviceClass) {
        T service = (T) mServices.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("Service " + serviceClass + " not found");
        }
        return service;
    }

    @Override
    public ServerInfoService getServerInfoService() {
        return get(ServerInfoService.class);
    }

    @Override
    public ServerSettingsService getServerSettingsService() {
        return get(ServerSettingsService.class);
    }

    @Override
    public ServerPoliciesService getServerPoliciesService() {
        return get(ServerPoliciesService.class);
    }

    @Override
    public AccountService getAccountService() {
        return get(AccountService.class);
    }

    @Override
    public UsersService getUsersService() {
        return get(UsersService.class);
    }

    @Override
    public NodesService getNodesService() {
        return get(NodesService.class);
    }

    @Override
    public SharesService getSharesService() {
        return get(SharesService.class);
    }

    @Override
    public FileKeyFetcher getFileKeyFetcher() {
        return get(FileKeyFetcher.class);
    }

    @Override
    public FileKeyGenerator getFileKeyGenerator() {
        return get(FileKeyGenerator.class);
    }

    @Override
    public AvatarDownloader getAvatarDownloader() {
        return get(AvatarDownloader.class);
    }

    @Override
    public DownloadStream.Factory getDownloadStreamFactory() {
        return get(DownloadStream.Factory.class);
    }

    @Override
    public DownloadThread.Factory getDownloadThreadFactory() {
        return get(DownloadThread.Factory.class);
    }

    @Override
    public UploadStream.Factory getUploadStreamFactory() {
        return get(UploadStream.Factory.class);
    }

    @Override
    public UploadThread.Factory getUploadThreadFactory() {
        return get(UploadThread.Factory.class);
    }

    @Override
    public List<Service> getServices() {
        return mServices.entrySet()
                .stream()
                .filter(s -> (s instanceof Service))
                .map(s -> (Service) s)
                .collect(Collectors.toList());
    }

}
