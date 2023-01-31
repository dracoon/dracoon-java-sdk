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
    public String internalNotes;
    public Date expireAt;
    public Integer filesExpiryPeriod;
    public Integer maxSlots;
    public Long maxSize;
    public Boolean showUploadedFiles;

    public String accessKey;
    public Integer cntUploads;
    public Integer cntFiles;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;

    public Date createdAt;
    public ApiUserInfo createdBy;

    public Boolean isProtected;
    public Boolean isEncrypted;

    public String dataUrl;
}
