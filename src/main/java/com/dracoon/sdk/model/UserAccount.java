package com.dracoon.sdk.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User account model.<br>
 * <br>
 * This model stores information about the user account.
 */
@SuppressWarnings("unused")
public class UserAccount {

    private Long mId;
    private String mTitle;
    private Gender mGender;
    private String mFirstName;
    private String mLastName;
    private String mEmail;

    private String mUsername;

    private Boolean mHasEncryptionEnabled;
    private Boolean mHasManageableRooms;

    private Boolean mNeedsToAcceptEULA;
    private Boolean mNeedsToChangeUserName;
    private Boolean mNeedsToChangePassword;

    private Date mExpireAt;

    private Date mLastLoginSuccessAt;
    private String mLastLoginSuccessIp;
    private Date mLastLoginFailAt;
    private String mLastLoginFailIp;

    private List<UserRole> mUserRoles;

    private UUID mAvatarUuid;
    private Boolean mHasCustomAvatar;

    /**
     * Returns the ID of the user.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the title of the user.
     *
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the title of the user.
     *
     * @param title The title.
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Returns the gender of the user.
     *
     * @return the gender
     */
    public Gender getGender() {
        return mGender;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender The gender.
     */
    public void setGender(Gender gender) {
        mGender = gender;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name.
     */
    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address.
     */
    public void setEmail(String email) {
        mEmail = email;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * Returns <code>true</code> if user has encryption enabled.
     *
     * @return <code>true</code> if encryption is enabled; <code>false</code> otherwise
     */
    public Boolean hasEncryptionEnabled() {
        return mHasEncryptionEnabled;
    }

    /**
     * Sets if user has encryption enabled.
     *
     * @param hasEncryptionEnabled <code>true</code> if encryption is enabled; otherwise
     *                             <code>false</code>.
     */
    public void setHasEncryptionEnabled(Boolean hasEncryptionEnabled) {
        mHasEncryptionEnabled = hasEncryptionEnabled;
    }

    /**
     * Returns <code>true</code> if user has manageable data rooms.
     *
     * @return <code>true</code> if user has manageable data rooms; <code>false</code> otherwise
     */
    public Boolean hasManageableRooms() {
        return mHasManageableRooms;
    }

    /**
     * Set if user has manageable data rooms.
     *
     * @param hasManageableRooms <code>true</code> if user has manageable data rooms;
     *                           <code>false</code> otherwise.
     */
    public void setHasManageableRooms(Boolean hasManageableRooms) {
        mHasManageableRooms = hasManageableRooms;
    }

    /**
     * Returns <code>true</code> if user needs to accept the EULA.
     *
     * @return <code>true</code> if user needs to accept the EULA; <code>false</code> otherwise
     */
    public Boolean needsToAcceptEULA() {
        return mNeedsToAcceptEULA;
    }

    /**
     * Sets if user needs to accept the EULA.
     *
     * @param needsToAcceptEULA <code>true</code> if user needs to accept the EULA;
     *                          <code>false</code> otherwise.
     */
    public void setNeedsToAcceptEULA(Boolean needsToAcceptEULA) {
        mNeedsToAcceptEULA = needsToAcceptEULA;
    }

    /**
     * Returns <code>true</code> if user needs to change the user name.
     *
     * @return <code>true</code> if user needs to change the user name; <code>false</code> otherwise
     */
    public Boolean needsToChangeUserName() {
        return mNeedsToChangeUserName;
    }

    /**
     * Sets if user needs to change the user name.
     *
     * @param needsToChangeUserName <code>true</code> if user needs to change the user name;
     *                              <code>false</code> otherwise.
     */
    public void setNeedsToChangeUserName(Boolean needsToChangeUserName) {
        mNeedsToChangeUserName = needsToChangeUserName;
    }

    /**
     * Returns <code>true</code> if user needs to change the password.
     *
     * @return <code>true</code> if user needs to change the password; <code>false</code> otherwise
     */
    public Boolean needsToChangePassword() {
        return mNeedsToChangePassword;
    }

    /**
     * Sets if user needs to change the password.
     *
     * @param needsToChangePassword <code>true</code> if user needs to change the password;
     *                              <code>false</code> otherwise.
     */
    public void setNeedsToChangePassword(Boolean needsToChangePassword) {
        mNeedsToChangePassword = needsToChangePassword;
    }

    /**
     * Returns the expire date of the user account, if user account and has a expire date.
     *
     * @return the expire date, or <code>null</code>
     */
    public Date getExpireAt() {
        return mExpireAt;
    }

    /**
     * Sets the expire date of the user account.
     *
     * @param expireAt The expire date.
     */
    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    /**
     * Returns the date of the last login of the user account, if user ever logged in successfully.
     *
     * @return the date of the last login, or <code>null</code>
     */
    public Date getLastLoginSuccessAt() {
        return mLastLoginSuccessAt;
    }

    /**
     * Sets the date of the last login of the user account.
     *
     * @param lastLoginSuccessAt The date of the last login.
     */
    public void setLastLoginSuccessAt(Date lastLoginSuccessAt) {
        mLastLoginSuccessAt = lastLoginSuccessAt;
    }

    /**
     * Returns the IP address of the last login of the user account, if user ever logged in
     * successfully and IP address logging is enabled.
     *
     * @return the IP address of the last login, or <code>null</code>
     */
    public String getLastLoginSuccessIp() {
        return mLastLoginSuccessIp;
    }

    /**
     * Sets the IP address of the last login of the user account.
     *
     * @param lastLoginSuccessIp The IP address of the last login.
     */
    public void setLastLoginSuccessIp(String lastLoginSuccessIp) {
        mLastLoginSuccessIp = lastLoginSuccessIp;
    }

    /**
     * Returns the date of the last failed login of the user account, if user ever logged in with an
     * error.
     *
     * @return the date of the last failed login, or <code>null</code>
     */
    public Date getLastLoginFailAt() {
        return mLastLoginFailAt;
    }

    /**
     * Sets the date of the last failed login of the user account.
     *
     * @param lastLoginFailAt The date of the last failed login.
     */
    public void setLastLoginFailAt(Date lastLoginFailAt) {
        mLastLoginFailAt = lastLoginFailAt;
    }

    /**
     * Returns the IP address of the last failed login of the user account, if user ever logged in
     * with an error and IP address logging is enabled.
     *
     * @return the IP address of the last failed login, or <code>null</code>
     */
    public String getLastLoginFailIp() {
        return mLastLoginFailIp;
    }

    /**
     * Sets the IP address of the last failed login of the user account.
     *
     * @param lastLoginFailIp The IP address of the last failed login.
     */
    public void setLastLoginFailIp(String lastLoginFailIp) {
        mLastLoginFailIp = lastLoginFailIp;
    }

    /**
     * Returns all roles of the user.
     *
     * @return all roles
     */
    public List<UserRole> getUserRoles() {
        return mUserRoles;
    }

    /**
     * Sets all roles of the user.
     *
     * @param userRoles All roles.
     */
    public void setUserRoles(List<UserRole> userRoles) {
        mUserRoles = userRoles;
    }

    /**
     * Returns the avatar UUID of the user.
     *
     * @return the avatar UUID
     */
    public UUID getAvatarUuid() {
        return mAvatarUuid;
    }

    /**
     * Sets avatar UUID of the user.
     *
     * @param avatarUuid The avatar UUID.
     */
    public void setAvatarUuid(UUID avatarUuid) {
        mAvatarUuid = avatarUuid;
    }

    /**
     * Returns <code>true</code> if user has a custom avatar.
     *
     * @return <code>true</code> if user has a custom avatar; <code>false</code> otherwise
     */
    public Boolean hasCustomAvatar() {
        return mHasCustomAvatar;
    }

    /**
     * Sets if user has a custom avatar.
     *
     * @param hasCustomAvatar <code>true</code> if user has a custom avatar;
     *                        <code>false</code> otherwise.
     */
    public void setHasCustomAvatar(Boolean hasCustomAvatar) {
        mHasCustomAvatar = hasCustomAvatar;
    }

}
