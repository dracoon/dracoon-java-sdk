package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UploadStreamTest extends BaseServiceTest {

    private static final long CHUNK_SIZE = 2048L;

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    private UploadStream mUls;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();
        mDracoonClientImpl.setChunkSize(CHUNK_SIZE);
        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);
    }

    private abstract class BaseUploadTests {

        protected final String mDataPath;

        protected BaseUploadTests(String dataPath) {
            mDataPath = dataPath;
        }

        @BeforeEach
        void baseSetup() throws Exception {
            setup();
        }

        protected abstract void setup() throws Exception;

    }

    private abstract class DcUploadTest extends BaseUploadTests {

        protected DcUploadTest(String dataPath) {
            super(dataPath);
        }

    }

    private abstract class S3UploadTest extends BaseUploadTests {

        protected S3UploadTest(String dataPath) {
            super(dataPath);
        }

        @BeforeEach
        void baseSetup() throws Exception {
            // Overwrite default chunk size to smaller chunks
            mDracoonClientImpl.setChunkSize(3072L);
            // Do test setup
            setup();
        }

    }

    // --- Start tests ---

    @Nested
    class StartTests extends BaseUploadTests {

        StartTests() {
            super("/upload/start/");
        }

        @Override
        protected void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 1024L, null, null);

            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload
            mUls.start();

            // Assert requests are valid
            checkRequest(mDataPath + "get_server_settings_request.json");
        }

        @Test
        void testCreateUploadErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            when(mDracoonErrorParser.parseUploadCreateError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_not_found_response.json");

            // Start upload
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mUls::start);

            // Assert correct error code
            assertEquals(code, thrown.getCode());
        }

        @Test
        void testStartAllowed() throws Exception { // NOSONAR: Test doesn't need assert statement
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload
            mUls.start();
        }

        @Test
        void testWriteBeforeStartNotAllowed() {
            assertThrows(IOException.class, () -> mUls.write(new byte[1]));
        }

        @Test
        void testCompleteBeforeStartNotAllowed() {
            assertThrows(IOException.class, () -> mUls.complete());
        }

    }

    @Nested
    class StartDcTests extends DcUploadTest {

        StartDcTests() {
            super("/upload/start_dc/");
        }

        @Override
        protected void setup() {
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 1024L, null, null);
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload
            mUls.start();

            // Assert requests are valid
            dropRequest();
            checkRequest(mDataPath + "create_upload_request.json");
        }

    }

    @Nested
    class StartS3Tests extends S3UploadTest {

        StartS3Tests() {
            super("/upload/start_s3/");
        }

        @Override
        protected void setup() {
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 1024L, null, null);
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload
            mUls.start();

            // Assert requests are valid
            dropRequest();
            checkRequest(mDataPath + "create_upload_request.json");
        }

    }

    // --- Write tests ---

    private abstract class BaseWriteDcTests extends DcUploadTest {

        protected byte[] mBytes;

        protected BaseWriteDcTests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Read bytes to upload
            mBytes = readBytes();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, mBytes.length,
                    getUserPublicKey(), getPlainFileKey());
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        protected void mockDependencies() throws Exception {

        }

        protected byte[] readBytes() {
            return readFile(mDataPath + "data.bin");
        }

        protected abstract UserPublicKey getUserPublicKey();

        protected abstract PlainFileKey getPlainFileKey();

        protected abstract EncryptedFileKey getEncryptedFileKey();

        protected void testNoChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "complete_upload_response.json");

            // Write bytes and complete
            mUls.complete();

            // Assert requests are valid
            checkRequest(mDataPath + "complete_upload_request.json");
        }

        protected void testOneChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "upload_response.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(mDataPath + "upload_request.json");
            checkRequest(mDataPath + "complete_upload_request.json");
        }

        protected void testMultiChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "upload_response_1.json");
            enqueueResponse(mDataPath + "upload_response_2.json");
            enqueueResponse(mDataPath + "upload_response_3.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(mDataPath + "upload_request_1.json");
            checkRequest(mDataPath + "upload_request_2.json");
            checkRequest(mDataPath + "upload_request_3.json");
            checkRequest(mDataPath + "complete_upload_request.json");
        }

    }

    private abstract class BaseWriteDcStandardTests extends BaseWriteDcTests {

        protected BaseWriteDcStandardTests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return null;
        }

        @Override
        protected PlainFileKey getPlainFileKey() {
            return null;
        }

        @Override
        protected EncryptedFileKey getEncryptedFileKey() {
            return null;
        }

    }

    @Nested
    class WriteDcTests extends BaseWriteDcStandardTests {

        WriteDcTests() {
            super("/upload/write_dc/");
        }

        @Override
        protected byte[] readBytes() {
            return new byte[2049];
        }

        @Test
        void testUploadError() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseUploadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "upload_failed_response.json");

            // Write bytes
            IOException thrown = assertThrows(IOException.class, () -> mUls.write(mBytes));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

    }

    @Nested
    class WriteDcEmptyFileTests extends BaseWriteDcStandardTests {

        WriteDcEmptyFileTests() {
            super("/upload/write_dc_empty_file/");
        }

        @Override
        protected byte[] readBytes() {
            return new byte[0];
        }

        @Test
        void testRequestsValid() throws Exception {
            testNoChunk();
        }

    }

    @Nested
    class WriteDcStandardOneChunkTests extends BaseWriteDcStandardTests {

        WriteDcStandardOneChunkTests() {
            super("/upload/write_dc_standard_one_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteDcStandardMultiChunkTests extends BaseWriteDcStandardTests {

        WriteDcStandardMultiChunkTests() {
            super("/upload/write_dc_standard_multi_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    private abstract class BaseWriteDcEncryptedTests extends BaseWriteDcTests {

        protected BaseWriteDcEncryptedTests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected void mockDependencies() throws Exception {
            when(mCryptoWrapper.createFileEncryptionCipher(any()))
                    .thenCallRealMethod();
            when(mCryptoWrapper.encryptFileKey(any(), any(), any()))
                    .thenReturn(getEncryptedFileKey());
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return readData(UserPublicKey.class, mDataPath + "public_key.json");
        }

        @Override
        protected PlainFileKey getPlainFileKey() {
            return readData(PlainFileKey.class, mDataPath + "plain_file_key.json");
        }

        @Override
        protected EncryptedFileKey getEncryptedFileKey() {
            return readData(EncryptedFileKey.class, mDataPath + "enc_file_key.json");
        }

    }

    @Nested
    class WriteDcEncryptedOneChunkTests extends BaseWriteDcEncryptedTests {

        WriteDcEncryptedOneChunkTests() {
            super("/upload/write_dc_encrypted_one_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteDcEncryptedMultiChunkTests extends BaseWriteDcEncryptedTests {

        WriteDcEncryptedMultiChunkTests() {
            super("/upload/write_dc_encrypted_multi_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    private abstract class BaseWriteS3Tests extends S3UploadTest {

        protected byte[] mBytes;

        protected BaseWriteS3Tests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Read bytes to upload
            mBytes = readBytes();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, mBytes.length,
                    getUserPublicKey(), getPlainFileKey());
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        protected void mockDependencies() throws Exception {

        }

        protected byte[] readBytes() {
            return readFile(mDataPath + "data.bin");
        }

        protected abstract UserPublicKey getUserPublicKey();

        protected abstract PlainFileKey getPlainFileKey();

        protected abstract EncryptedFileKey getEncryptedFileKey();

        protected void testNoChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_url_response.json");
            enqueueResponse(mDataPath + "upload_response.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");
            enqueueResponse(mDataPath + "get_upload_status_response.json");

            // Write bytes and complete
            mUls.complete();

            // Assert requests are valid
            checkRequest(mDataPath + "create_upload_url_request.json");
            checkRequest(mDataPath + "upload_request.json");
            checkRequest(mDataPath + "complete_upload_request.json");
            checkRequest(mDataPath + "get_upload_status_request.json");
        }

        protected void testOneChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_url_response.json");
            enqueueResponse(mDataPath + "upload_response.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");
            enqueueResponse(mDataPath + "get_upload_status_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(mDataPath + "create_upload_url_request.json");
            checkRequest(mDataPath + "upload_request.json");
            checkRequest(mDataPath + "complete_upload_request.json");
            checkRequest(mDataPath + "get_upload_status_request.json");
        }

        protected void testMultiChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_url_response_1.json");
            enqueueResponse(mDataPath + "upload_response_1.json");
            enqueueResponse(mDataPath + "create_upload_url_response_2.json");
            enqueueResponse(mDataPath + "upload_response_2.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");
            enqueueResponse(mDataPath + "get_upload_status_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(mDataPath + "create_upload_url_request_1.json");
            checkRequest(mDataPath + "upload_request_1.json");
            checkRequest(mDataPath + "create_upload_url_request_2.json");
            checkRequest(mDataPath + "upload_request_2.json");
            checkRequest(mDataPath + "complete_upload_request.json");
            checkRequest(mDataPath + "get_upload_status_request.json");
        }

    }

    private abstract class BaseWriteS3StandardTests extends BaseWriteS3Tests {

        protected BaseWriteS3StandardTests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return null;
        }

        @Override
        protected PlainFileKey getPlainFileKey() {
            return null;
        }

        @Override
        protected EncryptedFileKey getEncryptedFileKey() {
            return null;
        }

    }

    @Nested
    class WriteS3Tests extends BaseWriteS3StandardTests {

        WriteS3Tests() {
            super("/upload/write_s3/");
        }

        @Override
        protected byte[] readBytes() {
            return new byte[3073];
        }

        @Test
        void testCreateUploadUrlError() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseS3UploadGetUrlsError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_url_failed_response.json");

            // Write bytes
            IOException thrown = assertThrows(IOException.class, () -> mUls.write(mBytes));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        @Test
        void testUploadError() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseS3UploadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_url_response.json");
            enqueueResponse(mDataPath + "upload_failed_response.json");

            // Write bytes
            IOException thrown = assertThrows(IOException.class, () -> mUls.write(new byte[3073]));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

    }

    @Nested
    class WriteS3EmptyFileTests extends BaseWriteS3StandardTests {

        WriteS3EmptyFileTests() {
            super("/upload/write_s3_empty_file/");
        }

        @Override
        protected byte[] readBytes() {
            return new byte[0];
        }

        @Test
        void testRequestsValid() throws Exception {
            testNoChunk();
        }

    }

    @Nested
    class WriteS3StandardOneChunkTests extends BaseWriteS3StandardTests {

        WriteS3StandardOneChunkTests() {
            super("/upload/write_s3_standard_one_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteS3StandardMultiChunkTests extends BaseWriteS3StandardTests {

        WriteS3StandardMultiChunkTests() {
            super("/upload/write_s3_standard_multi_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    private abstract class BaseWriteS3EncryptedTests extends BaseWriteS3Tests {

        protected BaseWriteS3EncryptedTests(String dataPath) {
            super(dataPath);
        }

        @Override
        protected void mockDependencies() throws Exception {
            when(mCryptoWrapper.createFileEncryptionCipher(any()))
                    .thenCallRealMethod();
            when(mCryptoWrapper.encryptFileKey(any(), any(), any()))
                    .thenReturn(getEncryptedFileKey());
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return readData(UserPublicKey.class, mDataPath + "public_key.json");
        }

        @Override
        protected PlainFileKey getPlainFileKey() {
            return readData(PlainFileKey.class, mDataPath + "plain_file_key.json");
        }

        @Override
        protected EncryptedFileKey getEncryptedFileKey() {
            return readData(EncryptedFileKey.class, mDataPath + "enc_file_key.json");
        }

    }

    @Nested
    class WriteS3EncryptedOneChunkTests extends BaseWriteS3EncryptedTests {

        WriteS3EncryptedOneChunkTests() {
            super("/upload/write_s3_encrypted_one_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteS3EncryptedMultiChunkTests extends BaseWriteS3EncryptedTests {

        WriteS3EncryptedMultiChunkTests() {
            super("/upload/write_s3_encrypted_multi_chunk/");
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    // --- Complete tests ---

    @Nested
    class CompleteDcTests extends DcUploadTest {

        CompleteDcTests() {
            super("/upload/complete_dc/");
        }

        @Override
        protected void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 0, null, null);
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueCompleteUploadResponses();

            // Complete upload
            Node node = mUls.complete();

            // Assert data is correct
            Node expectedNode = readData(Node.class, mDataPath + "node.json");
            assertDeepEquals(expectedNode, node);
        }

        @Test
        void testCompleteAllowed() throws Exception { // NOSONAR: Test doesn't need assert statement
            // Enqueue responses
            enqueueCompleteUploadResponses();

            // Complete upload
            mUls.complete();
        }

        @Test
        void testWriteAfterCompleteNotAllowed() throws Exception {
            // Enqueue responses
            enqueueCompleteUploadResponses();

            // Complete upload
            mUls.complete();

            // Assert that write throws exception
            assertThrows(IOException.class, () -> mUls.write(new byte[1]));
        }

        @Test
        void testCompleteAfterCompleteNotAllowed() throws Exception {
            // Enqueue responses
            enqueueCompleteUploadResponses();

            // Complete upload
            mUls.complete();

            // Assert that complete throws exception
            assertThrows(IOException.class, () -> mUls.complete());
        }

        @Test
        void testCompleteUploadErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseUploadCompleteError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "complete_upload_not_found_response.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        private void enqueueCompleteUploadResponses() {
            enqueueResponse(mDataPath + "complete_upload_response.json");
        }

    }

    @Nested
    class CompleteS3Tests extends S3UploadTest {

        CompleteS3Tests() {
            super("/upload/complete_s3/");
        }

        @Override
        protected void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 0, null, null);
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueGetUploadStatusResponses();

            // Complete upload
            Node node = mUls.complete();

            // Assert data is correct
            Node expectedNode = readData(Node.class, mDataPath + "node.json");
            assertDeepEquals(expectedNode, node);
        }

        @Test
        void testCompleteAllowed() throws Exception { // NOSONAR: Test doesn't need assert statement
            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueGetUploadStatusResponses();

            // Complete upload
            mUls.complete();
        }

        @Test
        void testWriteAfterCompleteNotAllowed() throws Exception {
            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueGetUploadStatusResponses();

            // Complete upload
            mUls.complete();

            // Assert that write throws exception
            assertThrows(IOException.class, () -> mUls.write(new byte[1]));
        }

        @Test
        void testCompleteAfterCompleteNotAllowed() throws Exception {
            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueGetUploadStatusResponses();

            // Complete upload
            mUls.complete();

            // Assert that complete throws exception
            assertThrows(IOException.class, () -> mUls.complete());
        }

        @Test
        void testCompleteUploadErrorNotFound() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseS3UploadCompleteError(any())).thenReturn(code);

            // Enqueue responses
            enqueueUploadResponses();
            enqueueResponse(mDataPath + "complete_upload_not_found_response.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        @Test
        void testGetUploadStatusErrorNotFound1() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseS3UploadStatusError(any(Response.class)))
                    .thenReturn(code);

            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueResponse(mDataPath + "get_upload_status_not_found_response_1.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        @Test
        void testGetUploadStatusErrorNotFound2() {
            // Mock error parsing
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            when(mDracoonErrorParser.parseS3UploadStatusError(any(ApiErrorResponse.class)))
                    .thenReturn(code);

            // Enqueue responses
            enqueueUploadResponses();
            enqueueCompleteUploadResponses();
            enqueueResponse(mDataPath + "get_upload_status_not_found_response_2.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        private void enqueueUploadResponses() {
            enqueueResponse(mDataPath + "create_upload_url_response.json");
            enqueueResponse(mDataPath + "upload_response.json");
        }

        private void enqueueCompleteUploadResponses() {
            enqueueResponse(mDataPath + "complete_upload_response.json");
        }

        private void enqueueGetUploadStatusResponses() {
            enqueueResponse(mDataPath + "get_upload_status_response.json");
        }

    }

    // --- Close tests ---

    @Nested
    class CloseTests {

        @BeforeEach
        void setup() {
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, "Test", request, 512L, null, null);
        }

        @Test
        void testCloseAllowed() throws Exception { // NOSONAR: Test doesn't need assert statement
            mUls.close();
        }

        @Test
        void testWriteAfterCloseNotAllowed() throws Exception {
            mUls.close();
            assertThrows(IOException.class, () -> mUls.write(new byte[1]));
        }

        @Test
        void testCompleteAfterCloseNotAllowed() throws Exception {
            mUls.close();
            assertThrows(IOException.class, () -> mUls.complete());
        }

        @Test
        void testCloseAfterCloseNotAllowed() throws Exception {
            mUls.close();
            assertThrows(IOException.class, () -> mUls.close());
        }

    }

    // --- Callback tests ---

    private abstract class CallbackTests extends BaseUploadTests
            implements FileUploadCallback {

        CallbackTests() {
            super("/upload/callback/");
        }

        protected final String UPLOAD_ID = "Test";

        @BeforeEach
        void baseSetup() throws Exception {
            // Do test setup
            setup();

            // Enqueue responses
            enqueueResponse(mDataPath + "get_server_settings_response.json");
        }

        @Override
        public void onStarted(String id) {}

        @Override
        public void onRunning(String id, long bytesSend, long bytesTotal) {}

        @Override
        public void onFinished(String id, Node node) {}

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
        protected void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, UPLOAD_ID, request, 0, null, null);
            mUls.addCallback(this);
        }

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
            assertEquals(UPLOAD_ID, mOnStartId);
        }

        private void start() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload
            mUls.start();
        }

    }

    @Nested
    class CallbackRunningTests extends CallbackTests {

        private boolean mOnRunningCalled = false;
        private String mOnRunningId;
        private long mOnRunningBytesSend = 0L;
        private long mOnRunningBytesTotal = 0L;

        @Override
        protected void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, UPLOAD_ID, request, 4096L, null, null);
            mUls.addCallback(this);
        }

        @Override
        public void onRunning(String id, long bytesSend, long bytesTotal) {
            mOnRunningCalled = true;
            mOnRunningId = id;
            mOnRunningBytesSend = bytesSend;
            mOnRunningBytesTotal = bytesTotal;
        }

        @Test
        void testOnRunningCalled() throws Exception {
            startAndWrite();
            assertTrue(mOnRunningCalled, "Running callback was not called!");
        }

        @Test
        void testOnRunningParametersCorrect() throws Exception {
            startAndWrite();
            assertEquals(UPLOAD_ID, mOnRunningId);
            assertEquals(2048L, mOnRunningBytesSend);
            assertEquals(4096L, mOnRunningBytesTotal);
        }

        private void startAndWrite() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "upload_response.json");

            // Start upload and write some bytes
            mUls.start();
            Thread.sleep(100L); // NOSONAR: Sleep is needed to receive running callbacks (every 100ms)
            mUls.write(new byte[2048]);
            mUls.write(new byte[2048]);
        }

    }

    @Nested
    class CallbackFinishedTests extends CallbackTests {

        private boolean mOnFinishedCalled = false;
        private String mOnFinishedId;
        private Node mOnFinishedNode;

        @Override
        protected void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, UPLOAD_ID, request, 0L, null, null);
            mUls.addCallback(this);
        }

        @Override
        public void onFinished(String id, Node node) {
            mOnFinishedCalled = true;
            mOnFinishedId = id;
            mOnFinishedNode = node;
        }

        @Test
        void testOnFinishedCalled() throws Exception {
            startAndComplete();
            assertTrue(mOnFinishedCalled, "Finish callback was not called!");
        }

        @Test
        void testOnFinishedParametersCorrect() throws Exception {
            startAndComplete();
            assertEquals(UPLOAD_ID, mOnFinishedId);
            Node node = readData(Node.class, mDataPath + "node.json");
            assertDeepEquals(node, mOnFinishedNode);
        }

        private void startAndComplete() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "complete_upload_response.json");

            // Start and complete upload
            mUls.start();
            mUls.complete();
        }

    }

    @Nested
    class CallbackCanceledTests extends CallbackTests {

        private boolean mOnCanceledCalled = false;
        private String mOnCanceledId;

        @Override
        protected void setup() {
            // Simulate an interrupted thread
            TestHttpHelper httpHelper = (TestHttpHelper) mDracoonClientImpl.getHttpHelper();
            httpHelper.setSimulateInterruptedThread(true);

            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, UPLOAD_ID, request, 0L, null, null);
            mUls.addCallback(this);
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
            // Start upload
            mUls.start();
        }

        @Test
        void testOnCanceledCalledAtWrite() throws Exception {
            startAndWrite();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtWrite() throws Exception {
            startAndWrite();
            assertCallbackParameters();
        }

        private void startAndWrite() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload and write some bytes
            mUls.start();
            mUls.write(new byte[4096]);
        }

        @Test
        void testOnCanceledCalledAtComplete1() throws Exception {
            startWriteAndComplete1();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtComplete1() throws Exception {
            startWriteAndComplete1();
            assertCallbackParameters();
        }

        private void startWriteAndComplete1() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");

            // Start upload, write some bytes and complete upload
            mUls.start();
            mUls.write(new byte[512]);
            mUls.complete();
        }

        @Test
        void testOnCanceledCalledAtComplete2() throws Exception {
            startWriteAndComplete2();
            assertCallbackCalled();
        }

        @Test
        void testOnCanceledParametersCorrectAtComplete2() throws Exception {
            startWriteAndComplete2();
            assertCallbackParameters();
        }

        private void startWriteAndComplete2() throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "upload_response.json");

            // Start upload, write some bytes and complete upload
            mUls.start();
            mUls.write(new byte[512]);
            mUls.complete();
        }

        private void assertCallbackCalled() {
            assertTrue(mOnCanceledCalled, "Canceled callback was not called!");
        }

        private void assertCallbackParameters() {
            assertEquals(UPLOAD_ID, mOnCanceledId);
        }

    }

    @Nested
    class CallbackFailedTests extends CallbackTests {

        private boolean mOnFailedCalled = false;
        private String mOnFailedId;
        private DracoonException mOnFailedException;

        @Override
        protected void setup() {
            // Create upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = UploadStream.create(mDracoonClientImpl, UPLOAD_ID, request, 0L, null, null);
            mUls.addCallback(this);
        }

        @Override
        public void onFailed(String id, DracoonException e) {
            mOnFailedCalled = true;
            mOnFailedId = id;
            mOnFailedException = e;
        }

        @Test
        void testOnFailedCalledAtStart() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            start(code);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtStart() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            start(code);
            assertCallbackParameters(code);
        }

        private void start(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseUploadCreateError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_not_found_response.json");

            // Start upload
            try {
                mUls.start();
            } catch (DracoonApiException e) {
                // Nothing to do here
            }
        }

        @Test
        void testOnFailedCalledAtWrite() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startAndWrite(code);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtWrite() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startAndWrite(code);
            assertCallbackParameters(code);
        }

        private void startAndWrite(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseUploadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "upload_failed_response.json");

            // Start upload and write some bytes
            mUls.start();
            try {
                mUls.write(new byte[4096]);
            } catch (IOException e) {
                // Nothing to do here
            }
        }

        @Test
        void testOnFailedCalledAtComplete1() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startWriteAndComplete1(code);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtComplete1() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startWriteAndComplete1(code);
            assertCallbackParameters(code);
        }

        private void startWriteAndComplete1(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseUploadError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "upload_failed_response.json");

            // Start upload, write some bytes and complete upload
            mUls.start();
            mUls.write(new byte[512]);
            try {
                mUls.complete();
            } catch (IOException e) {
                // Nothing to do here
            }
        }

        @Test
        void testOnFailedCalledAtComplete2() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startWriteAndComplete2(code);
            assertCallbackCalled();
        }

        @Test
        void testOnFailedParametersCorrectAtComplete2() throws Exception {
            DracoonApiCode code = DracoonApiCode.SERVER_UPLOAD_NOT_FOUND;
            startWriteAndComplete2(code);
            assertCallbackParameters(code);
        }

        private void startWriteAndComplete2(DracoonApiCode code) throws Exception {
            // Mock error parsing
            when(mDracoonErrorParser.parseUploadCompleteError(any())).thenReturn(code);

            // Enqueue responses
            enqueueResponse(mDataPath + "create_upload_response.json");
            enqueueResponse(mDataPath + "upload_response.json");
            enqueueResponse(mDataPath + "complete_upload_not_found_response.json");

            // Start upload, write some bytes and complete upload
            mUls.start();
            mUls.write(new byte[512]);
            try {
                mUls.complete();
            } catch (IOException e) {
                // Nothing to do here
            }
        }

        private void assertCallbackCalled() {
            assertTrue(mOnFailedCalled, "Failed callback was not called!");
        }

        private void assertCallbackParameters(DracoonApiCode expectedCode) {
            assertEquals(UPLOAD_ID, mOnFailedId);
            assertEquals(DracoonApiException.class, mOnFailedException.getClass());
            DracoonApiException exception = (DracoonApiException) mOnFailedException;
            assertEquals(expectedCode, exception.getCode());
        }

    }

    // --- Helper methods ---

    private static void writeBytes(UploadStream uls, byte[] data) throws IOException {
        uls.write(data);
        uls.complete();
    }

}
