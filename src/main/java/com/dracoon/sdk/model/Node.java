package com.dracoon.sdk.model;

import java.util.Date;

public class Node {

    private Long mId;
    private NodeType mType;
    private Long mParentId;
    private String mParentPath;
    private String mName;

    private String mFileType;
    private String mMediaType;
    private Long mSize;
    private Long mQuota;
    private Classification mClassification;
    private String mNotes;
    private String mHash;
    private Date mExpireAt;

    private Date mCreatedAt;
    private UserInfo mCreatedBy;
    private Date mUpdatedAt;
    private UserInfo mUpdatedBy;

    private Boolean mHasInheritPermissions;
    private NodePermissions mPermissions;

    private Boolean mIsFavorite;
    private Boolean mIsEncrypted;
    private Integer mCntChildren;
    private Integer mCntDeletedVersions;
    private Boolean mHasRecycleBin;
    private Integer mRecycleBinRetentionPeriod;
    private Integer mCntDownloadShares;
    private Integer mCntUploadShares;
    private Long mBranchVersion;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public NodeType getType() {
        return mType;
    }

    public void setType(NodeType type) {
        mType = type;
    }

    public Long getParentId() {
        return mParentId;
    }

    public void setParentId(Long parentId) {
        mParentId = parentId;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public void setParentPath(String parentPath) {
        mParentPath = parentPath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    public Long getSize() {
        return mSize;
    }

    public void setSize(Long size) {
        mSize = size;
    }

    public Long getQuota() {
        return mQuota;
    }

    public void setQuota(Long quota) {
        mQuota = quota;
    }

    public Classification getClassification() {
        return mClassification;
    }

    public void setClassification(Classification classification) {
        mClassification = classification;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(String hash) {
        mHash = hash;
    }

    public Date getExpireAt() {
        return mExpireAt;
    }

    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public UserInfo getCreatedBy() {
        return mCreatedBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        mCreatedBy = createdBy;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public UserInfo getUpdatedBy() {
        return mUpdatedBy;
    }

    public void setUpdatedBy(UserInfo updatedBy) {
        mUpdatedBy = updatedBy;
    }

    public Boolean hasInheritPermissions() {
        return mHasInheritPermissions;
    }

    public void setHasInheritPermissions(Boolean hasInheritPermissions) {
        mHasInheritPermissions = hasInheritPermissions;
    }

    public NodePermissions getPermissions() {
        return mPermissions;
    }

    public void setPermissions(NodePermissions permissions) {
        mPermissions = permissions;
    }

    public Boolean isFavorite() {
        return mIsFavorite;
    }

    public void setIsFavorite(Boolean favorite) {
        mIsFavorite = favorite;
    }

    public Boolean isEncrypted() {
        return mIsEncrypted;
    }

    public void setIsEncrypted(Boolean encrypted) {
        mIsEncrypted = encrypted;
    }

    public Integer getCntChildren() {
        return mCntChildren;
    }

    public void setCntChildren(Integer cntChildren) {
        mCntChildren = cntChildren;
    }

    public Integer getCntDeletedVersions() {
        return mCntDeletedVersions;
    }

    public void setCntDeletedVersions(Integer cntDeletedVersions) {
        mCntDeletedVersions = cntDeletedVersions;
    }

    public Boolean hasRecycleBin() {
        return mHasRecycleBin;
    }

    public void setHasRecycleBin(Boolean hasRecycleBin) {
        mHasRecycleBin = hasRecycleBin;
    }

    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    public void setRecycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
        mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
    }

    public Integer getCntDownloadShares() {
        return mCntDownloadShares;
    }

    public void setCntDownloadShares(Integer cntDownloadShares) {
        mCntDownloadShares = cntDownloadShares;
    }

    public Integer getCntUploadShares() {
        return mCntUploadShares;
    }

    public void setCntUploadShares(Integer cntUploadShares) {
        mCntUploadShares = cntUploadShares;
    }

    public Long getBranchVersion() {
        return mBranchVersion;
    }

    public void setBranchVersion(Long branchVersion) {
        mBranchVersion = branchVersion;
    }

}
