package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.model.FileDownloadCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DownloadStreamTest extends DracoonRequestHandlerTest {

    private static final long CHUNK_SIZE = 2048L;

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    private DownloadStream mDls;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();
        mDracoonClientImpl.setChunkSize(CHUNK_SIZE);
        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);
    }

    private abstract class BaseDownloadTests {

        protected final String mDataPath;

        protected BaseDownloadTests(String dataPath) {
            mDataPath = dataPath;
        }

        @BeforeEach
        void baseSetup() throws Exception {
            setup();
        }

        protected abstract void setup() throws Exception;

    }

    private abstract class BaseStandardDownloadTests extends BaseDownloadTests {

        protected BaseStandardDownloadTests(String dataPath) {
            super(dataPath);
        }

        protected void mockDependencies() {

        }

        protected PlainFileKey getPlainFileKey() {
            return null;
        }

    }

    private abstract class BaseEncryptedDownloadTests extends BaseDownloadTests {

        protected BaseEncryptedDownloadTests(String dataPath) {
            super(dataPath);
        }

        protected void mockDependencies() throws Exception {
            when(mCryptoWrapper.createFileDecryptionCipher(any()))
                    .thenCallRealMethod();
        }

        protected PlainFileKey getPlainFileKey() {
            return readData(PlainFileKey.class, mDataPath + "plain_file_key.json");
        }

    }

    // --- Start tests ---

    @Nested
    class StartTests extends BaseStandardDownloadTests {

        StartTests() {
            super("/download/start/");
        }

        @Override
        protected void setup() {
            // Mock dependencies
            mockDependencies();

            // Create download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 2, getPlainFileKey());
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Start download
            mDls.start();

            // Assert requests are valid
            checkRequest(mDataPath + "get_node_request.json");
            checkRequest(mDataPath + "create_download_url_request.json");
        }

        @Test
        void testGetNodeErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            when(mDracoonErrorParser.parseNodesQueryError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_not_found_response.json");

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
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_not_found_response.json");

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
    class AvailableStandardTests extends BaseStandardDownloadTests {

        AvailableStandardTests() {
            super("/download/available_standard/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 2, getPlainFileKey());
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
            enqueueResponse(mDataPath + "download_response_1.json");

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
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");

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
    class AvailableEncryptedTests extends BaseEncryptedDownloadTests {

        AvailableEncryptedTests() {
            super("/download/available_encrypted/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 2, getPlainFileKey());
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
            enqueueResponse(mDataPath + "download_response_1.json");

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
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");

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
            enqueueResponse(mDataPath + "download_response_1.json");

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
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");

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
    class ReadTests extends BaseStandardDownloadTests {

        ReadTests() {
            super("/download/read/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 3, getPlainFileKey());
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
            enqueueResponse(mDataPath + "download_not_found_response.json");

            // Read bytes
            IOException thrown = assertThrows(IOException.class, () -> readBytes(mDls));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

    }

    @Nested
    class ReadStandardOneChunkTests extends BaseStandardDownloadTests {

        ReadStandardOneChunkTests() {
            super("/download/read_standard_one_chunk/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 4, getPlainFileKey());
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read.json");
        }

        @Test
        void testRequestsValidSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_skip_read.json");
        }

        @Test
        void testLengthCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read bytes
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(512L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls, 128L);

            // Assert size is correct
            assertEquals(128L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(384L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read all and read 1 byte
            readBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Skip all and read 1 byte
            skipBytes(mDls);
            long length = countReadBytes(mDls, 1L);

            // Assert size is correct
            assertEquals(0L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read bytes
            byte[] data = readBytes(mDls, 128L);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read, skip and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Read all and read 1 byte
            readBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipAllRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read.json");

            // Skip all and read 1 byte
            skipBytes(mDls);
            byte[] data = readBytes(mDls, 1L);

            // Assert data is correct
            assertArrayEquals(new byte[0], data, "Downloaded data does not match!");
        }

    }

    @Nested
    class ReadStandardMultiChunkTests extends BaseStandardDownloadTests {

        ReadStandardMultiChunkTests() {
            super("/download/read_standard_multi_chunk/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 5, getPlainFileKey());
            mDls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testRequestsValidReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

            // Read bytes
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_skip_read_1.json");
            checkRequest(mDataPath + "download_request_skip_read_2.json");
        }

        @Test
        void testRequestsValidReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipChunkReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_skip_read_2.json");
        }

        @Test
        void testLengthCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

            // Read bytes
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(4112L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(3984L, length, "Download length does not match!");
        }

        @Test
        void testLengthCorrectAfterReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

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
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(1936L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_skip_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_read_2.json");
            enqueueResponse(mDataPath + "download_response_read_3.json");

            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "download_response_read_1.json");
            enqueueResponse(mDataPath + "download_response_skip_read_2.json");

            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_chunk_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

    }

    @Nested
    class ReadEncryptedOneChunkTests extends BaseEncryptedDownloadTests {

        ReadEncryptedOneChunkTests() {
            super("/download/read_encrypted_one_chunk/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 6, getPlainFileKey());
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
            checkRequest(mDataPath + "download_request.json");
        }

        @Test
        void testRequestsValidSkipRead() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request.json");
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
            byte[] expectedData = readFile(mDataPath + "correct_data_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipRead() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipRead() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls, 256L);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_skip_read.bin");
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
    class ReadEncryptedMultiChunkTests extends BaseEncryptedDownloadTests {

        ReadEncryptedMultiChunkTests() {
            super("/download/read_encrypted_multi_chunk/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");
            enqueueResponse(mDataPath + "download_response_3.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 7, getPlainFileKey());
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
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipReadAll() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
        }

        @Test
        void testRequestsValidReadSkipChunkReadAll() throws Exception {
            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE);
            readBytes(mDls);

            // Assert requests are valid
            checkRequest(mDataPath + "download_request_read_1.json");
            checkRequest(mDataPath + "download_request_read_2.json");
            checkRequest(mDataPath + "download_request_read_3.json");
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
            skipBytes(mDls, CHUNK_SIZE);
            long length = countReadBytes(mDls);

            // Assert size is correct
            assertEquals(1936L, length, "Download length does not match!");
        }

        @Test
        void testDataCorrectAfterReadAll() throws Exception {
            // Read bytes
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterSkipReadAll() throws Exception {
            // Skip and read bytes
            skipBytes(mDls, 128L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipReadAll() throws Exception {
            // Read, skip and read bytes
            readBytes(mDls, 64L);
            skipBytes(mDls, 64L);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

        @Test
        void testDataCorrectAfterReadSkipChunkReadAll() throws Exception {
            // Read, skip chunk and read bytes
            readBytes(mDls, 128L);
            skipBytes(mDls, CHUNK_SIZE);
            byte[] data = readBytes(mDls);

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + "correct_data_skip_chunk_read_all.bin");
            assertArrayEquals(expectedData, data, "Downloaded data does not match!");
        }

    }

    // --- Skip tests ---

    @Nested
    class SkipStandardTests extends BaseStandardDownloadTests {

        SkipStandardTests() {
            super("/download/skip_standard/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 8, getPlainFileKey());
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
            enqueueResponse(mDataPath + "download_response_1.json");

            // Read some bytes
            readBytes(mDls, 16L);
            // Skip bytes
            long skipped = mDls.skip(skip);

            // Assert skipped is correct
            assertEquals(expectedSkipped, skipped, "Skipped bytes does not match!");
        }

    }

    @Nested
    class SkipEncryptedTests extends BaseEncryptedDownloadTests {

        SkipEncryptedTests() {
            super("/download/skip_encrypted/");
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Create and start download
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 9, getPlainFileKey());
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
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");

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
            enqueueResponse(mDataPath + "download_response_1.json");
            enqueueResponse(mDataPath + "download_response_2.json");

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

    @Nested
    class CloseTests {

        @BeforeEach
        void setup() {
            mDls = DownloadStream.create(mDracoonClientImpl, "Test", 9, null);
        }

        @Test
        void testCloseAllowed() throws Exception { // NOSONAR: Test doesn't need assert statement
            mDls.close();
        }

        @Test
        void testAvailableAfterCloseNotAllowed() throws Exception {
            mDls.close();
            assertThrows(IOException.class, () -> mDls.available());
        }

        @Test
        void testReadAfterCloseNotAllowed() throws Exception {
            mDls.close();
            assertThrows(IOException.class, () -> readBytes(mDls));
        }

        @Test
        void testSkipAfterCloseNotAllowed() throws Exception {
            mDls.close();
            assertThrows(IOException.class, () -> skipBytes(mDls));
        }

        @Test
        void testCloseAfterCloseNotAllowed() throws Exception {
            mDls.close();
            assertThrows(IOException.class, () -> mDls.close());
        }

    }

    // --- Callback tests ---

    private abstract class CallbackTests extends BaseEncryptedDownloadTests
            implements FileDownloadCallback {

        protected final String DOWNLOAD_ID = "Test";

        protected CallbackTests() {
            super("/download/callback/");
        }

        @Override
        protected void setup() throws Exception {
            mockDependencies();

            mDls = DownloadStream.create(mDracoonClientImpl, DOWNLOAD_ID, 10, getPlainFileKey());
            mDls.addCallback(this);
        }

        @Override
        public void onStarted(String id) {}

        @Override
        public void onRunning(String id, long bytesRead, long bytesTotal) {}

        @Override
        public void onFinished(String id) {}

        @Override
        public void onCanceled(String id) {}

        @Override
        public void onFailed(String id, DracoonException e) {}

    }

    @Nested
    class CallbackStartTests extends CallbackTests {

        private boolean mOnStartCalled = false;
        private String mOnStartId;

        @Override
        public void onStarted(String id) {
            mOnStartCalled = true;
            mOnStartId = id;
        }

        @Test
        void testOnStartedCalled() throws Exception {
            start();
            assertTrue(mOnStartCalled, "Start callback was not called!");
        }

        @Test
        void testOnStartedParametersCorrect() throws Exception {
            start();
            assertEquals(DOWNLOAD_ID, mOnStartId);
        }

        private void start() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Start download
            mDls.start();
        }

    }

    @Nested
    class CallbackRunningTests extends CallbackTests {

        private boolean mOnRunningCalled = false;
        private String mOnRunningId;
        private long mOnRunningBytesRead = 0L;
        private long mOnRunningBytesTotal = 0L;

        @Override
        protected void setup() throws Exception {
            // Change chunk size to be larger than internal buffer of 2KB
            mDracoonClientImpl.setChunkSize(4096L);

            // Create download
            super.setup();
        }

        @Override
        public void onRunning(String id, long bytesRead, long bytesTotal) {
            mOnRunningCalled = true;
            mOnRunningId = id;
            mOnRunningBytesRead = bytesRead;
            mOnRunningBytesTotal = bytesTotal;
        }

        @Test
        void testOnRunningCalled() throws Exception {
            startAndRead();
            assertTrue(mOnRunningCalled, "Running callback was not called!");
        }

        @Test
        void testOnRunningParametersCorrect() throws Exception {
            startAndRead();
            assertEquals(DOWNLOAD_ID, mOnRunningId);
            assertEquals(2048L, mOnRunningBytesRead);
            assertEquals(4096L, mOnRunningBytesTotal);
        }

        private void startAndRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_response.json");

            // Start download and read some bytes
            mDls.start();
            Thread.sleep(100L); // NOSONAR: Sleep is needed to receive running callbacks (every 100ms)
            readBytes(mDls, 512L);
        }

    }

    @Nested
    class CallbackFinishedTests extends CallbackTests {

        private boolean mOnFinishedCalled = false;
        private String mOnFinishedId;

        @Override
        public void onFinished(String id) {
            mOnFinishedCalled = true;
            mOnFinishedId = id;
        }

        @Test
        void testOnFinishedCalled() throws Exception {
            startAndReadAll();
            assertTrue(mOnFinishedCalled, "Finish callback was not called!");
        }

        @Test
        void testOnFinishedParametersCorrect() throws Exception {
            startAndReadAll();
            assertEquals(DOWNLOAD_ID, mOnFinishedId);
        }

        private void startAndReadAll() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_response.json");

            // Start download and read all bytes
            mDls.start();
            readBytes(mDls);
        }

    }

    @Nested
    class CallbackCanceledTests extends CallbackTests {

        private boolean mOnCanceledCalled = false;
        private String mOnCanceledId;

        @Override
        protected void setup() throws Exception {
            // Simulate an interrupted thread
            TestHttpHelper httpHelper = (TestHttpHelper) mDracoonClientImpl.getHttpHelper();
            httpHelper.setSimulateInterruptedThread(true);

            // Create download
            super.setup();
        }

        @Override
        public void onCanceled(String id) {
            mOnCanceledCalled = true;
            mOnCanceledId = id;
        }

        @Test
        void testOnCanceledCalledAtStart() throws Exception {
            start();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtStart() throws Exception {
            start();
            assertCallbackParameters();
        }

        private void start() throws Exception {
            // Enqueue responses
            enqueueIOErrorResponse(mDataPath + "get_node_response.json", 50L);

            // Start download
            mDls.start();
        }

        @Test
        void testOnCanceledCalledAtRead() throws Exception {
            startAndRead();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtRead() throws Exception {
            startAndRead();
            assertCallbackParameters();
        }

        private void startAndRead() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Start download and read some bytes
            mDls.start();
            readBytes(mDls, 1024L);
        }

        @Test
        void testOnCanceledCalledAtSkip() throws Exception {
            startAndSkip();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtSkip() throws Exception {
            startAndSkip();
            assertCallbackParameters();
        }

        private void startAndSkip() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");

            // Start download and skip some bytes
            mDls.start();
            skipBytes(mDls, 1024L);
        }

        private void assertCallbackCalled() {
            assertTrue(mOnCanceledCalled, "Canceled callback was not called!");
        }

        private void assertCallbackParameters() {
            assertEquals(DOWNLOAD_ID, mOnCanceledId);
        }

    }

    @Nested
    class CallbackFailedTests extends CallbackTests {

        private boolean mOnFailedCalled = false;
        private String mOnFailedId;
        private DracoonException mOnFailedException;

        @Override
        public void onFailed(String id, DracoonException e) {
            mOnFailedCalled = true;
            mOnFailedId = id;
            mOnFailedException = e;
        }

        @Test
        void testOnFailedCalledAtStart() throws Exception {
            start(DracoonApiCode.SERVER_UNKNOWN_ERROR);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtStart() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            start(code);
            assertCallbackParameters(code);
        }

        private void start(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseNodesQueryError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_not_found_response.json");

            // Start download
            try {
                mDls.start();
            } catch (DracoonApiException e) {
                // Nothing to do here
            }
        }

        @Test
        void testOnFailedCalledAtRead() throws Exception {
            startAndRead(DracoonApiCode.SERVER_UNKNOWN_ERROR);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtRead() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_FILE_NOT_FOUND;
            startAndRead(code);
            assertCallbackParameters(code);
        }

        private void startAndRead(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseDownloadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_not_found_response.json");

            // Start download and read some bytes
            mDls.start();
            try {
                readBytes(mDls, 1024L);
            } catch (IOException e) {
                // Nothing to do here
            }
        }

        @Test
        void testOnFailedCalledAtSkip() throws Exception {
            startAndSkip(DracoonApiCode.SERVER_UNKNOWN_ERROR);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtSkip() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_FILE_NOT_FOUND;
            startAndSkip(code);
            assertCallbackParameters(code);
        }

        private void startAndSkip(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseDownloadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "get_node_response.json");
            enqueueResponse(mDataPath + "create_download_url_response.json");
            enqueueResponse(mDataPath + "download_not_found_response.json");

            // Start download and skip some bytes
            mDls.start();
            try {
                skipBytes(mDls, 1024L);
            } catch (IOException e) {
                // Nothing to do here
            }
        }

        private void assertCallbackCalled() {
            assertTrue(mOnFailedCalled, "Failed callback was not called!");
        }

        private void assertCallbackParameters(DracoonApiCode expectedCode) {
            assertEquals(DOWNLOAD_ID, mOnFailedId);
            assertEquals(DracoonApiException.class, mOnFailedException.getClass());
            DracoonApiException exception = (DracoonApiException) mOnFailedException;
            assertEquals(expectedCode, exception.getCode());
        }

    }

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
