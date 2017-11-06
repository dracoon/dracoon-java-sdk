package com.dracoon.sdk.internal.model;

public class ApiNode {
    public Long id;
    public String type;
    public Long parentId;
    public String parentPath;
    public String createdAt;
    public ApiUserInfo createdBy;
    public String updatedAt;
    public ApiUserInfo updatedBy;
    public String expireAt;
    public String name;
    public String hash;
    public String fileType;
    public String mediaType;
    public Long size;
    public Integer classification;
    public String notes;
    public ApiNodePermissions permissions;
    public Boolean isEncrypted;
    public Integer cntChildren;
    public Integer cntDeletedVersions;
    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;
    public Long quota;
    public Integer cntDownloadShares;
    public Integer cntUploadShares;
    public Boolean isFavorite;
    public Boolean inheritPermissions;
    public ApiEncryptionInfo encryptionInfo;
    public Long branchVersion;
    public String mediaToken;
    public ApiNode[] children;
}
