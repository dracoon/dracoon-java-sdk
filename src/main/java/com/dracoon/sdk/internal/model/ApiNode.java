package com.dracoon.sdk.internal.model;

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
    public String expireAt;

    public String createdAt;
    public ApiUserInfo createdBy;
    public String updatedAt;
    public ApiUserInfo updatedBy;

    public ApiNodePermissions permissions;
    public Boolean inheritPermissions;

    public Boolean isFavorite;
    public Boolean isEncrypted;
    public ApiEncryptionInfo encryptionInfo;
    public Integer cntChildren;
    public Integer cntDeletedVersions;
    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;
    public Integer cntDownloadShares;
    public Integer cntUploadShares;
    public Long branchVersion;

    public String mediaToken;

    public ApiNode[] children;
}
