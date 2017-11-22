package com.dracoon.sdk.model;

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

    public Boolean getManage() {
        return mManage;
    }

    public void setManage(Boolean manage) {
        mManage = manage;
    }

    public Boolean getRead() {
        return mRead;
    }

    public void setRead(Boolean read) {
        mRead = read;
    }

    public Boolean getCreate() {
        return mCreate;
    }

    public void setCreate(Boolean create) {
        mCreate = create;
    }

    public Boolean getChange() {
        return mChange;
    }

    public void setChange(Boolean change) {
        mChange = change;
    }

    public Boolean getDelete() {
        return mDelete;
    }

    public void setDelete(Boolean delete) {
        mDelete = delete;
    }

    public Boolean getManageDownloadShare() {
        return mManageDownloadShare;
    }

    public void setManageDownloadShare(Boolean manageDownloadShare) {
        mManageDownloadShare = manageDownloadShare;
    }

    public Boolean getManageUploadShare() {
        return mManageUploadShare;
    }

    public void setManageUploadShare(Boolean manageUploadShare) {
        mManageUploadShare = manageUploadShare;
    }

    public Boolean getReadRecycleBin() {
        return mReadRecycleBin;
    }

    public void setReadRecycleBin(Boolean readRecycleBin) {
        mReadRecycleBin = readRecycleBin;
    }

    public Boolean getRestoreRecycleBin() {
        return mRestoreRecycleBin;
    }

    public void setRestoreRecycleBin(Boolean restoreRecycleBin) {
        mRestoreRecycleBin = restoreRecycleBin;
    }

    public Boolean getDeleteRecycleBin() {
        return mDeleteRecycleBin;
    }

    public void setDeleteRecycleBin(Boolean deleteRecycleBin) {
        mDeleteRecycleBin = deleteRecycleBin;
    }

}
