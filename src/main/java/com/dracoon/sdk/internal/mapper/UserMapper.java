package com.dracoon.sdk.internal.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserAvatarInfo;
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

    private UserMapper() {
        super();
    }

    public static UserInfo fromApiUserInfo(ApiUserInfo apiUserInfo) {
        if (apiUserInfo == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(apiUserInfo.id);
        userInfo.setUserType(UserInfo.UserType.getByValue(apiUserInfo.userType));
        userInfo.setFirstName(apiUserInfo.firstName);
        userInfo.setLastName(apiUserInfo.lastName);
        userInfo.setUsername(apiUserInfo.userName);
        userInfo.setEmail(apiUserInfo.email);
        try {
            userInfo.setAvatarUuid(UUID.fromString(apiUserInfo.avatarUuid));
        } catch (IllegalArgumentException e) {
            // Nothing to do here
        }
        userInfo.setDisplayName(apiUserInfo.displayName);
        return userInfo;
    }

    public static UserAccount fromApiUserAccount(ApiUserAccount apiUserAccount,
            ApiUserAvatarInfo apiUserAvatarInfo) {
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

        userAccount.setUsername(apiUserAccount.userName != null ? apiUserAccount.userName :
                apiUserAccount.login);

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

        if (apiUserAvatarInfo != null) {
            try {
                userAccount.setAvatarUuid(UUID.fromString(apiUserAvatarInfo.avatarUuid));
            } catch (IllegalArgumentException e) {
                // Nothing to do here
            }
            userAccount.setHasCustomAvatar(apiUserAvatarInfo.isCustomAvatar);
        }

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
        apiUserPrivateKey.version = userPrivateKey.getVersion().getValue();
        apiUserPrivateKey.privateKey = userPrivateKey.getPrivateKey();
        return apiUserPrivateKey;
    }

    private static ApiUserPublicKey toApiUserPublicKey(UserPublicKey userPublicKey) {
        if (userPublicKey == null) {
            return null;
        }

        ApiUserPublicKey apiUserPublicKey = new ApiUserPublicKey();
        apiUserPublicKey.version = userPublicKey.getVersion().getValue();
        apiUserPublicKey.publicKey = userPublicKey.getPublicKey();
        return apiUserPublicKey;
    }

    public static UserKeyPair fromApiUserKeyPair(ApiUserKeyPair apiUserKeyPair)
            throws UnknownVersionException {
        if (apiUserKeyPair == null) {
            return null;
        }

        return new UserKeyPair(fromApiUserPrivateKey(apiUserKeyPair.privateKeyContainer),
                fromApiUserPublicKey(apiUserKeyPair.publicKeyContainer));
    }

    private static UserPrivateKey fromApiUserPrivateKey(ApiUserPrivateKey apiUserPrivateKey)
            throws UnknownVersionException {
        if (apiUserPrivateKey == null) {
            return null;
        }

        UserKeyPair.Version version = UserKeyPair.Version.getByValue(apiUserPrivateKey.version);

        return new UserPrivateKey(version, apiUserPrivateKey.privateKey);
    }

    public static UserPublicKey fromApiUserPublicKey(ApiUserPublicKey apiUserPublicKey)
            throws UnknownVersionException {
        if (apiUserPublicKey == null) {
            return null;
        }

        UserKeyPair.Version version = UserKeyPair.Version.getByValue(apiUserPublicKey.version);

        return new UserPublicKey(version, apiUserPublicKey.publicKey);
    }

}
