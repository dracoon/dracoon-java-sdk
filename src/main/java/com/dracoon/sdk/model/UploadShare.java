package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Upload share model.<br>
 * <br>
 * This model stores information about a upload share.
 */
@SuppressWarnings("unused")
public class UploadShare {

    private Long mId;
    private Long mTargetNodeId;
    private String mTargetNodePath;
    private String mName;
    private String mNotes;
    private Date mExpireAt;
    private Integer mFilesExpirePeriod;
    private Integer mMaxUploads;
    private Long mMaxQuota;

    private String mAccessKey;

    private Boolean mShowsUploadedFiles;
    private Boolean mNotifiesCreator;
    private Integer mCntUploads;
    private Integer mCntFiles;

    private Date mCreatedAt;
    private UserInfo mCreatedBy;

    private Boolean mIsProtected;
    private Boolean mIsEncrypted;

    /**
     * Returns the ID of the up share.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the up share.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the target node ID of the up share.
     *
     * @return the target node ID
     */
    public Long getTargetNodeId() {
        return mTargetNodeId;
    }

    /**
     * Sets the target node ID of the up share.
     *
     * @param targetNodeId The target node ID.
     */
    public void setTargetNodeId(Long targetNodeId) {
        mTargetNodeId = targetNodeId;
    }

    /**
     * Returns the target node path of the up share.
     *
     * @return the target node path
     */
    public String getTargetNodePath() {
        return mTargetNodePath;
    }

    /**
     * Sets the target node path of the up share.
     *
     * @param targetNodePath The target node path.
     */
    public void setTargetNodePath(String targetNodePath) {
        mTargetNodePath = targetNodePath;
    }

    /**
     * Returns the name of the up share, if up share has a name.
     *
     * @return the name, or <code>null</code>
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the up share.
     *
     * @param name The name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the notes which are attached to the up share, if up share has notes.
     *
     * @return the notes, or <code>null</code>
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Sets the notes which are attached to the up share.
     *
     * @param notes The notes.
     */
    public void setNotes(String notes) {
        mNotes = notes;
    }

    /**
     * Returns the expire date of the up share, if up share has a expire date.
     *
     * @return the expire date, or <code>null</code>
     */
    public Date getExpireAt() {
        return mExpireAt;
    }

    /**
     * Sets the expire date of the up share.
     *
     * @param expireAt The expire date.
     */
    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    /**
     * Returns the expire period of files of the upload share, if upload share has a expire period
     * of files.
     *
     * @return the expire period of files, or <code>null</code>
     */
    public Integer getFilesExpirePeriod() {
        return mFilesExpirePeriod;
    }

    /**
     * Sets the expire period of files of the up share.
     *
     * @param filesExpirePeriod The expire period of files.
     */
    public void setFilesExpirePeriod(Integer filesExpirePeriod) {
        mFilesExpirePeriod = filesExpirePeriod;
    }

    /**
     * Returns the access key of the up share.
     *
     * @return the access key
     */
    public String getAccessKey() {
        return mAccessKey;
    }

    /**
     * Sets the access key of the up share.
     *
     * @param accessKey The access key.
     */
    public void setAccessKey(String accessKey) {
        mAccessKey = accessKey;
    }

    /**
     * Returns <code>true</code> if uploaded files are shown for the upload share.
     *
     * @return <code>true</code> if uploaded files are shown; <code>false</code> otherwise
     */
    public Boolean showsUploadedFiles() {
        return mShowsUploadedFiles;
    }

    /**
     * Sets if uploaded files are shown for the upload share.
     *
     * @param showsUploadedFiles <code>true</code> if uploaded files are shown; otherwise
     *                           <code>false</code>.
     */
    public void setShowsUploadedFiles(Boolean showsUploadedFiles) {
        mShowsUploadedFiles = showsUploadedFiles;
    }

    /**
     * Returns <code>true</code> if creator is notified at ups of the up share.
     *
     * @return <code>true</code> if creator is notified; <code>false</code> otherwise
     */
    public Boolean notifiesCreator() {
        return mNotifiesCreator;
    }

    /**
     * Sets if creator is notified at ups of the up share.
     *
     * @param notifiesCreator <code>true</code> if creator is notified; otherwise
     *                        <code>false</code>.
     */
    public void setNotifiesCreator(Boolean notifiesCreator) {
        mNotifiesCreator = notifiesCreator;
    }

    /**
     * Returns the current number of uploads to the upload share.
     *
     * @return the current number of uploads
     */
    public Integer getCntUploads() {
        return mCntUploads;
    }

    /**
     * Sets the current number of uploads to the upload share.
     *
     * @param cntUploads The current number of uploads.
     */
    public void setCntUploads(Integer cntUploads) {
        mCntUploads = cntUploads;
    }

    /**
     * Returns the current number of files of the upload share.
     *
     * @return the current number of files
     */
    public Integer getCntFiles() {
        return mCntFiles;
    }

    /**
     * Sets the current number of files of the upload share.
     *
     * @param cntFiles The current number of files.
     */
    public void setCntFiles(Integer cntFiles) {
        mCntFiles = cntFiles;
    }

    /**
     * Returns the creation date of the up share.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    /**
     * Sets the creation date of the up share.
     *
     * @param createdAt The creation date.
     */
    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    /**
     * Returns information about the user who created the up share.
     *
     * @return information about the user
     */
    public UserInfo getCreatedBy() {
        return mCreatedBy;
    }

    /**
     * Sets information about the user who created the up share.
     *
     * @param createdBy Information about the user.
     */
    public void setCreatedBy(UserInfo createdBy) {
        mCreatedBy = createdBy;
    }

    /**
     * Returns <code>true</code> if up share node is protected.
     *
     * @return <code>true</code> if up share node is protected; <code>false</code> otherwise
     */
    public Boolean isProtected() {
        return mIsProtected;
    }

    /**
     * Sets if up share node is protected.
     *
     * @param isProtected <code>true</code> if up share node is protected; <code>false</code>
     *                    otherwise.
     */
    public void setIsProtected(Boolean isProtected) {
        mIsProtected = isProtected;
    }

    /**
     * Returns <code>true</code> if up share node is encrypted.
     *
     * @return <code>true</code> if up share node is encrypted; <code>false</code> otherwise
     */
    public Boolean isEncrypted() {
        return mIsEncrypted;
    }

    /**
     * Sets if up share node is encrypted.
     *
     * @param isEncrypted <code>true</code> if up share node is encrypted; <code>false</code>
     *                    otherwise.
     */
    public void setIsEncrypted(Boolean isEncrypted) {
        mIsEncrypted = isEncrypted;
    }

    /**
     * Returns the maximum number of uploads of the uploads share.
     *
     * @return the maximum number of uploads
     */
    public Integer getMaxUploads() {
        return mMaxUploads;
    }

    /**
     * Sets the maximum number of uploads of the upload share.
     *
     * @param maxUploads The maximum number of uploads.
     */
    public void setMaxUploads(Integer maxUploads) {
        this.mMaxUploads = maxUploads;
    }

    /**
     * Returns the maximum number of bytes which can be uploaded by this upload share.
     *
     * @return the maximum number of bytes
     */
    public Long getMaxQuota() {
        return mMaxQuota;
    }

    /**
     * Sets the maximum number of bytes which can be uploaded by this upload share.
     *
     * @param maxQuota The maximum number of bytes.
     */
    public void setMaxQuota(Long maxQuota) {
        this.mMaxQuota = maxQuota;
    }
}
