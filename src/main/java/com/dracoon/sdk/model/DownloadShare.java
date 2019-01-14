package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Download share model.<br>
 * <br>
 * This model stores information about a download share.
 */
@SuppressWarnings("unused")
public class DownloadShare {

    private Long mId;
    private Long mNodeId;
    private String mNodePath;
    private String mName;
    private Classification mClassification;
    private String mNotes;
    private Date mExpireAt;

    private String mAccessKey;

    private Boolean mShowsCreatorName;
    private Boolean mShowsCreatorUserName;
    private Boolean mNotifiesCreator;
    private Integer mMaxDownloads;
    private Integer mCntDownloads;

    private Date mCreatedAt;
    private UserInfo mCreatedBy;

    private Boolean mIsProtected;
    private Boolean mIsEncrypted;

    /**
     * Returns the ID of the download share.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the download share.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the node ID of the download share.
     *
     * @return the node ID
     */
    public Long getNodeId() {
        return mNodeId;
    }

    /**
     * Sets the node ID of the download share.
     *
     * @param nodeId The node ID.
     */
    public void setNodeId(Long nodeId) {
        mNodeId = nodeId;
    }

    /**
     * Returns the node path of the download share.
     *
     * @return the node path
     */
    public String getNodePath() {
        return mNodePath;
    }

    /**
     * Sets the node path of the download share.
     *
     * @param nodePath The node path.
     */
    public void setNodePath(String nodePath) {
        mNodePath = nodePath;
    }

    /**
     * Returns the name of the download share, if download share has a name.
     *
     * @return the name, or <code>null</code>
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the download share.
     *
     * @param name The name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the classification of the download share.
     *
     * @return the classification
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * Sets the classification of the download share.
     *
     * @param classification The classification
     */
    public void setClassification(Classification classification) {
        mClassification = classification;
    }

    /**
     * Returns the notes which are attached to the download share, if download share has notes.
     *
     * @return the notes, or <code>null</code>
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Sets the notes which are attached to the download share.
     *
     * @param notes The notes.
     */
    public void setNotes(String notes) {
        mNotes = notes;
    }

    /**
     * Returns the expire date of the download share, if download share has a expire date.
     *
     * @return the expire date, or <code>null</code>
     */
    public Date getExpireAt() {
        return mExpireAt;
    }

    /**
     * Sets the expire date of the download share.
     *
     * @param expireAt The expire date.
     */
    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    /**
     * Returns the access key of the download share.
     *
     * @return the access key
     */
    public String getAccessKey() {
        return mAccessKey;
    }

    /**
     * Sets the access key of the download share.
     *
     * @param accessKey The access key.
     */
    public void setAccessKey(String accessKey) {
        mAccessKey = accessKey;
    }

    /**
     * Returns <code>true</code> if creator's name is shown for the download share.
     *
     * @return <code>true</code> if creator's name is shown; <code>false</code> otherwise
     */
    public Boolean showsCreatorName() {
        return mShowsCreatorName;
    }

    /**
     * Sets if creator's name is shown for the download share.
     *
     * @param showsCreatorName <code>true</code> if creator's name is shown; otherwise
     *                         <code>false</code>.
     */
    public void setShowsCreatorName(Boolean showsCreatorName) {
        mShowsCreatorName = showsCreatorName;
    }

    /**
     * Returns <code>true</code> if creator's user name is shown for the download share.
     *
     * @return <code>true</code> if creator's user name is shown; <code>false</code> otherwise
     */
    public Boolean showsCreatorUserName() {
        return mShowsCreatorUserName;
    }

    /**
     * Sets if creator's user name is shown for the download share.
     *
     * @param showsCreatorUserName <code>true</code> if creator's user name is shown; otherwise
     *                             <code>false</code>.
     */
    public void setShowsCreatorUserName(Boolean showsCreatorUserName) {
        mShowsCreatorUserName = showsCreatorUserName;
    }

    /**
     * Returns <code>true</code> if creator is notified at downloads of the download share.
     *
     * @return <code>true</code> if creator is notified; <code>false</code> otherwise
     */
    public Boolean notifiesCreator() {
        return mNotifiesCreator;
    }

    /**
     * Sets if creator is notified at downloads of the download share.
     *
     * @param notifiesCreator <code>true</code> if creator is notified; otherwise
     *                        <code>false</code>.
     */
    public void setNotifiesCreator(Boolean notifiesCreator) {
        mNotifiesCreator = notifiesCreator;
    }

    /**
     * Returns the maximum number of downloads of the download share.
     *
     * @return the maximum number of downloads
     */
    public Integer getMaxDownloads() {
        return mMaxDownloads;
    }

    /**
     * Sets the maximum number of downloads of the download share.
     *
     * @param maxDownloads The maximum number of downloads.
     */
    public void setMaxDownloads(Integer maxDownloads) {
        mMaxDownloads = maxDownloads;
    }

    /**
     * Returns the current number of downloads of the download share.
     *
     * @return the current number of downloads
     */
    public Integer getCntDownloads() {
        return mCntDownloads;
    }

    /**
     * Sets the current number of downloads of the download share.
     *
     * @param cntDownloads The current number of downloads.
     */
    public void setCntDownloads(Integer cntDownloads) {
        mCntDownloads = cntDownloads;
    }

    /**
     * Returns the creation date of the download share.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    /**
     * Sets the creation date of the download share.
     *
     * @param createdAt The creation date.
     */
    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    /**
     * Returns information about the user who created the download share.
     *
     * @return information about the user
     */
    public UserInfo getCreatedBy() {
        return mCreatedBy;
    }

    /**
     * Sets information about the user who created the download share.
     *
     * @param createdBy Information about the user.
     */
    public void setCreatedBy(UserInfo createdBy) {
        mCreatedBy = createdBy;
    }

    /**
     * Returns <code>true</code> if download share node is protected.
     *
     * @return <code>true</code> if download share node is protected; <code>false</code> otherwise
     */
    public Boolean isProtected() {
        return mIsProtected;
    }

    /**
     * Sets if download share node is protected.
     *
     * @param isProtected <code>true</code> if download share node is protected; <code>false</code>
     *                    otherwise.
     */
    public void setIsProtected(Boolean isProtected) {
        mIsProtected = isProtected;
    }

    /**
     * Returns <code>true</code> if download share node is encrypted.
     *
     * @return <code>true</code> if download share node is encrypted; <code>false</code> otherwise
     */
    public Boolean isEncrypted() {
        return mIsEncrypted;
    }

    /**
     * Sets if download share node is encrypted.
     *
     * @param isEncrypted <code>true</code> if download share node is encrypted; <code>false</code>
     *                    otherwise.
     */
    public void setIsEncrypted(Boolean isEncrypted) {
        mIsEncrypted = isEncrypted;
    }

}
