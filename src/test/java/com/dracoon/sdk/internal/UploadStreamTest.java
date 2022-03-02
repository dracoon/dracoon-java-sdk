package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.model.FileUploadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UploadStreamTest extends DracoonRequestHandlerTest {

    private static final int CHUNK_SIZE = 2;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();
        DracoonHttpConfig httpConfig = new DracoonHttpConfig();
        httpConfig.setChunkSize(CHUNK_SIZE);
        mDracoonClientImpl.setHttpConfig(httpConfig);
    }

    // --- Start tests ---

    @Nested
    class StartTests {

        private final String DATA_PATH = "/upload/start/";

        private UploadStream mUls;

        @BeforeEach
        void setup() {
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, 1024L, null, null);
        }

        @Test
        void testCreateUploadErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            when(mDracoonErrorParser.parseUploadCreateError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(DATA_PATH + "create_upload_not_found_response.json");

            // Start upload
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mUls::start);

            // Assert correct error code
            assertEquals(code, thrown.getCode());
        }

        @Test
        void testWriteBeforeStartedNotAllowed() {
            assertThrows(IOException.class, () -> mUls.write(new byte[1]));
        }

        @Test
        void testCompleteBeforeStartedNotAllowed() {
            assertThrows(IOException.class, () -> mUls.complete());
        }

    }

    @Nested
    class StartDcTests {

        private final String DATA_PATH = "/upload/start_dc/";

        private UploadStream mUls;

        @BeforeEach
        void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, 1024L, null, null);
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "create_upload_response.json");

            // Start upload
            mUls.start();

            // Assert requests are valid
            checkRequest(DATA_PATH + "create_upload_request.json");
        }

    }

    @Nested
    class StartS3Tests {

        private final String DATA_PATH = "/upload/start_s3/";

        private UploadStream mUls;

        @BeforeEach
        void setup() {
            // Overwrite API version to allow S3 upload
            mDracoonClientImpl.setApiVersion(DracoonConstants.API_MIN_S3_DIRECT_UPLOAD);

            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, 1024L, null, null);
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_server_settings_response.json");
            enqueueResponse(DATA_PATH + "create_upload_response.json");

            // Start upload
            mUls.start();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_server_settings_request.json");
            checkRequest(DATA_PATH + "create_upload_request.json");
        }

    }

    // --- Write tests ---

    // TODO

    // --- Complete tests ---

    // TODO

    // --- Close tests ---

    // TODO

    // --- Callback tests ---

    // TODO

    // --- Helper methods ---

}
