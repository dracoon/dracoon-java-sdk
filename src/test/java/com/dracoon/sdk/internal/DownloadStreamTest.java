package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Nested
    class AvailableStandardTests {

        private final String DATA_PATH = "/download/available_standard/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 2, null);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testAvailableCorrectAfterStart() throws Exception {
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2064L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");

            // Read bytes (< chunk size)
            readBytes(mDls, 8L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2056L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterReadChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");

            // Read bytes (>= chunk size)
            readBytes(mDls, 2056L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(8L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterSkip() throws Exception {
            // Skip bytes (< chunk size)
            skipBytes(mDls, 8L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2056L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterSkipChunk() throws Exception {
            // Skip bytes (>= chunk size)
            skipBytes(mDls, 2056L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(8L, available, "Number of available bytes does not match!");
        }

    }

    @Nested
    class AvailableEncryptedTests {

        private final String DATA_PATH = "/download/available_encrypted/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            PlainFileKey fileKey = readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 2, fileKey);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testAvailableCorrectAfterStart() throws Exception {
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2064L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");

            // Read bytes (< chunk size)
            readBytes(mDls, 8L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2056L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterReadChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");

            // Read bytes (>= chunk size)
            readBytes(mDls, 2056L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(8L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterSkip() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");

            // Skip bytes (< chunk size)
            skipBytes(mDls, 8L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(2056L, available, "Number of available bytes does not match!");
        }

        @Test
        void testAvailableCorrectAfterSkipChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");

            // Skip bytes (>= chunk size)
            skipBytes(mDls, 2056L);
            // Get available bytes
            long available = mDls.available();

            // Assert number of available bytes is correct
            assertEquals(8L, available, "Number of available bytes does not match!");
        }

    }

    // --- Read tests ---

    @Nested
    class ReadTests {

        private final String DATA_PATH = "/download/read/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 3, null);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testDownloadErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_FILE_NOT_FOUND;
            when(mDracoonErrorParser.parseDownloadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_not_found_response.json");

            // Read bytes
            IOException thrown = assertThrows(IOException.class, () -> readBytes(mDls));

            // Assert correct error code
            Throwable cause = thrown.getCause();
            assertInstanceOf(DracoonApiException.class, cause);
            DracoonApiException exception = (DracoonApiException) cause;
            assertEquals(code, exception.getCode());
        }

    }

    @Nested
    class ReadStandardOneChunkTests {

        private final String DATA_PATH = "/download/read_standard_one_chunk/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 4, null);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read.json");
        }

        @Test
        void testRequestsValidSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_skip_read.json");
        }

        @Test
        void testLengthCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read bytes
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(512L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(384L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read all and read 1 byte
            readBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Skip all and read 1 byte
            skipBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read bytes
            byte[] data = readBytes(mDls, 128L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read, skip and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Read all and read 1 byte
            readBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read.json");

            // Skip all and read 1 byte
            skipBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

    }

    @Nested
    class ReadStandardMultiChunkTests {

        private final String DATA_PATH = "/download/read_standard_multi_chunk/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 5, null);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_skip_read_1.json");
            checkRequest(DATA_PATH + "download_request_skip_read_2.json");
        }

        @Test
        void testRequestsValidReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipChunkReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_skip_read_2.json");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(4112L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(3984L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(3984L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(1936L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_skip_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_read_2.json");
            enqueueResponse(DATA_PATH + "download_response_read_3.json");

            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_read_1.json");
            enqueueResponse(DATA_PATH + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_chunk_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

    }

    @Nested
    class ReadEncryptedOneChunkTests {

        private final String DATA_PATH = "/download/read_encrypted_one_chunk/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");
            enqueueResponse(DATA_PATH + "download_response.json");

            // Create and start download
            PlainFileKey fileKey = readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 6, fileKey);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidRead() throws Exception {
            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request.json");
        }

        @Test
        void testRequestsValidSkipRead() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request.json");
        }

        @Test
        void testLengthCorrectAfterRead() throws Exception {
            // Read bytes
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(512L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipRead() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(384L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAllRead() throws Exception {
            // Read all and read 1 byte
            readBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipAllRead() throws Exception {
            // Skip all and read 1 byte
            skipBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterRead() throws Exception {
            // Read bytes
            byte[] data = readBytes(mDls, 128L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipRead() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipRead() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAllRead() throws Exception {
            // Read all and read 1 byte
            readBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipAllRead() throws Exception {
            // Skip all and read 1 byte
            skipBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

    }

    @Nested
    class ReadEncryptedMultiChunkTests {

        private final String DATA_PATH = "/download/read_encrypted_multi_chunk/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");
            enqueueResponse(DATA_PATH + "download_response_3.json");

            // Create and start download
            PlainFileKey fileKey = readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 7, fileKey);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidReadAll() throws Exception {
            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipReadAll() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipChunkReadAll() throws Exception {
            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(DATA_PATH + "download_request_read_1.json");
            checkRequest(DATA_PATH + "download_request_read_2.json");
            checkRequest(DATA_PATH + "download_request_read_3.json");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(4112L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(3984L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadSkipReadAll() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(3984L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(1936L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipReadAll() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE * 1024L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(DATA_PATH + "correct_data_skip_chunk_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

    }

    // --- Skip tests ---

    @Nested
    class SkipStandardTests {

        private final String DATA_PATH = "/download/skip_standard/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 8, null);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.DownloadStreamTest#" +
                "createSkipTestsTestSkippedCorrectArguments")
        void testSkippedCorrect(long skip, long expectedSkipped) throws Exception {
            // Skip bytes
            long skipped = mDls.skip(skip);

            // Assert skipped is correct
            assertEquals(expectedSkipped, skipped, "Skipped bytes does not match!");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.DownloadStreamTest#" +
                "createSkipTestsTestSkippedCorrectAfterReadArguments")
        void testSkippedCorrectAfterRead(long skip, long expectedSkipped) throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");

            // Read some bytes
            readBytes(mDls, 16L);
            // Skip bytes
            long skipped = mDls.skip(skip);

            // Assert skipped is correct
            assertEquals(expectedSkipped, skipped, "Skipped bytes does not match!");
        }

    }

    @Nested
    class SkipEncryptedTests {

        private final String DATA_PATH = "/download/skip_encrypted/";

        @BeforeEach
        void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_node_response.json");
            enqueueResponse(DATA_PATH + "create_download_url_response.json");

            // Create and start download
            PlainFileKey fileKey = readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
            mDls = new DownloadStream(mDracoonClientImpl, "Test", 9, fileKey);
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.DownloadStreamTest#" +
                "createSkipTestsTestSkippedCorrectArguments")
        void testSkippedCorrect(long skip, long expectedSkipped) throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");

            // Skip bytes
            long skipped = mDls.skip(skip);

            // Assert skipped is correct
            assertEquals(expectedSkipped, skipped, "Skipped bytes does not match!");
        }

        @ParameterizedTest
        @MethodSource("com.dracoon.sdk.internal.DownloadStreamTest#" +
                "createSkipTestsTestSkippedCorrectAfterReadArguments")
        void testSkippedCorrectAfterRead(long skip, long expectedSkipped) throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "download_response_1.json");
            enqueueResponse(DATA_PATH + "download_response_2.json");

            // Read some bytes
            readBytes(mDls, 16L);
            // Skip bytes
            long skipped = mDls.skip(skip);

            // Assert skipped is correct
            assertEquals(expectedSkipped, skipped, "Skipped bytes does not match!");
        }

    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createSkipTestsTestSkippedCorrectArguments() {
        return Stream.of(
                Arguments.of(-1L,   0L),    // Skip negative bytes
                Arguments.of(0L,    0L),    // Skip zero bytes
                Arguments.of(1024L, 1024L), // Skip bytes in first chunk
                Arguments.of(2048L, 2048L), // Skip all bytes in first chunk
                Arguments.of(2056L, 2056L), // Skip bytes in second chunk
                Arguments.of(2064L, 2064L), // Skip all bytes
                Arguments.of(2065L, 2064L)  // Skip more bytes then available
        );
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createSkipTestsTestSkippedCorrectAfterReadArguments() {
        return Stream.of(
                Arguments.of(-1L,   0L),    // Skip negative bytes
                Arguments.of(0L,    0L),    // Skip zero bytes
                Arguments.of(1024L, 1024L), // Skip bytes in first chunk
                Arguments.of(2032L, 2032L), // Skip all bytes in first chunk
                Arguments.of(2040L, 2040L), // Skip bytes in second chunk
                Arguments.of(2048L, 2048L), // Skip all bytes
                Arguments.of(2049L, 2048L)  // Skip more bytes then available
        );
    }

    // --- Close tests ---

    // TODO

    // --- Callback tests ---

    // TODO

    // --- Helper methods ---

    private static long countReadBytes(DownloadStream dls) throws IOException {
        return readBytes(dls, new ByteArrayOutputStream());
    }

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

    private static long countReadBytes(DownloadStream dls, long length) throws IOException {
        return readBytes(dls, new ByteArrayOutputStream(), length);
    }

    private static byte[] readBytes(DownloadStream dls, long length) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        readBytes(dls, os, length);
        return os.toByteArray();
    }

    private static long readBytes(InputStream is, OutputStream os, long length) throws IOException {
        byte[] b = new byte[1024];
        long read = 0L;
        long remaining = length;
        while (remaining > 0L) {
            int len = remaining > b.length ? b.length : (int) remaining;
            int cnt = is.read(b, 0, len);
            if (cnt < 0) {
                break;
            }
            os.write(b, 0, cnt);
            read += cnt;
            remaining -= cnt;
        }
        return read;
    }

    private static void skipBytes(DownloadStream dls) throws IOException {
        while (dls.skip(128L) > 0L) {};
    }

    private static void skipBytes(DownloadStream dls, long skip) throws IOException {
        dls.skip(skip);
    }

}
