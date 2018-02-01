package com.dracoon.sdk.internal.model;

public class ApiDownloadShare {
    public Long id;
    public Long nodeId;
    public String nodePath;
    public String name;
    public Integer classification;
    public String notes;
    public String expireAt;

    public String accessKey;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;
    public Integer maxDownloads;
    public Integer cntDownloads;

    public String createdAt;
    public ApiUserInfo createdBy;

    public Boolean isProtected;
    public Boolean isEncrypted;
}
