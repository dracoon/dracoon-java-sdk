package com.dracoon.sdk.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.model.ApiUserPrivateKey;
import com.dracoon.sdk.internal.model.ApiUserPublicKey;
import com.dracoon.sdk.internal.model.ApiUserRole;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserInfo;
import com.dracoon.sdk.model.Gender;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserInfo;
import com.dracoon.sdk.model.UserRole;

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

        userAccount.setExpireAt(apiUserAccount.expireAt);

        userAccount.setLastLoginSuccessAt(apiUserAccount.lastLoginSuccessAt);
        userAccount.setLastLoginSuccessIp(apiUserAccount.lastLoginSuccessIp);
        userAccount.setLastLoginFailAt(apiUserAccount.lastLoginFailAt);
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

    private static UserRole fromApiUserRole(ApiUserRole apiUserRole) {
        if (apiUserRole == null) {
            return null;
        }

        return UserRole.getByValue(apiUserRole.id);
    }

    public static ApiUserKeyPair toApiUserKeyPair(UserKeyPair userKeyPair) {
        ApiUserKeyPair apiUserKeyPair = new ApiUserKeyPair();
        apiUserKeyPair.privateKeyContainer = toApiUserPrivateKey(userKeyPair.getUserPrivateKey());
        apiUserKeyPair.publicKeyContainer = toApiUserPublicKey(userKeyPair.getUserPublicKey());
        return apiUserKeyPair;
    }

    private static ApiUserPrivateKey toApiUserPrivateKey(UserPrivateKey userPrivateKey) {
        ApiUserPrivateKey apiUserPrivateKey = new ApiUserPrivateKey();
        apiUserPrivateKey.version = userPrivateKey.getVersion();
        apiUserPrivateKey.privateKey = userPrivateKey.getPrivateKey();
        return apiUserPrivateKey;
    }

    private static ApiUserPublicKey toApiUserPublicKey(UserPublicKey userPublicKey) {
        ApiUserPublicKey apiUserPublicKey = new ApiUserPublicKey();
        apiUserPublicKey.version = userPublicKey.getVersion();
        apiUserPublicKey.publicKey = userPublicKey.getPublicKey();
        return apiUserPublicKey;
    }

    public static UserKeyPair fromApiUserKeyPair(ApiUserKeyPair apiUserKeyPair) {
        if (apiUserKeyPair == null) {
            return null;
        }

        UserKeyPair userKeyPair = new UserKeyPair();
        userKeyPair.setUserPrivateKey(fromApiUserPrivateKey(apiUserKeyPair.privateKeyContainer));
        userKeyPair.setUserPublicKey(fromApiUserPublicKey(apiUserKeyPair.publicKeyContainer));
        return userKeyPair;
    }

    private static UserPrivateKey fromApiUserPrivateKey(ApiUserPrivateKey apiUserPrivateKey) {
        if (apiUserPrivateKey == null) {
            return null;
        }

        UserPrivateKey userPrivateKey = new UserPrivateKey();
        userPrivateKey.setVersion(apiUserPrivateKey.version);
        userPrivateKey.setPrivateKey(apiUserPrivateKey.privateKey);
        return userPrivateKey;
    }

    public static UserPublicKey fromApiUserPublicKey(ApiUserPublicKey apiUserPublicKey) {
        if (apiUserPublicKey == null) {
            return null;
        }

        UserPublicKey userPublicKey = new UserPublicKey();
        userPublicKey.setVersion(apiUserPublicKey.version);
        userPublicKey.setPublicKey(apiUserPublicKey.publicKey);
        return userPublicKey;
    }

}
