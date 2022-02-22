package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DownloadStreamTest extends DracoonRequestHandlerTest {

    private static final int CHUNK_SIZE = 2;

    private DownloadStream mDls;

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

        private final String DATA_PATH = "/download/start/";

        @BeforeEach
        void setup() {
            // Create download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 2, null);
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Start download
            mDls.start();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_node_request.json");
            checkRequest(DATA_PATH + "create_download_url_request.json");
        }

        @Test
        void testGetNodeErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            when(mDracoonErrorParser.parseNodesQueryError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_not_found_response.json");

            // Start download
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mDls::start);

            // Assert correct error code
            assertEquals(code, thrown.getCode());
        }

        @Test
        void testGetDownloadUrlErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_FILE_NOT_FOUND;
            when(mDracoonErrorParser.parseDownloadTokenGetError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_not_found_response.json");

            // Start download
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mDls::start);

            // Assert correct error code
            assertEquals(code, thrown.getCode());
        }

        @Test
        void testAvailableBeforeStartedNotAllowed() {
            assertThrows(IOException.class, () -> mDls.available());
        }

        @Test
        void testReadBeforeStartedNotAllowed() {
            assertThrows(IOException.class, () -> readBytes(mDls));
        }

        @Test
        void testSkipBeforeStartedNotAllowed() {
            assertThrows(IOException.class, () -> skipBytes(mDls));
        }

    }

    // --- Available tests ---

    // TODO

    // --- Read tests ---

    // TODO

    // --- Skip tests ---

    // TODO

    // --- Close tests ---

    // TODO

    // --- Callback tests ---

    // TODO

    // --- Helper methods ---

    private static byte[] readBytes(DownloadStream dls) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        readBytes(dls, os);
        return os.toByteArray();
    }

    private static long readBytes(InputStream is, OutputStream os) throws IOException {
        byte[] b = new byte[1024];
        long read = 0L;
        int cnt;
        while ((cnt = is.read(b)) != -1) {
            os.write(b, 0, cnt);
            read += cnt;
        }
        return read;
    }

    private static void skipBytes(DownloadStream dls) throws IOException {
        while (dls.skip(128L) > 0L) {};
    }

}
