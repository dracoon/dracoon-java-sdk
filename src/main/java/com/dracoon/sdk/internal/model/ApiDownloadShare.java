package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings("unused")
public class ApiDownloadShare {
    public Long id;
    public Long nodeId;
    public String nodePath;
    public String name;
    public String notes;
    public Date expireAt;

    public String accessKey;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;
    public Integer maxDownloads;
    public Integer cntDownloads;

    public Date createdAt;
    public ApiUserInfo createdBy;

    public Boolean isProtected;
    public Boolean isEncrypted;

    public String dataUrl;
}
