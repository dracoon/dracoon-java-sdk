package com.dracoon.sdk.model;

/**
 * Node permission model.<br>
 * <br>
 * This model stores information about the permission a user has on a node.
 *
 * @see com.dracoon.sdk.model.Node
 */
@SuppressWarnings("unused")
public class NodePermissions {

    private Boolean mManage;
    private Boolean mRead;
    private Boolean mCreate;
    private Boolean mChange;
    private Boolean mDelete;
    private Boolean mManageDownloadShare;
    private Boolean mManageUploadShare;
    private Boolean mReadRecycleBin;
    private Boolean mRestoreRecycleBin;
    private Boolean mDeleteRecycleBin;

    /**
     * Returns <code>true</code> if a user has the manage permission on a data room.
     *
     * @return <code>true</code> if a user has the manage permission; <code>false</code> otherwise
     */
    public Boolean getManage() {
        return mManage;
    }

    /**
     * Sets if a user has the manage permission on a data room.
     *
     * @param manage <code>true</code> if a user has the manage permission; <code>false</code>
     *               otherwise.
     */
    public void setManage(Boolean manage) {
        mManage = manage;
    }

    /**
     * Returns <code>true</code> if a user has the read permission on a data room.
     *
     * @return <code>true</code> if a user has the read permission; <code>false</code> otherwise
     */
    public Boolean getRead() {
        return mRead;
    }

    /**
     * Sets if a user has the read permission on a data room.
     *
     * @param read <code>true</code> if a user has the read permission; <code>false</code>
     *             otherwise.
     */
    public void setRead(Boolean read) {
        mRead = read;
    }

    /**
     * Returns <code>true</code> if a user has the create permission on a data room.
     *
     * @return <code>true</code> if a user has the create permission; <code>false</code> otherwise
     */
    public Boolean getCreate() {
        return mCreate;
    }

    /**
     * Sets if a user has the create permission on a data room.
     *
     * @param create <code>true</code> if a user has the create permission; <code>false</code>
     *               otherwise.
     */
    public void setCreate(Boolean create) {
        mCreate = create;
    }

    /**
     * Returns <code>true</code> if a user has the change permission on a data room.
     *
     * @return <code>true</code> if a user has the change permission; <code>false</code> otherwise
     */
    public Boolean getChange() {
        return mChange;
    }

    /**
     * Sets if a user has the change permission on a data room.
     *
     * @param change <code>true</code> if a user has the change permission; <code>false</code>
     *               otherwise.
     */
    public void setChange(Boolean change) {
        mChange = change;
    }

    /**
     * Returns <code>true</code> if a user has the delete permission on a data room.
     *
     * @return <code>true</code> if a user has the delete permission; <code>false</code> otherwise
     */
    public Boolean getDelete() {
        return mDelete;
    }

    /**
     * Sets if a user has the delete permission on a data room.
     *
     * @param delete <code>true</code> if a user has the delete permission; <code>false</code>
     *               otherwise.
     */
    public void setDelete(Boolean delete) {
        mDelete = delete;
    }

    /**
     * Returns <code>true</code> if a user has the manage download shares permission on a data room.
     *
     * @return <code>true</code> if a user has the manage download shares permission;
     *         <code>false</code> otherwise
     */
    public Boolean getManageDownloadShare() {
        return mManageDownloadShare;
    }

    /**
     * Sets if a user has the manage download shares permission on a data room.
     *
     * @param manageDownloadShare <code>true</code> if a user has the manage download shares
     *                            permission; <code>false</code> otherwise.
     */
    public void setManageDownloadShare(Boolean manageDownloadShare) {
        mManageDownloadShare = manageDownloadShare;
    }

    /**
     * Returns <code>true</code> if a user has the manage upload shares permission on a data room.
     *
     * @return <code>true</code> if a user has the manage upload shares permission;
     *         <code>false</code> otherwise
     */
    public Boolean getManageUploadShare() {
        return mManageUploadShare;
    }

    /**
     * Sets if a user has the manage upload shares permission on a data room.
     *
     * @param manageUploadShare <code>true</code> if a user has the manage upload shares permission;
     *                          <code>false</code> otherwise.
     */
    public void setManageUploadShare(Boolean manageUploadShare) {
        mManageUploadShare = manageUploadShare;
    }

    /**
     * Returns <code>true</code> if a user has the read recycle bin permission on a data room.
     *
     * @return <code>true</code> if a user has the read recycle bin permission;
     *         <code>false</code> otherwise
     */
    public Boolean getReadRecycleBin() {
        return mReadRecycleBin;
    }

    /**
     * Sets if a user has the read recycle bin permission on a data room.
     *
     * @param readRecycleBin <code>true</code> if a user has the read recycle bin permission;
     *                       <code>false</code> otherwise.
     */
    public void setReadRecycleBin(Boolean readRecycleBin) {
        mReadRecycleBin = readRecycleBin;
    }

    /**
     * Returns <code>true</code> if a user has the restore recycle bin permission on a data room.
     *
     * @return <code>true</code> if a user has the restore recycle bin permission;
     *         <code>false</code> otherwise
     */
    public Boolean getRestoreRecycleBin() {
        return mRestoreRecycleBin;
    }

    /**
     * Sets if a user has the restore recycle bin permission on a data room.
     *
     * @param restoreRecycleBin <code>true</code> if a user has the restore recycle bin permission;
     *                          <code>false</code> otherwise.
     */
    public void setRestoreRecycleBin(Boolean restoreRecycleBin) {
        mRestoreRecycleBin = restoreRecycleBin;
    }

    /**
     * Returns <code>true</code> if a user has the delete recycle bin permission on a data room.
     *
     * @return <code>true</code> if a user has the delete recycle bin permission;
     *         <code>false</code> otherwise
     */
    public Boolean getDeleteRecycleBin() {
        return mDeleteRecycleBin;
    }

    /**
     * Sets if a user has the delete recycle bin permission on a data room.
     *
     * @param deleteRecycleBin <code>true</code> if a user has the delete recycle bin permission;
     *                         <code>false</code> otherwise.
     */
    public void setDeleteRecycleBin(Boolean deleteRecycleBin) {
        mDeleteRecycleBin = deleteRecycleBin;
    }

}
