package com.dracoon.sdk.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserInfo;
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.model.ApiUserPrivateKey;
import com.dracoon.sdk.internal.model.ApiUserPublicKey;
import com.dracoon.sdk.internal.model.ApiUserRole;
import com.dracoon.sdk.model.Gender;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserInfo;
import com.dracoon.sdk.model.UserRole;

public class UserMapper extends BaseMapper {

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

        userAccount.setHasEncryptionEnabled(toBoolean(apiUserAccount.isEncryptionEnabled));
        userAccount.setHasManageableRooms(toBoolean(apiUserAccount.hasManageableRooms));

        userAccount.setNeedsToAcceptEULA(toBoolean(apiUserAccount.needsToAcceptEULA));
        userAccount.setNeedsToChangeUserName(toBoolean(apiUserAccount.needsToChangeUserName));
        userAccount.setNeedsToChangePassword(toBoolean(apiUserAccount.needsToChangePassword));

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
        if (userKeyPair == null) {
            return null;
        }

        ApiUserKeyPair apiUserKeyPair = new ApiUserKeyPair();
        apiUserKeyPair.privateKeyContainer = toApiUserPrivateKey(userKeyPair.getUserPrivateKey());
        apiUserKeyPair.publicKeyContainer = toApiUserPublicKey(userKeyPair.getUserPublicKey());
        return apiUserKeyPair;
    }

    private static ApiUserPrivateKey toApiUserPrivateKey(UserPrivateKey userPrivateKey) {
        if (userPrivateKey == null) {
            return null;
        }

        ApiUserPrivateKey apiUserPrivateKey = new ApiUserPrivateKey();
        apiUserPrivateKey.version = userPrivateKey.getVersion();
        apiUserPrivateKey.privateKey = userPrivateKey.getPrivateKey();
        return apiUserPrivateKey;
    }

    private static ApiUserPublicKey toApiUserPublicKey(UserPublicKey userPublicKey) {
        if (userPublicKey == null) {
            return null;
        }

        ApiUserPublicKey apiUserPublicKey = new ApiUserPublicKey();
        apiUserPublicKey.version = userPublicKey.getVersion();
        apiUserPublicKey.publicKey = userPublicKey.getPublicKey();
        return apiUserPublicKey;
    }

    public static UserKeyPair fromApiUserKeyPair(ApiUserKeyPair apiUserKeyPair) {
        if (apiUserKeyPair == null) {
            return null;
        }

        return new UserKeyPair(fromApiUserPrivateKey(apiUserKeyPair.privateKeyContainer),
                fromApiUserPublicKey(apiUserKeyPair.publicKeyContainer));
    }

    private static UserPrivateKey fromApiUserPrivateKey(ApiUserPrivateKey apiUserPrivateKey) {
        if (apiUserPrivateKey == null) {
            return null;
        }

        return new UserPrivateKey(apiUserPrivateKey.version, apiUserPrivateKey.privateKey);
    }

    public static UserPublicKey fromApiUserPublicKey(ApiUserPublicKey apiUserPublicKey) {
        if (apiUserPublicKey == null) {
            return null;
        }

        return new UserPublicKey(apiUserPublicKey.version, apiUserPublicKey.publicKey);
    }

}
