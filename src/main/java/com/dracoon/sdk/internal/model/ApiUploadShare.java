package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings("unused")
public class ApiUploadShare {
    public Long id;
    public Long targetId;
    public String targetPath;
    public String name;
    public String notes;
    public Date expireAt;
    public Integer filesExpiryPeriod;
    public Integer maxSlots;
    public Long maxSize;

    public String accessKey;

    public Boolean showUploadedFiles;
    public Boolean notifyCreator;
    public Integer cntUploads;
    public Integer cntFiles;

    public Date createdAt;
    public ApiUserInfo createdBy;

    public Boolean isProtected;
    public Boolean isEncrypted;

    public String dataUrl;
}
