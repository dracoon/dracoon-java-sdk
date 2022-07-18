package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
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
