package com.dracoon.sdk.internal.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.FileStreamHelper;
import com.dracoon.sdk.internal.ThreadHelper;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NodesServiceUploadTest extends BaseServiceTest {

    private static class StubInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return 0;
        }
    }

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    private NodesService mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);

        mDni = new NodesService(mDracoonClientImpl);
    }

    private abstract class BaseTests {

        protected final String mDataPath;

        @Mock
        protected AccountService mDracoonAccountImpl;

        protected BaseTests(String dataPath) {
            mDataPath = dataPath;
        }

        @BeforeEach
        protected void setup() {
            mDracoonClientImpl.setUserService(mDracoonAccountImpl);
        }

        protected UserKeyPair readUserKeyPairData() {
            return readData(UserKeyPair.class, mDataPath + "user_key_pair_2048.json");
        }

        protected PlainFileKey readPlainFileKeyData() {
            return readData(PlainFileKey.class, mDataPath + "plain_file_key.json");
        }

        protected void mockGetUserKeyPairCall(UserKeyPair userKeyPair) throws Exception {
            when(mDracoonAccountImpl.getPreferredUserKeyPair()).thenReturn(userKeyPair);
        }

        protected void verifyGetUserKeyPairCall() throws Exception {
            verify(mDracoonAccountImpl).getPreferredUserKeyPair();
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseUploadFileTests<T> extends BaseTests {

        protected final Class<T> mDataClass;

        protected final String mUploadId = "test";
        protected final FileUploadRequest mUploadRequest;
        protected final InputStream mStream;

        @Mock
        protected FileStreamHelper mFileStreamHelper;

        @Mock
        protected UploadThread mUploadThread;

        @Mock
        protected FileUploadCallback mFileUploadCallback;

        protected BaseUploadFileTests(String dataPath, Class<T> dataClass) {
            super(dataPath);
            mDataClass = dataClass;
            mUploadRequest = new FileUploadRequest.Builder(1L, "test.txt").build();
            mStream = new StubInputStream();
        }

        @BeforeEach
        protected void setup() {
            super.setup();
            mDracoonClientImpl.setFileStreamHelper(mFileStreamHelper);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMocked();

            // Assert requests are valid
            checkRequests();
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMockedAndVerified();
        }

        void executeTestDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            T expectedData = getExpectedData();
            T data = executeMockedWithReturn(expectedData);

            // Assert data is correct
            assertEquals(expectedData, data);
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodesQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(mDataPath + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeTestApiError);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        void executeTestDependencyError() {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            assertThrows(DracoonNetIOException.class, this::executeMockedWithException);
        }

        protected void enqueueOkResponses() {
            enqueueResponse(mDataPath + "get_node_response.json");
        }

        protected void checkRequests() throws Exception {
            checkRequest(mDataPath + "get_node_request.json");
        }

        protected abstract void executeMocked() throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract T executeMockedWithReturn(T expectedData) throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        protected abstract void executeTestApiError() throws Exception;

        protected void mockDependencyCalls() throws Exception {
            mockAndReturnDependencyCalls(null);
        }

        protected T mockAndReturnDependencyCalls(T expectedData) throws Exception {
            mockUploadThreadCall(expectedData);

            try (MockedStatic<UploadThread> mock = Mockito.mockStatic(UploadThread.class)) {
                mock.when(() -> UploadThread.create(any(), anyString(), any(), anyLong(), any(),
                                any(), any()))
                        .thenReturn(mUploadThread);
                return executeUpload();
            }
        }

        protected void mockAndVerifyDependencyCalls(long length) throws Exception {
            mockAndVerifyDependencyCalls(length, null, null);
        }

        protected void mockAndVerifyDependencyCalls(long length, UserPublicKey userPublicKey,
                PlainFileKey fileKey) throws Exception {
            mockUploadThreadCall(getExpectedData());

            try (MockedStatic<UploadThread> mock = Mockito.mockStatic(UploadThread.class)) {
                mock.when(() -> UploadThread.create(any(), anyString(), any(), anyLong(), any(),
                                any(), any()))
                        .thenReturn(mUploadThread);
                executeUpload();
                mock.verify(() -> UploadThread.create(mDracoonClientImpl, mUploadId, mUploadRequest,
                        length, userPublicKey, fileKey, mStream));
            }

            verifyUploadThreadCall();
        }

        protected void mockWithExceptionDependencyCalls() throws Exception {
            mockUploadThreadCallException();

            try (MockedStatic<UploadThread> mock = Mockito.mockStatic(UploadThread.class)) {
                mock.when(() -> UploadThread.create(any(), anyString(), any(), anyLong(), any(),
                                any(), any()))
                        .thenReturn(mUploadThread);
                executeUpload();
            }
        }

        protected abstract T getExpectedData();

        protected abstract void mockUploadThreadCall(T expectedData) throws Exception;

        protected abstract void mockUploadThreadCallException() throws Exception;

        protected abstract void verifyUploadThreadCall() throws Exception;

        protected abstract T executeUpload() throws Exception;

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseUploadFileStandardTests<T> extends BaseUploadFileTests<T> {

        protected BaseUploadFileStandardTests(Class<T> dataClass) {
            super("/nodes/upload/", dataClass);
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseUploadFileEncryptedTests<T> extends BaseUploadFileTests<T> {

        protected BaseUploadFileEncryptedTests(Class<T> dataClass) {
            super("/nodes/upload_encrypted/", dataClass);
        }

        @Override
        protected void mockDependencyCalls() throws Exception {
            mockAndReturnDependencyCalls(null);
        }

        @Override
        protected T mockAndReturnDependencyCalls(T expectedData) throws Exception {
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(readPlainFileKeyData());
            return super.mockAndReturnDependencyCalls(expectedData);
        }

        @Override
        protected void mockAndVerifyDependencyCalls(long length) throws Exception {
            UserKeyPair userKeyPair = readUserKeyPairData();
            mockGetUserKeyPairCall(userKeyPair);

            PlainFileKey plainFileKey = readPlainFileKeyData();
            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(plainFileKey);
            super.mockAndVerifyDependencyCalls(length, userKeyPair.getUserPublicKey(),
                    plainFileKey);
            verify(mCryptoWrapper).generateFileKey(plainFileKey.getVersion());

            verifyGetUserKeyPairCall();
        }

        @Override
        protected void mockWithExceptionDependencyCalls() throws Exception {
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(readPlainFileKeyData());
            super.mockWithExceptionDependencyCalls();
        }

    }

    // --- Synchronous upload tests ---

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseUploadFileSyncStandardTests
            extends BaseUploadFileStandardTests<Node> {

        protected BaseUploadFileSyncStandardTests() {
            super(Node.class);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect();
        }

        @Test
        void testDependencyError() {
            executeTestDependencyError();
        }

        @Override
        protected Node getExpectedData() {
            return new Node();
        }

        @Override
        protected void mockUploadThreadCall(Node expectedData) throws Exception {
            when(mUploadThread.runSync()).thenReturn(expectedData);
        }

        @Override
        protected void verifyUploadThreadCall() throws Exception {
            verify(mUploadThread).addCallback(mFileUploadCallback);
            verify(mUploadThread).runSync();
        }

        @Override
        protected void mockUploadThreadCallException() throws Exception {
            when(mUploadThread.runSync()).thenThrow(new DracoonNetIOException());
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseUploadFileSyncEncryptedTests
            extends BaseUploadFileEncryptedTests<Node> {

        protected BaseUploadFileSyncEncryptedTests() {
            super(Node.class);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect();
        }

        @Test
        void testDependencyError() {
            executeTestDependencyError();
        }

        @Override
        protected Node getExpectedData() {
            return new Node();
        }

        @Override
        protected void mockUploadThreadCall(Node expectedData) throws Exception {
            when(mUploadThread.runSync()).thenReturn(expectedData);
        }

        @Override
        protected void verifyUploadThreadCall() throws Exception {
            verify(mUploadThread).addCallback(mFileUploadCallback);
            verify(mUploadThread).runSync();
        }

        @Override
        protected void mockUploadThreadCallException() throws Exception {
            when(mUploadThread.runSync()).thenThrow(new DracoonNetIOException());
        }

    }

    @Nested
    class UploadFileWithFileSyncStandardTests extends BaseUploadFileSyncStandardTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            mockAndVerifyDependencyCalls(mFile.length());
            verifyGetFileStreamCall();
        }

        @Override
        protected Node executeMockedWithReturn(Node expectedNode) throws Exception {
            mockGetFileStreamCall();
            return mockAndReturnDependencyCalls(expectedNode);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockGetFileStreamCall();
            mockWithExceptionDependencyCalls();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            mockGetFileStreamCall();
            executeUpload();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileInputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileInputStream(mFile);
        }

        @Override
        protected Node executeUpload() throws Exception {
            return mDni.uploadFile(mUploadId, mUploadRequest, mFile, mFileUploadCallback);
        }

    }

    @Nested
    class UploadFileWithFileSyncEncryptedTests extends BaseUploadFileSyncEncryptedTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            mockAndVerifyDependencyCalls(mFile.length());
            verifyGetFileStreamCall();
        }

        @Override
        protected Node executeMockedWithReturn(Node expectedNode) throws Exception {
            mockGetFileStreamCall();
            return mockAndReturnDependencyCalls(expectedNode);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockGetFileStreamCall();
            mockWithExceptionDependencyCalls();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            mockGetFileStreamCall();
            executeUpload();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileInputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileInputStream(mFile);
        }

        @Override
        protected Node executeUpload() throws Exception {
            return mDni.uploadFile(mUploadId, mUploadRequest, mFile, mFileUploadCallback);
        }

    }

    @Nested
    class UploadFileWithStreamSyncStandardTests extends BaseUploadFileSyncStandardTests {

        @Override
        protected void executeMocked() throws Exception {
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockAndVerifyDependencyCalls(5L);
        }

        @Override
        protected Node executeMockedWithReturn(Node expectedNode) throws Exception {
            return mockAndReturnDependencyCalls(expectedNode);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockWithExceptionDependencyCalls();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            executeUpload();
        }

        @Override
        protected Node executeUpload() throws Exception {
            return mDni.uploadFile(mUploadId, mUploadRequest, mStream, 5L, mFileUploadCallback);
        }

    }

    @Nested
    class UploadFileWithStreamSyncEncryptedTests extends BaseUploadFileSyncEncryptedTests {

        @Override
        protected void executeMocked() throws Exception {
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockAndVerifyDependencyCalls(5L);
        }

        @Override
        protected Node executeMockedWithReturn(Node expectedNode) throws Exception {
            return mockAndReturnDependencyCalls(expectedNode);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockWithExceptionDependencyCalls();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            executeUpload();
        }

        @Override
        protected Node executeUpload() throws Exception {
            return mDni.uploadFile(mUploadId, mUploadRequest, mStream, 5L, mFileUploadCallback);
        }

    }

    // --- Asynchronous upload tests ---

    @SuppressWarnings("unused")
    private abstract class BaseUploadFileAsyncStandardTests
            extends BaseUploadFileStandardTests<Void> {

        protected BaseUploadFileAsyncStandardTests() {
            super(Void.class);
        }

        @Test
        void testUploadThreadExists() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMocked();

            // Assert upload thread matches mocked upload thread
            UploadThread uploadThread = mDni.getUploadThread(mUploadId);
            assertEquals(mUploadThread, uploadThread);
        }

        @Test
        void testUploadThreadExitsAfterStart() throws Exception {
            executeTestUploadThreadExists(c -> c.onStarted(mUploadId));
        }

        @Test
        void testUploadThreadExitsAfterProgress() throws Exception {
            executeTestUploadThreadExists(c -> c.onRunning(mUploadId, 0L, 0L));
        }

        private void executeTestUploadThreadExists(Consumer<FileUploadCallback> callbackConsumer)
                throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMockedWithCallback(callbackConsumer);

            // Assert upload thread exists
            UploadThread uploadThread = mDni.getUploadThread(mUploadId);
            assertNotNull(uploadThread);
        }

        @Test
        void testUploadThreadIsRemovedAfterFinish() throws Exception {
            executeTestUploadThreadIsRemoved(c -> c.onFinished(mUploadId, null));
        }

        @Test
        void testUploadThreadIsRemovedAfterCancel() throws Exception {
            executeTestUploadThreadIsRemoved(c -> c.onCanceled(mUploadId));
        }

        @Test
        void testUploadThreadIsRemovedAfterFail() throws Exception {
            executeTestUploadThreadIsRemoved(c -> c.onFailed(mUploadId, null));
        }

        private void executeTestUploadThreadIsRemoved(Consumer<FileUploadCallback> callbackConsumer)
                throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMockedWithCallback(callbackConsumer);

            // Assert upload thread is not found
            UploadThread uploadThread = mDni.getUploadThread(mUploadId);
            assertNull(uploadThread);
        }

        private void executeMockedWithCallback(Consumer<FileUploadCallback> callbackConsumer)
                throws Exception {
            ArgumentCaptor<FileUploadCallback> captor = ArgumentCaptor.forClass(
                    FileUploadCallback.class);
            doNothing().when(mUploadThread).addCallback(captor.capture());
            executeMocked();
            List<FileUploadCallback> callbacks = captor.getAllValues();
            callbacks.forEach(callbackConsumer);
        }

        @Override
        protected Void executeMockedWithReturn(Void expectedVoid) {
            // No need to implement this method here
            return null;
        }

        @Override
        protected void executeMockedWithException() {
            // No need to implement this method here
        }

        @Override
        protected Void getExpectedData() {
            return null;
        }

        @Override
        protected void mockUploadThreadCall(Void expectedData) {
            // No need to implement this method here
        }

        @Override
        protected void verifyUploadThreadCall() {
            verify(mUploadThread).addCallback(mFileUploadCallback);
            verify(mUploadThread).start();
        }

        @Override
        protected void mockUploadThreadCallException() {
            // No need to implement this method here
        }

    }

    @SuppressWarnings("unused")
    private abstract class BaseUploadFileAsyncEncryptedTests
            extends BaseUploadFileEncryptedTests<Void> {

        protected BaseUploadFileAsyncEncryptedTests() {
            super(Void.class);
        }

        @Override
        protected Void executeMockedWithReturn(Void expectedVoid) {
            // No need to implement this method here
            return null;
        }

        @Override
        protected void executeMockedWithException() {
            // No need to implement this method here
        }

        @Override
        protected Void getExpectedData() {
            return null;
        }

        @Override
        protected void mockUploadThreadCall(Void expectedData) {
            // No need to implement this method here
        }

        @Override
        protected void verifyUploadThreadCall() {
            verify(mUploadThread).addCallback(mFileUploadCallback);
            verify(mUploadThread).start();
        }

        @Override
        protected void mockUploadThreadCallException() {
            // No need to implement this method here
        }

    }

    @Nested
    class UploadFileWithFileAsyncStandardTests extends BaseUploadFileAsyncStandardTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            mockAndVerifyDependencyCalls(mFile.length());
            verifyGetFileStreamCall();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            mockGetFileStreamCall();
            executeUpload();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileInputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileInputStream(mFile);
        }

        @Override
        protected Void executeUpload() throws Exception {
            mDni.startUploadFileAsync(mUploadId, mUploadRequest, mFile, mFileUploadCallback);
            return null;
        }

    }

    @Nested
    class UploadFileWithFileAsyncEncryptedTests extends BaseUploadFileAsyncEncryptedTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            mockAndVerifyDependencyCalls(mFile.length());
            verifyGetFileStreamCall();
        }

        @Override
        protected void executeTestApiError() throws Exception {
            mockGetFileStreamCall();
            executeUpload();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileInputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileInputStream(mFile);
        }

        @Override
        protected Void executeUpload() throws Exception {
            mDni.startUploadFileAsync(mUploadId, mUploadRequest, mFile, mFileUploadCallback);
            return null;
        }

    }

    @Nested
    class UploadFileWithStreamAsyncStandardTests extends BaseUploadFileAsyncStandardTests {

        @Override
        protected void executeMocked() throws Exception {
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockAndVerifyDependencyCalls(5L);
        }

        @Override
        protected void executeTestApiError() throws Exception {
            executeUpload();
        }

        @Override
        protected Void executeUpload() throws Exception {
            mDni.startUploadFileAsync(mUploadId, mUploadRequest, mStream, 5L, mFileUploadCallback);
            return null;
        }

    }

    @Nested
    class UploadFileWithStreamAsyncEncryptedTests extends BaseUploadFileAsyncEncryptedTests {

        @Override
        protected void executeMocked() throws Exception {
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockAndVerifyDependencyCalls(5L);
        }

        @Override
        protected void executeTestApiError() throws Exception {
            executeUpload();
        }

        @Override
        protected Void executeUpload() throws Exception {
            mDni.startUploadFileAsync(mUploadId, mUploadRequest, mStream, 5L, mFileUploadCallback);
            return null;
        }

    }

    @Nested
    class CancelUploadFileAsyncTests {

        protected final String mUploadId = "test";

        @Mock
        protected ThreadHelper mThreadHelper;

        @Mock
        protected UploadThread mUploadThread;

        @BeforeEach
        void setup() {
            mDracoonClientImpl.setThreadHelper(mThreadHelper);
        }

        @Test
        void testUploadThreadStillExists() {
            // Add upload thread
            mDni.putUploadThread(mUploadId, mUploadThread);

            // Execute method to test
            mDni.cancelUploadFileAsync("-");

            // Assert upload thread still exists
            UploadThread uploadThread = mDni.getUploadThread(mUploadId);
            assertNotNull(uploadThread);
        }

        @Test
        void testUploadThreadIsInterrupted() {
            // Add upload thread
            mDni.putUploadThread(mUploadId, mUploadThread);
            mockThreadHelperCalls();

            // Execute method to test
            mDni.cancelUploadFileAsync(mUploadId);

            // Assert upload thread is interrupted
            verifyThreadHelperCalls();
        }

        @Test
        void testUploadThreadIsRemoved() {
            // Add upload thread
            mDni.putUploadThread(mUploadId, mUploadThread);
            mockThreadHelperCalls();

            // Execute method to test
            mDni.cancelUploadFileAsync(mUploadId);

            // Assert upload thread is not found
            UploadThread uploadThread = mDni.getUploadThread(mUploadId);
            assertNull(uploadThread);
        }

        private void mockThreadHelperCalls() {
            when(mThreadHelper.isThreadAlive(any())).thenReturn(true);
        }

        private void verifyThreadHelperCalls() {
            verify(mThreadHelper).isThreadAlive(mUploadThread);
            verify(mThreadHelper).interruptThread(mUploadThread);
        }

    }

    // --- Stream upload tests ---

    @SuppressWarnings({
            "unused",
            "resource",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseCreateUploadStreamTests extends BaseTests {

        private final String mUploadId = "test";
        private final FileUploadRequest mUploadRequest;
        private final long mLength = 5L;

        @Mock
        protected UploadStream mUploadStream;

        @Mock
        protected FileUploadCallback mFileUploadCallback;

        protected BaseCreateUploadStreamTests(String dataPath) {
            super(dataPath);
            mUploadRequest = new FileUploadRequest.Builder(1L, "test.txt").build();
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMocked();

            // Assert requests are valid
            checkRequests();
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodesQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(mDataPath + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeCreateUploadStream);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testDependencyError() {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            assertThrows(DracoonNetIOException.class, this::executeMockedWithException);
        }

        protected void enqueueOkResponses() {
            enqueueResponse(mDataPath + "get_node_response.json");
        }

        protected void checkRequests() throws Exception {
            checkRequest(mDataPath + "get_node_request.json");
        }

        protected abstract void executeMocked() throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        protected void mockDependencyCalls() throws Exception {
            try (MockedStatic<UploadStream> mock = Mockito.mockStatic(UploadStream.class)) {
                mock.when(() -> UploadStream.create(any(), anyString(), any(), anyLong(), any(),
                                any()))
                        .thenReturn(mUploadStream);
                executeCreateUploadStream();
            }
        }

        protected void mockAndVerifyDependencyCalls() throws Exception {
            mockAndVerifyDependencyCalls(null, null);
        }

        protected void mockAndVerifyDependencyCalls(UserPublicKey userPublicKey,
                PlainFileKey fileKey) throws Exception {
            try (MockedStatic<UploadStream> mock = Mockito.mockStatic(UploadStream.class)) {
                mock.when(() -> UploadStream.create(any(), anyString(), any(), anyLong(), any(),
                                any()))
                        .thenReturn(mUploadStream);
                executeCreateUploadStream();
                mock.verify(() -> UploadStream.create(mDracoonClientImpl, mUploadId, mUploadRequest,
                        mLength, userPublicKey, fileKey));
            }

            verify(mUploadStream).start();
        }

        protected void mockWithExceptionDependencyCalls() throws Exception {
            doThrow(new DracoonNetIOException()).when(mUploadStream).start();

            try (MockedStatic<UploadStream> mock = Mockito.mockStatic(UploadStream.class)) {
                mock.when(() -> UploadStream.create(any(), anyString(), any(), anyLong(), any(),
                                any()))
                        .thenReturn(mUploadStream);
                executeCreateUploadStream();
            }
        }

        private void executeCreateUploadStream() throws Exception {
            mDni.createFileUploadStream(mUploadId, mUploadRequest, mLength, mFileUploadCallback);
        }

    }

    @Nested
    class CreateUploadStreamStandardTests extends BaseCreateUploadStreamTests {

        CreateUploadStreamStandardTests() {
            super("/nodes/upload/");
        }

        @Override
        protected void executeMocked() throws Exception {
            mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockAndVerifyDependencyCalls();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockWithExceptionDependencyCalls();
        }

    }

    @Nested
    class CreateUploadStreamEncryptedTests extends BaseCreateUploadStreamTests {

        CreateUploadStreamEncryptedTests() {
            super("/nodes/upload_encrypted/");
        }

        @Override
        protected void executeMocked() throws Exception {
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(readPlainFileKeyData());
            super.mockDependencyCalls();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readUserKeyPairData();
            mockGetUserKeyPairCall(userKeyPair);

            PlainFileKey plainFileKey = readPlainFileKeyData();
            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(plainFileKey);
            super.mockAndVerifyDependencyCalls(userKeyPair.getUserPublicKey(), plainFileKey);
            verify(mCryptoWrapper).generateFileKey(plainFileKey.getVersion());

            verifyGetUserKeyPairCall();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.generateFileKey(any()))
                    .thenReturn(readPlainFileKeyData());
            super.mockWithExceptionDependencyCalls();
        }

    }

}
