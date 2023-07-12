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
    private String mExtension;

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

    private Date mOriginalCreatedAt;
    private Date mOriginalModifiedAt;

    private Boolean mHasInheritPermissions;
    private NodePermissions mPermissions;

    private Boolean mIsFavorite;
    private Boolean mIsEncrypted;
    private Integer mCntChildRooms;
    private Integer mCntChildFolders;
    private Integer mCntChildFiles;
    private Integer mCntDeletedVersions;
    private Integer mRecycleBinRetentionPeriod;
    private Integer mCntComments;
    private Integer mCntDownloadShares;
    private Integer mCntUploadShares;
    private Long mBranchVersion;

    private String mMediaToken;
    private VirusScanInfo mVirusScanInfo;

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
     * Returns the ID of the parent node of the node, if node is not a root node.
     *
     * @return the ID, or <code>null</code>
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
     * Returns the extension of the node, if the node is a file.
     *
     * @return the extension, or <code>null</code>
     */
    public String getExtension() {
        return mExtension;
    }

    /**
     * Sets the extension of the node.
     *
     * @param extension The extension.
     */
    public void setExtension(String extension) {
        mExtension = extension;
    }

    /**
     * Returns the MIME type of the node, if the node is a file.
     *
     * @return the MIME type, or <code>null</code>
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
     * Returns the quota on the node, if node is a room and has quota.
     *
     * @return the quota in bytes, or <code>null</code>
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
     * Returns the notes which are attached to the node, if node has notes.
     *
     * @return the notes, or <code>null</code>
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
     * @return the MD5 hash, or <code>null</code>
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
     * Returns the expire date of the node, if node is a file and has a expire date.
     *
     * @return the expire date, or <code>null</code>
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
     * Returns the original creation date of the node, if the node is a file and a creation date was
     * provided at the upload.
     *
     * @return the original creation date, or <code>null</code>
     */
    public Date getOriginalCreatedAt() {
        return mOriginalCreatedAt;
    }

    /**
     * Sets the original creation date of the node.
     *
     * @param originalCreatedAt The original creation date.
     */
    public void setOriginalCreatedAt(Date originalCreatedAt) {
        mOriginalCreatedAt = originalCreatedAt;
    }

    /**
     * Returns the original modification date of the node, if the node is a file and a modification
     * date was provided at the upload.
     *
     * @return the original modification date, or <code>null</code>
     */
    public Date getOriginalModifiedAt() {
        return mOriginalModifiedAt;
    }

    /**
     * Sets the original modification date of the node.
     *
     * @param originalModifiedAt The original modification date.
     */
    public void setOriginalModifiedAt(Date originalModifiedAt) {
        mOriginalModifiedAt = originalModifiedAt;
    }

    /**
     * Returns <code>true</code> if node inherits permissions of the parent node. (Only for rooms.)
     *
     * @return <code>true</code> if node inherits permissions of the parent node; <code>false</code>
     *         otherwise; <code>null</code> for folders and files
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
     * @param isFavorite <code>true</code> if node is a favorite of the current user;
     *                   <code>false</code> otherwise.
     */
    public void setIsFavorite(Boolean isFavorite) {
        mIsFavorite = isFavorite;
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
     * @param isEncrypted <code>true</code> if node is encrypted; <code>false</code> otherwise.
     */
    public void setIsEncrypted(Boolean isEncrypted) {
        mIsEncrypted = isEncrypted;
    }

    /**
     * Returns the number of child rooms of the node, if node is a room.
     *
     * @return the number of child rooms, or <code>null</code>
     */
    public Integer getCntChildRooms() {
        return mCntChildRooms;
    }

    /**
     * Sets the number of child rooms of the node.
     *
     * @param cntChildRooms The number of child rooms.
     */
    public void setCntChildRooms(Integer cntChildRooms) {
        mCntChildRooms = cntChildRooms;
    }

    /**
     * Returns the number of child folders of the node, if node is a room or folder.
     *
     * @return the number of child folders, or <code>null</code>
     */
    public Integer getCntChildFolders() {
        return mCntChildFolders;
    }

    /**
     * Sets the number of child folders of the node.
     *
     * @param cntChildFolders The number of child folders.
     */
    public void setCntChildFolders(Integer cntChildFolders) {
        mCntChildFolders = cntChildFolders;
    }

    /**
     * Returns the number of child files of the node, if node is a room or folder.
     *
     * @return the number of child files, or <code>null</code>
     */
    public Integer getCntChildFiles() {
        return mCntChildFiles;
    }

    /**
     * Sets the number of child files of the node.
     *
     * @param cntChildFiles The number of child files.
     */
    public void setCntChildFiles(Integer cntChildFiles) {
        mCntChildFiles = cntChildFiles;
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
     * Returns the recycle bin retention period (in days) of the node, if node is a room.
     *
     * @return the recycle bin retention period, or <code>null</code>
     */
    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    /**
     * Sets the recycle bin retention period (in days) of the node.
     *
     * @param recycleBinRetentionPeriod The recycle bin retention period.
     */
    public void setRecycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
        mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
    }

    /**
     * Returns the number of comments on the node.
     *
     * @return the number of comments, or <code>null</code>
     */
    public Integer getCntComments() {
        return mCntComments;
    }

    /**
     * Sets the number of comments on the node.
     *
     * @param cntComments The number of comments.
     */
    public void setCntComments(Integer cntComments) {
        mCntComments = cntComments;
    }

    /**
     * Returns the number of download shares on the node.
     *
     * @return the number of download shares, or <code>null</code>
     */
    public Integer getCntDownloadShares() {
        return mCntDownloadShares;
    }

    /**
     * Sets the number of download shares on the node.
     *
     * @param cntDownloadShares The number of download shares.
     */
    public void setCntDownloadShares(Integer cntDownloadShares) {
        mCntDownloadShares = cntDownloadShares;
    }

    /**
     * Returns the number of upload shares on the node, if node is a room or folder.
     *
     * @return the number of upload shares, or <code>null</code>
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

    /**
     * Returns the media token for the node, if a media server is available.<br>
     * <br>
     * A media token can be used to get a thumbnail or preview image for a node. To retrieve the
     * image the appropriate media URL must be generate first. It can be created via
     * {@link com.dracoon.sdk.DracoonClient.Nodes#buildMediaUrl buildMediaUrl} or by using the
     * following template.<br>
     * <br>
     * http(s)://[host]/mediaserver/image/[media-token]/[width]x[height]
     *
     * @return the media token
     */
    public String getMediaToken() {
        return mMediaToken;
    }

    /**
     * Sets the media token for the node.
     *
     * @param mMediaToken The media token.
     */
    public void setMediaToken(String mMediaToken) {
        this.mMediaToken = mMediaToken;
    }

    /**
     * Returns information about the virus scan of the node, if the node is a file.
     *
     * @return the information about the virus scan
     */
    public VirusScanInfo getVirusScanInfo() {
        return mVirusScanInfo;
    }

    /**
     * Sets information about the virus scan of the node.
     *
     * @param virusScanInfo The information about the virus scan.
     */
    public void setVirusScanInfo(VirusScanInfo virusScanInfo) {
        mVirusScanInfo = virusScanInfo;
    }

}
