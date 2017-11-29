package com.dracoon.sdk.model;

import java.util.Date;
import java.util.List;

public class UserAccount {

    private Long mId;
    private String mTitle;
    private Gender mGender;
    private String mFirstName;
    private String mLastName;
    private String mEmail;

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

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        mGender = gender;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Boolean hasEncryptionEnabled() {
        return mHasEncryptionEnabled;
    }

    public void setHasEncryptionEnabled(Boolean hasEncryptionEnabled) {
        mHasEncryptionEnabled = hasEncryptionEnabled;
    }

    public Boolean hasManageableRooms() {
        return mHasManageableRooms;
    }

    public void setHasManageableRooms(Boolean hasManageableRooms) {
        mHasManageableRooms = hasManageableRooms;
    }

    public Boolean needsToAcceptEULA() {
        return mNeedsToAcceptEULA;
    }

    public void setNeedsToAcceptEULA(Boolean needsToAcceptEULA) {
        mNeedsToAcceptEULA = needsToAcceptEULA;
    }

    public Boolean needsToChangeUserName() {
        return mNeedsToChangeUserName;
    }

    public void setNeedsToChangeUserName(Boolean needsToChangeUserName) {
        mNeedsToChangeUserName = needsToChangeUserName;
    }

    public Boolean needsToChangePassword() {
        return mNeedsToChangePassword;
    }

    public void setNeedsToChangePassword(Boolean needsToChangePassword) {
        mNeedsToChangePassword = needsToChangePassword;
    }

    public Date getExpireAt() {
        return mExpireAt;
    }

    public void setExpireAt(Date expireAt) {
        mExpireAt = expireAt;
    }

    public Date getLastLoginSuccessAt() {
        return mLastLoginSuccessAt;
    }

    public void setLastLoginSuccessAt(Date lastLoginSuccessAt) {
        mLastLoginSuccessAt = lastLoginSuccessAt;
    }

    public String getLastLoginSuccessIp() {
        return mLastLoginSuccessIp;
    }

    public void setLastLoginSuccessIp(String lastLoginSuccessIp) {
        mLastLoginSuccessIp = lastLoginSuccessIp;
    }

    public Date getLastLoginFailAt() {
        return mLastLoginFailAt;
    }

    public void setLastLoginFailAt(Date lastLoginFailAt) {
        mLastLoginFailAt = lastLoginFailAt;
    }

    public String getLastLoginFailIp() {
        return mLastLoginFailIp;
    }

    public void setLastLoginFailIp(String lastLoginFailIp) {
        mLastLoginFailIp = lastLoginFailIp;
    }

    public List<UserRole> getUserRoles() {
        return mUserRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        mUserRoles = userRoles;
    }

}
