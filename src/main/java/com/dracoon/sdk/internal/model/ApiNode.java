package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiNode {
    public Long id;
    public String type;
    public Long parentId;
    public String parentPath;
    public String name;

    public String fileType;
    public String mediaType;
    public Long size;
    public Long quota;
    public Integer classification;
    public String notes;
    public String hash;
    public Date expireAt;

    public Date createdAt;
    public ApiUserInfo createdBy;
    public Date updatedAt;
    public ApiUserInfo updatedBy;

    public Date timestampCreation;
    public Date timestampModification;

    public ApiNodePermissions permissions;
    public Boolean inheritPermissions;

    public Boolean isFavorite;
    public Boolean isEncrypted;
    public ApiEncryptionInfo encryptionInfo;
    public Integer cntRooms;
    public Integer cntFolders;
    public Integer cntFiles;
    public Integer cntDeletedVersions;
    public Integer recycleBinRetentionPeriod;
    public Integer cntComments;
    public Integer cntDownloadShares;
    public Integer cntUploadShares;
    public Long branchVersion;

    public String mediaToken;
}
