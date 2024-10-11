package com.dracoon.sdk.internal;

import java.util.UUID;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.validator.UserValidator;

@ClientImpl(DracoonClient.Users.class)
class UsersService extends BaseService {

    UsersService(DracoonClientImpl client) {
        super(client);
    }

    // --- Avatar methods ---

    @ClientMethodImpl
    public byte[] getUserAvatar(long userId, UUID avatarUuid) throws DracoonNetIOException,
            DracoonApiException {
        UserValidator.validateUserId(userId);
        UserValidator.validateAvatarUuid(avatarUuid);

        String downloadUrl = mClient.buildApiUrl("downloads", "avatar", Long.toString(userId),
                avatarUuid.toString());

        return mClient.getAvatarDownloader().downloadAvatar(downloadUrl);
    }

}
