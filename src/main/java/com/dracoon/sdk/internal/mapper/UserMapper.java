package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiUserRole;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserInfo;
import com.dracoon.sdk.internal.util.DateUtils;
import com.dracoon.sdk.model.Gender;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserInfo;
import com.dracoon.sdk.model.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserInfo fromApiUserInfo(ApiUserInfo apiUserInfo) {
        if (apiUserInfo == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(apiUserInfo.id);
        userInfo.setDisplayName(apiUserInfo.displayName);
        return userInfo;
    }

    public static UserAccount fromApiUserAccount(ApiUserAccount apiUserAccount) {
        if (apiUserAccount == null) {
            return null;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setId(apiUserAccount.id);
        userAccount.setTitle(apiUserAccount.title);
        userAccount.setGender(Gender.getByValue(apiUserAccount.gender));
        userAccount.setFirstName(apiUserAccount.firstName);
        userAccount.setLastName(apiUserAccount.lastName);
        userAccount.setEmail(apiUserAccount.email);

        userAccount.setHasEncryptionEnabled(apiUserAccount.isEncryptionEnabled);
        userAccount.setHasManageableRooms(apiUserAccount.hasManageableRooms);

        userAccount.setNeedsToAcceptEULA(apiUserAccount.needsToAcceptEULA);
        userAccount.setNeedsToChangeUserName(apiUserAccount.needsToChangeUserName);
        userAccount.setNeedsToChangePassword(apiUserAccount.needsToChangePassword);

        userAccount.setExpireAt(DateUtils.parseDate(apiUserAccount.expireAt));

        userAccount.setLastLoginSuccessAt(DateUtils.parseDate(apiUserAccount.lastLoginSuccessAt));
        userAccount.setLastLoginSuccessIp(apiUserAccount.lastLoginSuccessIp);
        userAccount.setLastLoginFailAt(DateUtils.parseDate(apiUserAccount.lastLoginFailAt));
        userAccount.setLastLoginFailIp(apiUserAccount.lastLoginFailIp);

        List<UserRole> userRoles = new ArrayList<>();
        if (apiUserAccount.userRoles != null) {
            for (ApiUserRole apiUserRole : apiUserAccount.userRoles.items) {
                userRoles.add(fromApiUserRole(apiUserRole));
            }
        }
        userAccount.setUserRoles(userRoles);

        return userAccount;
    }

    public static UserRole fromApiUserRole(ApiUserRole apiUserRole) {
        if (apiUserRole == null) {
            return null;
        }

        return UserRole.getByValue(apiUserRole.id);
    }

}
