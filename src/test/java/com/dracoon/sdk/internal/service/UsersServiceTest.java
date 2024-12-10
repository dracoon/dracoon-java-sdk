package com.dracoon.sdk.internal.service;

import java.util.UUID;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsersServiceTest extends BaseServiceTest {

    private UsersService mSrv;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mSrv = new UsersService(mDracoonClientImpl);
    }

    // --- Get user avatar tests ---

    @Nested
    class GetUserAvatarTests {

        private final long USER_ID = 1L;
        private final String AVATAR_UUID = "c33e748c-d05b-4af2-90e3-1a24d79b1d41";
        private final byte[] AVATAR_BYTES = "avatar".getBytes();

        @Mock
        protected AvatarDownloader mAvatarDownloader;

        @BeforeEach
        void setup() {
            mServiceLocator.setAvatarDownloader(mAvatarDownloader);
        }

        @Test
        void testDownloaderCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            assertArrayEquals(AVATAR_BYTES, executeMockedWithReturn());
        }

        @Test
        void testDownloaderError() {
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeMockedWithException);

            assertEquals(DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND, thrown.getCode());
        }

        private void executeMockedAndVerified() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenReturn(AVATAR_BYTES);
            mSrv.getUserAvatar(USER_ID, UUID.fromString(AVATAR_UUID));
            verify(mAvatarDownloader).downloadAvatar(mServerUrl +
                    "/api/v4/downloads/avatar/" + USER_ID + "/" + AVATAR_UUID);
        }

        private void executeMockedWithException() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenThrow(new DracoonApiException(DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND));
            mSrv.getUserAvatar(USER_ID, UUID.fromString(AVATAR_UUID));
        }

        private byte[] executeMockedWithReturn() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenReturn(AVATAR_BYTES);
            return mSrv.getUserAvatar(USER_ID, UUID.fromString(AVATAR_UUID));
        }

    }

}
