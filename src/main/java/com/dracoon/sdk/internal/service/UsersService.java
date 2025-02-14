package com.dracoon.sdk.internal.service;

import java.util.UUID;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.ClientImpl;
import com.dracoon.sdk.internal.ClientMethodImpl;
import com.dracoon.sdk.internal.validator.UserValidator;

@ClientImpl(DracoonClient.Users.class)
public class UsersService extends BaseService {

    public UsersService(ServiceLocator locator, ServiceDependencies dependencies) {
        super(locator, dependencies);
    }

    // --- Avatar methods ---

    @ClientMethodImpl
    public byte[] getUserAvatar(long userId, UUID avatarUuid) throws DracoonNetIOException,
            DracoonApiException {
        UserValidator.validateUserId(userId);
        UserValidator.validateAvatarUuid(avatarUuid);

        String downloadUrl = buildApiUrl("downloads", "avatar", Long.toString(userId),
                avatarUuid.toString());

        return mServiceLocator.getAvatarDownloader().downloadAvatar(downloadUrl);
    }

}
