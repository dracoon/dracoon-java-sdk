package com.dracoon.sdk.internal.model;

public class ApiUserAccount {
    public Long id;
    public String title;
    public String gender;
    public String firstName;
    public String lastName;
    public String email;

    public Boolean isEncryptionEnabled;
    public Boolean hasManageableRooms;

    public Boolean needsToAcceptEULA;
    public Boolean needsToChangeUserName;
    public Boolean needsToChangePassword;

    public Integer lockStatus;
    public String expireAt;

    public String lastLoginSuccessAt;
    public String lastLoginSuccessIp;
    public String lastLoginFailAt;
    public String lastLoginFailIp;

    public ApiUserRoleList userRoles;
}
