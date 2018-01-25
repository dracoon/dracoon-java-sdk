package com.dracoon.sdk.model;

/**
 * Customer account model.<br>
 * <br>
 * This model stores information about the customer account.
 */
@SuppressWarnings("unused")
public class CustomerAccount {

    private Long mId;
    private String mName;

    private Integer mUserAccountsUsed;
    private Integer mUserAccountsLimit;
    private Long mSpaceUsed;
    private Long mSpaceLimit;
    private Long mCntRooms;
    private Long mCntFolders;
    private Long mCntFiles;

    private Boolean mHasEncryptionEnabled;

    /**
     * Returns the ID of the customer.
     *
     * @return the ID.
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the customer.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the name of the customer.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the customer.
     *
     * @param name The name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the number of user accounts used by the customer.
     *
     * @return the number of user accounts
     */
    public Integer getUserAccountsUsed() {
        return mUserAccountsUsed;
    }

    /**
     * Sets the number of user accounts used by the customer.
     *
     * @param userAccountsUsed The number of user accounts.
     */
    public void setUserAccountsUsed(Integer userAccountsUsed) {
        mUserAccountsUsed = userAccountsUsed;
    }

    /**
     * Returns the number of user accounts available to the customer.
     *
     * @return the number of user accounts
     */
    public Integer getUserAccountsLimit() {
        return mUserAccountsLimit;
    }

    /**
     * Sets the number of user accounts available to the customer.
     *
     * @param userAccountsLimit The number of user accounts.
     */
    public void setUserAccountsLimit(Integer userAccountsLimit) {
        mUserAccountsLimit = userAccountsLimit;
    }

    /**
     * Returns the space used by the customer.
     *
     * @return the space in bytes
     */
    public Long getSpaceUsed() {
        return mSpaceUsed;
    }

    /**
     * Sets the space used by the customer.
     *
     * @param spaceUsed The space in bytes.
     */
    public void setSpaceUsed(Long spaceUsed) {
        mSpaceUsed = spaceUsed;
    }

    /**
     * Returns the space available to the customer.
     *
     * @return the space in bytes
     */
    public Long getSpaceLimit() {
        return mSpaceLimit;
    }

    /**
     * Sets the space available to the customer.
     *
     * @param spaceLimit The space in bytes.
     */
    public void setSpaceLimit(Long spaceLimit) {
        mSpaceLimit = spaceLimit;
    }

    /**
     * Returns the number of rooms used by the customer.
     *
     * @return the number of rooms
     */
    public Long getCntRooms() {
        return mCntRooms;
    }

    /**
     * Sets the number of rooms used by the customer.
     *
     * @param cntRooms The number of rooms.
     */
    public void setCntRooms(Long cntRooms) {
        mCntRooms = cntRooms;
    }

    /**
     * Returns the number of folders used by the customer.
     *
     * @return the number of folders
     */
    public Long getCntFolders() {
        return mCntFolders;
    }

    /**
     * Sets the number of folders used by the customer.
     *
     * @param cntFolders The number of folders.
     */
    public void setCntFolders(Long cntFolders) {
        mCntFolders = cntFolders;
    }

    /**
     * Returns the number of files used by the customer.
     *
     * @return the number of files
     */
    public Long getCntFiles() {
        return mCntFiles;
    }

    /**
     * Sets the number of files used by the customer.
     *
     * @param cntFiles The number of files.
     */
    public void setCntFiles(Long cntFiles) {
        mCntFiles = cntFiles;
    }

    /**
     * Returns <code>true</code> if customer has encryption enabled.
     *
     * @return <code>true</code> if encryption is enabled; <code>false</code> otherwise
     */
    public Boolean hasEncryptionEnabled() {
        return mHasEncryptionEnabled;
    }

    /**
     * Sets if customer has encryption enabled.
     *
     * @param hasEncryptionEnabled <code>true</code> if encryption is enabled; otherwise
     *                             <code>false</code>.
     */
    public void setHasEncryptionEnabled(Boolean hasEncryptionEnabled) {
        mHasEncryptionEnabled = hasEncryptionEnabled;
    }

}
