package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UploadStreamTest extends DracoonRequestHandlerTest {

    private static final int CHUNK_SIZE = 2;

    private UploadStream mUls;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();
        DracoonHttpConfig httpConfig = new DracoonHttpConfig();
        httpConfig.setChunkSize(CHUNK_SIZE);
        mDracoonClientImpl.setHttpConfig(httpConfig);
    }

    private abstract class DcUploadTest {

        @BeforeEach
        void baseSetup() throws Exception {
            // Do test setup
            setup();
        }

        protected abstract void setup() throws Exception;

    }

    private abstract class S3UploadTest {

        @BeforeEach
        void baseSetup() throws Exception {
            // Overwrite API version to allow S3 upload
            mDracoonClientImpl.setApiVersion(DracoonConstants.API_MIN_S3_DIRECT_UPLOAD);
            // Overwrite S3 default chunk size to smaller chunks
            mDracoonClientImpl.setS3DefaultChunkSize(3 * DracoonConstants.KIB);
            // Do test setup
            setup();
        }

        protected abstract void setup() throws Exception;

    }

    // --- Start tests ---

    @Nested
    class StartTests {

        private final String DATA_PATH = "/upload/start/";

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
    class StartDcTests extends DcUploadTest {

        private final String DATA_PATH = "/upload/start_dc/";

        @Override
        protected void setup() {
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
    class StartS3Tests extends S3UploadTest {

        private final String DATA_PATH = "/upload/start_s3/";

        @Override
        protected void setup() {
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

    private abstract class BaseWriteDcTests extends DcUploadTest {

        protected byte[] mBytes;

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Read bytes to upload
            mBytes = readBytes();

            // Enqueue responses
            enqueueResponse(getDataPath() + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, mBytes.length,
                    getUserPublicKey(), getFileKey());
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
        }

        protected void mockDependencies() throws Exception {

        }

        protected abstract String getDataPath();

        protected byte[] readBytes() {
            return readFile(getDataPath() + "data.bin");
        }

        protected abstract UserPublicKey getUserPublicKey();

        protected abstract PlainFileKey getFileKey();

        protected void testNoChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "complete_upload_response.json");

            // Write bytes and complete
            mUls.complete();

            // Assert requests are valid
            checkRequest(getDataPath() + "complete_upload_request.json");
        }

        protected void testOneChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "upload_response.json");
            enqueueResponse(getDataPath() + "complete_upload_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(getDataPath() + "upload_request.json");
            checkRequest(getDataPath() + "complete_upload_request.json");
        }

        protected void testMultiChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "upload_response_1.json");
            enqueueResponse(getDataPath() + "upload_response_2.json");
            enqueueResponse(getDataPath() + "upload_response_3.json");
            enqueueResponse(getDataPath() + "complete_upload_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(getDataPath() + "upload_request_1.json");
            checkRequest(getDataPath() + "upload_request_2.json");
            checkRequest(getDataPath() + "upload_request_3.json");
            checkRequest(getDataPath() + "complete_upload_request.json");
        }

    }

    private abstract class BaseWriteDcStandardTests extends BaseWriteDcTests {

        @Override
        protected UserPublicKey getUserPublicKey() {
            return null;
        }

        @Override
        protected PlainFileKey getFileKey() {
            return null;
        }

    }

    private abstract class BaseWriteDcEncryptedTests extends BaseWriteDcTests {

        @Mock
        protected DracoonNodesImpl mDracoonNodesImpl;

        @Override
        protected void mockDependencies() throws Exception {
            mDracoonClientImpl.setNodesImpl(mDracoonNodesImpl);

            EncryptedFileKey fileKey = readData(EncryptedFileKey.class, getDataPath() +
                    "enc_file_key.json");
            when(mDracoonNodesImpl.encryptFileKey(any(), any(), any())).thenReturn(fileKey);
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return readData(UserPublicKey.class, getDataPath() + "public_key.json");
        }

        @Override
        protected PlainFileKey getFileKey() {
            return readData(PlainFileKey.class, getDataPath() + "plain_file_key.json");
        }

    }

    @Nested
    class WriteDcTests extends BaseWriteDcStandardTests {

        private final String DATA_PATH = "/upload/write_dc/";

        @Override
        protected String getDataPath() {
            return DATA_PATH;
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
            enqueueResponse(DATA_PATH + "upload_failed_response.json");

            // Write bytes
            IOException thrown = assertThrows(IOException.class, () -> mUls.write(mBytes));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

    }

    @Nested
    class WriteDcEmptyFileTests extends BaseWriteDcStandardTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_dc_empty_file/";
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

        @Override
        protected String getDataPath() {
            return "/upload/write_dc_standard_one_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteDcStandardMultiChunkTests extends BaseWriteDcStandardTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_dc_standard_multi_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    @Nested
    class WriteDcEncryptedOneChunkTests extends BaseWriteDcEncryptedTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_dc_encrypted_one_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteDcEncryptedMultiChunkTests extends BaseWriteDcEncryptedTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_dc_encrypted_multi_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    private abstract class BaseWriteS3Tests extends S3UploadTest {

        protected byte[] mBytes;

        @Override
        protected void setup() throws Exception {
            // Mock dependencies
            mockDependencies();

            // Read bytes to upload
            mBytes = readBytes();

            // Enqueue responses
            enqueueResponse(getDataPath() + "get_server_settings_response.json");
            enqueueResponse(getDataPath() + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, mBytes.length,
                    getUserPublicKey(), getFileKey());
            mUls.start();

            // Drop irrelevant requests
            dropRequest();
            dropRequest();
        }

        protected void mockDependencies() throws Exception {

        }

        protected abstract String getDataPath();

        protected byte[] readBytes() {
            return readFile(getDataPath() + "data.bin");
        }

        protected abstract UserPublicKey getUserPublicKey();

        protected abstract PlainFileKey getFileKey();

        protected void testNoChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "create_upload_url_response.json");
            enqueueResponse(getDataPath() + "upload_response.json");
            enqueueResponse(getDataPath() + "complete_upload_response.json");
            enqueueResponse(getDataPath() + "get_upload_status_response.json");

            // Write bytes and complete
            mUls.complete();

            // Assert requests are valid
            checkRequest(getDataPath() + "create_upload_url_request.json");
            checkRequest(getDataPath() + "upload_request.json");
            checkRequest(getDataPath() + "complete_upload_request.json");
            checkRequest(getDataPath() + "get_upload_status_request.json");
        }

        protected void testOneChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "create_upload_url_response.json");
            enqueueResponse(getDataPath() + "upload_response.json");
            enqueueResponse(getDataPath() + "complete_upload_response.json");
            enqueueResponse(getDataPath() + "get_upload_status_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(getDataPath() + "create_upload_url_request.json");
            checkRequest(getDataPath() + "upload_request.json");
            checkRequest(getDataPath() + "complete_upload_request.json");
            checkRequest(getDataPath() + "get_upload_status_request.json");
        }

        protected void testMultiChunk() throws Exception {
            // Enqueue responses
            enqueueResponse(getDataPath() + "create_upload_url_response_1.json");
            enqueueResponse(getDataPath() + "upload_response_1.json");
            enqueueResponse(getDataPath() + "create_upload_url_response_2.json");
            enqueueResponse(getDataPath() + "upload_response_2.json");
            enqueueResponse(getDataPath() + "complete_upload_response.json");
            enqueueResponse(getDataPath() + "get_upload_status_response.json");

            // Write bytes and complete
            writeBytes(mUls, mBytes);

            // Assert requests are valid
            checkRequest(getDataPath() + "create_upload_url_request_1.json");
            checkRequest(getDataPath() + "upload_request_1.json");
            checkRequest(getDataPath() + "create_upload_url_request_2.json");
            checkRequest(getDataPath() + "upload_request_2.json");
            checkRequest(getDataPath() + "complete_upload_request.json");
            checkRequest(getDataPath() + "get_upload_status_request.json");
        }

    }

    private abstract class BaseWriteS3StandardTests extends BaseWriteS3Tests {

        @Override
        protected UserPublicKey getUserPublicKey() {
            return null;
        }

        @Override
        protected PlainFileKey getFileKey() {
            return null;
        }

    }

    private abstract class BaseWriteS3EncryptedTests extends BaseWriteS3Tests {

        @Mock
        protected DracoonNodesImpl mDracoonNodesImpl;

        @Override
        protected void mockDependencies() throws Exception {
            mDracoonClientImpl.setNodesImpl(mDracoonNodesImpl);

            EncryptedFileKey fileKey = readData(EncryptedFileKey.class, getDataPath() +
                    "enc_file_key.json");
            when(mDracoonNodesImpl.encryptFileKey(any(), any(), any())).thenReturn(fileKey);
        }

        @Override
        protected UserPublicKey getUserPublicKey() {
            return readData(UserPublicKey.class, getDataPath() + "public_key.json");
        }

        @Override
        protected PlainFileKey getFileKey() {
            return readData(PlainFileKey.class, getDataPath() + "plain_file_key.json");
        }

    }

    @Nested
    class WriteS3Tests extends BaseWriteS3StandardTests {

        private final String DATA_PATH = "/upload/write_s3/";

        @Override
        protected String getDataPath() {
            return DATA_PATH;
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
            enqueueResponse(DATA_PATH + "create_upload_url_failed_response.json");

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
            enqueueResponse(DATA_PATH + "create_upload_url_response.json");
            enqueueResponse(DATA_PATH + "upload_failed_response.json");

            // Write bytes
            IOException thrown = assertThrows(IOException.class, () -> mUls.write(new byte[3073]));

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

    }

    @Nested
    class WriteS3EmptyFileTests extends BaseWriteS3StandardTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_s3_empty_file/";
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

        @Override
        protected String getDataPath() {
            return "/upload/write_s3_standard_one_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteS3StandardMultiChunkTests extends BaseWriteS3StandardTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_s3_standard_multi_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    @Nested
    class WriteS3EncryptedOneChunkTests extends BaseWriteS3EncryptedTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_s3_encrypted_one_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testOneChunk();
        }

    }

    @Nested
    class WriteS3EncryptedMultiChunkTests extends BaseWriteS3EncryptedTests {

        @Override
        protected String getDataPath() {
            return "/upload/write_s3_encrypted_multi_chunk/";
        }

        @Test
        void testRequestsValid() throws Exception {
            testMultiChunk();
        }

    }

    // --- Complete tests ---

    @Nested
    class CompleteDcTests extends DcUploadTest {

        private final String DATA_PATH = "/upload/complete_dc/";

        @Override
        protected void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, 0, null, null);
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
            Node expectedNode = readData(Node.class, DATA_PATH + "node.json");
            assertDeepEquals(expectedNode, node);
        }

        @Test
        void testCompleteAllowed() throws Exception {
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
            enqueueResponse(DATA_PATH + "complete_upload_not_found_response.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        private void enqueueCompleteUploadResponses() {
            enqueueResponse(DATA_PATH + "complete_upload_response.json");
        }

    }

    @Nested
    class CompleteS3Tests extends S3UploadTest {

        private final String DATA_PATH = "/upload/complete_s3/";

        @Override
        protected void setup() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_server_settings_response.json");
            enqueueResponse(DATA_PATH + "create_upload_response.json");

            // Create and start upload
            FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt").build();
            mUls = new UploadStream(mDracoonClientImpl, "Test", request, 0, null, null);
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
            Node expectedNode = readData(Node.class, DATA_PATH + "node.json");
            assertDeepEquals(expectedNode, node);
        }

        @Test
        void testCompleteAllowed() throws Exception {
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
            enqueueResponse(DATA_PATH + "complete_upload_not_found_response.json");

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
            enqueueResponse(DATA_PATH + "get_upload_status_not_found_response_1.json");

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
            enqueueResponse(DATA_PATH + "get_upload_status_not_found_response_2.json");

            // Complete upload
            IOException thrown = assertThrows(IOException.class, mUls::complete);

            // Assert correct error code
            assertDracoonApiException(thrown, code);
        }

        private void enqueueUploadResponses() {
            enqueueResponse(DATA_PATH + "create_upload_url_response.json");
            enqueueResponse(DATA_PATH + "upload_response.json");
        }

        private void enqueueCompleteUploadResponses() {
            enqueueResponse(DATA_PATH + "complete_upload_response.json");
        }

        private void enqueueGetUploadStatusResponses() {
            enqueueResponse(DATA_PATH + "get_upload_status_response.json");
        }

    }

    // --- Close tests ---

    // TODO

    // --- Callback tests ---

    // TODO

    // --- Helper methods ---

    private static void writeBytes(UploadStream uls, byte[] data) throws IOException {
        uls.write(data);
        uls.complete();
    }

}
