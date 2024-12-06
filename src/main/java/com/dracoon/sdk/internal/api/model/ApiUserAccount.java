package com.dracoon.sdk.internal.api.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
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

    public Date expireAt;

    public Date lastLoginSuccessAt;
    public String lastLoginSuccessIp;
    public Date lastLoginFailAt;
    public String lastLoginFailIp;

    public ApiUserRoleList userRoles;
}
