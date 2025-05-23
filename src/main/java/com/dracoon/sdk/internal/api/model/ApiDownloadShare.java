package com.dracoon.sdk.internal.api.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiDownloadShare {
    public Long id;
    public Long nodeId;
    public String nodePath;
    public String name;
    public String notes;
    public String internalNotes;
    public Date expireAt;
    public Integer maxDownloads;

    public String accessKey;
    public Integer cntDownloads;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;

    public Date createdAt;
    public ApiUserInfo createdBy;

    public Boolean isProtected;
    public Boolean isEncrypted;

    public String dataUrl;
}
