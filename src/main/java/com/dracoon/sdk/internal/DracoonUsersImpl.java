package com.dracoon.sdk.internal;

import java.util.UUID;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.validator.UserValidator;

public class DracoonUsersImpl extends DracoonRequestHandler implements DracoonClient.Users {

    private static final String LOG_TAG = DracoonUsersImpl.class.getSimpleName();

    private final AvatarDownloader mAvatarDownloader;

    DracoonUsersImpl(DracoonClientImpl client) {
        super(client);

        mAvatarDownloader = new AvatarDownloader(client);
    }

    // --- Avatar methods ---

    @Override
    public byte[] getUserAvatar(long userId, UUID avatarUuid) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        UserValidator.validateUserId(userId);
        UserValidator.validateAvatarUuir(avatarUuid);

        String downloadUrl = mClient.buildApiUrl("downloads", "avatar", Long.toString(userId),
                avatarUuid.toString());

        return mAvatarDownloader.downloadAvatar(downloadUrl);
    }

}
