package com.dracoon.sdk.internal.model;

import java.util.Date;

public class ApiUserAccount {
    public Long id;
    public String title;
    public String gender;
    public String firstName;
    public String lastName;
    public String email;

    public String login;
    public String userName;

    public Boolean isEncryptionEnabled;
    public Boolean hasManageableRooms;

    public Boolean needsToAcceptEULA;
    public Boolean needsToChangeUserName;
    public Boolean needsToChangePassword;

    public Integer lockStatus;
    public Date expireAt;

    public Date lastLoginSuccessAt;
    public String lastLoginSuccessIp;
    public Date lastLoginFailAt;
    public String lastLoginFailIp;

    public ApiUserRoleList userRoles;
}
