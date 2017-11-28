package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiUserInfo;
import com.dracoon.sdk.model.UserInfo;

public class UserInfoMapper {

    public static UserInfo fromApiUserInfo(ApiUserInfo apiUserInfo) {
        if (apiUserInfo == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(apiUserInfo.id);
        userInfo.setDisplayName(apiUserInfo.displayName);
        return userInfo;
    }

}
