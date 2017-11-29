package com.dracoon.sdk.model;

public class CustomerAccount {

    private Long mId;
    private String mName;

    private Integer mAccountsUsed;
    private Integer mAccountsLimit;
    private Long mSpaceUsed;
    private Long mSpaceLimit;
    private Long mCntRooms;
    private Long mCntFolders;
    private Long mCntFiles;

    private Boolean mHasEncryptionEnabled;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Integer getAccountsUsed() {
        return mAccountsUsed;
    }

    public void setAccountsUsed(Integer accountsUsed) {
        mAccountsUsed = accountsUsed;
    }

    public Integer getAccountsLimit() {
        return mAccountsLimit;
    }

    public void setAccountsLimit(Integer accountsLimit) {
        mAccountsLimit = accountsLimit;
    }

    public Long getSpaceUsed() {
        return mSpaceUsed;
    }

    public void setSpaceUsed(Long spaceUsed) {
        mSpaceUsed = spaceUsed;
    }

    public Long getSpaceLimit() {
        return mSpaceLimit;
    }

    public void setSpaceLimit(Long spaceLimit) {
        mSpaceLimit = spaceLimit;
    }

    public Long getCntRooms() {
        return mCntRooms;
    }

    public void setCntRooms(Long cntRooms) {
        mCntRooms = cntRooms;
    }

    public Long getCntFolders() {
        return mCntFolders;
    }

    public void setCntFolders(Long cntFolders) {
        mCntFolders = cntFolders;
    }

    public Long getCntFiles() {
        return mCntFiles;
    }

    public void setCntFiles(Long cntFiles) {
        mCntFiles = cntFiles;
    }

    public Boolean hasEncryptionEnabled() {
        return mHasEncryptionEnabled;
    }

    public void setHasEncryptionEnabled(Boolean hasEncryptionEnabled) {
        mHasEncryptionEnabled = hasEncryptionEnabled;
    }

}
