package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Node model.<br>
 * <br>
 * Node is generic term for all file system objects in Dracoon. Rooms, folders and files are
 * nodes.<br>
 * <br>
 * This model stores information about a node.
 */
@SuppressWarnings("unused")
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

    /**
     * Returns the ID of the node.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the node.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the type of the node.
     *
     * @return the type
     */
    public NodeType getType() {
        return mType;
    }

    /**
     * Sets the type of the node.
     *
     * @param type The type.
     */
    public void setType(NodeType type) {
        mType = type;
    }

    /**
     * Returns the ID of the parent node of the node.
     *
     * @return the ID
     */
    public Long getParentId() {
        return mParentId;
    }

    /**
     * Sets the ID of the parent node of the node.
     *
     * @param parentId The ID.
     */
    public void setParentId(Long parentId) {
        mParentId = parentId;
    }

    /**
     * Returns the file path of the parent node of the node.
     *
     * @return the file path
     */
    public String getParentPath() {
        return mParentPath;
    }

    /**
     * Sets the file path of the parent node of the node.
     *
     * @param parentPath The file path.
     */
    public void setParentPath(String parentPath) {
        mParentPath = parentPath;
    }

    /**
     * Returns the name of the node.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the node.
     *
     * @param name The name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the file type of the node.
     *
     * @return the file type
     */
    public String getFileType() {
        return mFileType;
    }

    /**
     * Sets the file type of the node.
     *
     * @param fileType The file type.
     */
    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    /**
     * Returns the MIME type of the node.
     *
     * @return the MIME type
     */
    public String getMediaType() {
        return mMediaType;
    }

    /**
     * Sets the MIME type of the node.
     *
     * @param mediaType The MIME type.
     */
    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    /**
     * Returns the size of the node.
     *
     * @return the size in bytes
     */
    public Long getSize() {
        return mSize;
    }

    /**
     * Sets the size of the node.
     *
     * @param size The size in bytes.
     */
    public void setSize(Long size) {
        mSize = size;
    }

    /**
     * Returns the quota on the node.
     *
     * @return the quota in bytes
     */
    public Long getQuota() {
        return mQuota;
    }

    /**
     * Sets the quota on the node.
     *
     * @param quota The quota in bytes.
     */
    public void setQuota(Long quota) {
        mQuota = quota;
    }

    /**
     * Returns the classification of the node.
     *
     * @return the classification
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * Sets the classification of the node.
     *
     * @param classification The classification.
     */
    public void setClassification(Classification classification) {
        mClassification = classification;
    }

    /**
     * Returns the notes which are attached to the node.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Sets the notes which are attached to the node.
     *
     * @param notes The notes.
     */
    public void setNotes(String notes) {
        mNotes = notes;
    }

    /**
     * Returns the MD5 hash of the node, if the node is a file.
     *
     * @return the MD5 hash, or null
     */
    public String getHash() {
        return mHash;
    }

    /**
     * Sets the MD5 hash of the node.
     *
     * @param hash The MD5 hash.
     */
    public void setHash(String hash) {
        mHash = hash;
    }

    /**
     * Returns the expire date of the node.
     *
     * @return the expire date
     */
    public Date getExpireAt() {
        return mExpireAt;
    }

    /**
     * Sets the expire date of the node.
     *
     * @param expireAt The expire date.
     */
    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    /**
     * Returns the creation date of the node.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    /**
     * Sets the creation date of the node.
     *
     * @param createdAt The creation date.
     */
    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    /**
     * Returns information about the user who created the node.
     *
     * @return information about the user
     */
    public UserInfo getCreatedBy() {
        return mCreatedBy;
    }

    /**
     * Sets information about the user who created the node.
     *
     * @param createdBy Information about the user.
     */
    public void setCreatedBy(UserInfo createdBy) {
        mCreatedBy = createdBy;
    }

    /**
     * Returns the update date of the node.
     *
     * @return the update date
     */
    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    /**
     * Sets the update date of the node.
     *
     * @param updatedAt The update date.
     */
    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    /**
     * Returns information about the user who updated the node.
     *
     * @return information about the user
     */
    public UserInfo getUpdatedBy() {
        return mUpdatedBy;
    }

    /**
     * Sets information about the user who updated the node.
     *
     * @param updatedBy Information about the user.
     */
    public void setUpdatedBy(UserInfo updatedBy) {
        mUpdatedBy = updatedBy;
    }

    /**
     * Returns <code>true</code> if node inherits permissions of the parent node.
     *
     * @return <code>true</code> if node inherits permissions of the parent node; <code>false</code>
     *         otherwise
     */
    public Boolean hasInheritPermissions() {
        return mHasInheritPermissions;
    }

    /**
     * Sets if node inherits permissions of the parent node.
     *
     * @param hasInheritPermissions <code>true</code> if node inherits permissions of the parent
     *                              node; <code>false</code> otherwise.
     */
    public void setHasInheritPermissions(Boolean hasInheritPermissions) {
        mHasInheritPermissions = hasInheritPermissions;
    }

    /**
     * Returns the permissions of the current user on the node.
     *
     * @return the permissions of the current user
     */
    public NodePermissions getPermissions() {
        return mPermissions;
    }

    /**
     * Sets the permissions of the current user on the node.
     *
     * @param permissions The permissions of the current user.
     */
    public void setPermissions(NodePermissions permissions) {
        mPermissions = permissions;
    }

    /**
     * Returns <code>true</code> if node is a favorite of the current user.
     *
     * @return <code>true</code> if node is a favorite of the current user; <code>false</code>
     *         otherwise
     */
    public Boolean isFavorite() {
        return mIsFavorite;
    }

    /**
     * Sets if node is a favorite of the current user.
     *
     * @param favorite <code>true</code> if node is a favorite of the current user;
     *                 <code>false</code> otherwise.
     */
    public void setIsFavorite(Boolean favorite) {
        mIsFavorite = favorite;
    }

    /**
     * Returns <code>true</code> if node is encrypted.
     *
     * @return <code>true</code> if node is encrypted; <code>false</code> otherwise
     */
    public Boolean isEncrypted() {
        return mIsEncrypted;
    }

    /**
     * Sets if node is encrypted.
     *
     * @param encrypted <code>true</code> if node is encrypted; <code>false</code> otherwise.
     */
    public void setIsEncrypted(Boolean encrypted) {
        mIsEncrypted = encrypted;
    }

    /**
     * Returns the number of child nodes of the node.
     *
     * @return the number of child nodes
     */
    public Integer getCntChildren() {
        return mCntChildren;
    }

    /**
     * Sets the number of child nodes of the node.
     *
     * @param cntChildren The number of child nodes.
     */
    public void setCntChildren(Integer cntChildren) {
        mCntChildren = cntChildren;
    }

    /**
     * Returns the number of deleted versions of the node.
     *
     * @return the number of deleted versions
     */
    public Integer getCntDeletedVersions() {
        return mCntDeletedVersions;
    }

    /**
     * Sets the number of deleted versions of the node.
     *
     * @param cntDeletedVersions The number of deleted versions.
     */
    public void setCntDeletedVersions(Integer cntDeletedVersions) {
        mCntDeletedVersions = cntDeletedVersions;
    }

    /**
     * Returns <code>true</code> if node has recycle bin enabled.
     *
     * @return <code>true</code> if node has recycle bin enabled; <code>false</code> otherwise
     */
    public Boolean hasRecycleBin() {
        return mHasRecycleBin;
    }

    /**
     * Sets if node has recycle bin enabled.
     *
     * @param hasRecycleBin <code>true</code> if node has recycle bin enabled; <code>false</code>
     *                      otherwise.
     */
    public void setHasRecycleBin(Boolean hasRecycleBin) {
        mHasRecycleBin = hasRecycleBin;
    }

    /**
     * Returns the recycle bin retention period of the node.
     *
     * @return the recycle bin retention period
     */
    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    /**
     * Sets the recycle bin retention period of the node.
     *
     * @param recycleBinRetentionPeriod The recycle bin retention period.
     */
    public void setRecycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
        mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
    }

    /**
     * Returns the number of download shares on the node. (Only for files.)
     *
     * @return the number of download shares
     */
    public Integer getCntDownloadShares() {
        return mCntDownloadShares;
    }

    /**
     * Sets the number of download shares on the node. (Only for files.)
     *
     * @param cntDownloadShares The number of download shares.
     */
    public void setCntDownloadShares(Integer cntDownloadShares) {
        mCntDownloadShares = cntDownloadShares;
    }

    /**
     * Returns the number of upload shares on the node. (Only for rooms and folders.)
     *
     * @return the number of upload shares
     */
    public Integer getCntUploadShares() {
        return mCntUploadShares;
    }

    /**
     * Sets the number of upload shares on the node. (Only for rooms and folders.)
     *
     * @param cntUploadShares The number of upload shares.
     */
    public void setCntUploadShares(Integer cntUploadShares) {
        mCntUploadShares = cntUploadShares;
    }

    /**
     * Returns the branch version of the node. (A counter which increases with every change.)
     *
     * @return the branch version
     */
    public Long getBranchVersion() {
        return mBranchVersion;
    }

    /**
     * Sets the branch version of the node. (A counter which increases with every change.)
     *
     * @param branchVersion The branch version.
     */
    public void setBranchVersion(Long branchVersion) {
        mBranchVersion = branchVersion;
    }

}
