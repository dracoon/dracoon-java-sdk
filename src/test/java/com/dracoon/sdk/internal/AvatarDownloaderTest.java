package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AvatarDownloaderTest extends BaseServiceTest {

    private final String DATA_PATH = "/avatar/";

    private final byte[] AVATAR_BYTES = "avatar".getBytes();

    private AvatarDownloader mAvatarDownloader;
    private String mAvatarDownloadUrl;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mAvatarDownloader = new AvatarDownloader(mDracoonClientImpl);

        mAvatarDownloadUrl = mServerUrl +
                "/api/v4/downloads/avatar/1/c33e748c-d05b-4af2-90e3-1a24d79b1d41";
    }

    @Test
    void testApiRequestValid() throws Exception {
        // Enqueue response
        enqueueResponse(DATA_PATH + "download_response.json");

        // Execute method to test
        mAvatarDownloader.downloadAvatar(mAvatarDownloadUrl);

        // Assert request is valid
        checkRequest(DATA_PATH + "download_request.json");
    }

    @Test
    void testDataCorrect() throws Exception {
        // Enqueue response
        enqueueResponse(DATA_PATH + "download_response.json");

        // Execute method to test
        byte[] avatarBytes = mAvatarDownloader.downloadAvatar(mAvatarDownloadUrl);

        // Assert data is correct
        assertArrayEquals(AVATAR_BYTES, avatarBytes);
    }

    @Test
    void testError() {
        // Mock error parsing
        DracoonApiCode expectedCode = DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND;
        when(mDracoonErrorParser.parseAvatarDownloadError(any()))
                .thenReturn(expectedCode);

        // Enqueue response
        enqueueResponse(DATA_PATH + "not_found_response.json");

        // Execute method to test
        DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                mAvatarDownloader.downloadAvatar(mAvatarDownloadUrl));

        // Assert correct error code
        assertEquals(expectedCode, thrown.getCode());
    }

}
