package com.dracoon.sdk.internal;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.model.FileDownloadCallback;
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

public class DracoonNodesDownloadTest extends DracoonRequestHandlerTest {

    private static class StubOutputStream extends OutputStream {
        @Override
        public void write(int b) {

        }
    }

    @Mock
    protected FileKeyFetcher mFileKeyFetcher;

    private DracoonNodesImpl mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setFileKeyFetcher(mFileKeyFetcher);

        mDni = new DracoonNodesImpl(mDracoonClientImpl);
    }

    private static abstract class BaseTests {

        protected final String mDataPath;

        protected BaseTests(String dataPath) {
            mDataPath = dataPath;
        }

        protected PlainFileKey readPlainFileKeyData() {
            return readData(PlainFileKey.class, mDataPath + "plain_file_key.json");
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseDownloadFileTests extends BaseTests {

        protected final String mDownloadId = "test";
        protected final long mNodeId;
        protected final OutputStream mStream;

        @Mock
        protected FileStreamHelper mFileStreamHelper;

        @Mock
        protected DownloadThread mDownloadThread;

        @Mock
        protected FileDownloadCallback mFileDownloadCallback;

        protected BaseDownloadFileTests(String dataPath) {
            super(dataPath);
            mNodeId = 2L;
            mStream = new StubOutputStream();
        }

        @BeforeEach
        protected void setup() {
            mDracoonClientImpl.setFileStreamHelper(mFileStreamHelper);
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        protected void executeTestDependencyError() {
            assertThrows(DracoonNetIOException.class, this::executeMockedWithException);
        }

        protected abstract void executeMocked() throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        protected void executeMockedDownloadThread() throws Exception {
            try (MockedStatic<DownloadThread> mock = Mockito.mockStatic(DownloadThread.class)) {
                mock.when(() -> DownloadThread.create(any(), anyString(), anyLong(), any(), any()))
                        .thenReturn(mDownloadThread);
                executeDownload();
            }
        }

        protected void executeMockedAndVerifiedDownloadThread() throws Exception {
            executeMockedAndVerifiedDownloadThread(null);
        }

        protected void executeMockedAndVerifiedDownloadThread(PlainFileKey fileKey) throws Exception {
            try (MockedStatic<DownloadThread> mock = Mockito.mockStatic(DownloadThread.class)) {
                mock.when(() -> DownloadThread.create(any(), anyString(), anyLong(), any(), any()))
                        .thenReturn(mDownloadThread);
                executeDownload();
                mock.verify(() -> DownloadThread.create(mDracoonClientImpl, mDownloadId, mNodeId,
                        fileKey, mStream));
            }

            verifyDownloadThreadCall();
        }

        protected void executeMockedWithExceptionDownloadThread() throws Exception {
            mockDownloadThreadCallException();

            try (MockedStatic<DownloadThread> mock = Mockito.mockStatic(DownloadThread.class)) {
                mock.when(() -> DownloadThread.create(any(), anyString(), anyLong(), any(), any()))
                        .thenReturn(mDownloadThread);
                executeDownload();
            }
        }

        protected abstract void mockDownloadThreadCallException() throws Exception;

        protected abstract void verifyDownloadThreadCall() throws Exception;

        protected abstract void executeDownload() throws Exception;

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseDownloadFileStandardTests extends BaseDownloadFileTests {

        protected BaseDownloadFileStandardTests() {
            super("/nodes/download/");
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseDownloadFileEncryptedTests extends BaseDownloadFileTests {

        protected BaseDownloadFileEncryptedTests() {
            super("/nodes/download_encrypted/");
        }

        @Override
        protected void executeMockedDownloadThread() throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(readPlainFileKeyData());
            super.executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerifiedDownloadThread() throws Exception {
            PlainFileKey plainFileKey = readPlainFileKeyData();
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(plainFileKey);
            super.executeMockedAndVerifiedDownloadThread(plainFileKey);
            verify(mFileKeyFetcher).getPlainFileKey(2L);
        }

        @Override
        protected void executeMockedWithExceptionDownloadThread() throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(readPlainFileKeyData());
            super.executeMockedWithExceptionDownloadThread();
        }

    }

    // --- Synchronous download tests ---

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseDownloadFileSyncStandardTests extends BaseDownloadFileStandardTests {

        @Test
        void testDependencyError() {
            executeTestDependencyError();
        }

        @Override
        protected void verifyDownloadThreadCall() throws Exception {
            verify(mDownloadThread).addCallback(mFileDownloadCallback);
            verify(mDownloadThread).runSync();
        }

        @Override
        protected void mockDownloadThreadCallException() throws Exception {
            doThrow(new DracoonNetIOException()).when(mDownloadThread).runSync();
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseDownloadFileSyncEncryptedTests extends BaseDownloadFileEncryptedTests {

        @Test
        void testDependencyError() {
            executeTestDependencyError();
        }

        @Override
        protected void verifyDownloadThreadCall() throws Exception {
            verify(mDownloadThread).addCallback(mFileDownloadCallback);
            verify(mDownloadThread).runSync();
        }

        @Override
        protected void mockDownloadThreadCallException() throws Exception {
            doThrow(new DracoonNetIOException()).when(mDownloadThread).runSync();
        }

    }

    @Nested
    class DownloadFileWithFileSyncStandardTests extends BaseDownloadFileSyncStandardTests {

        private final File mFile;

        DownloadFileWithFileSyncStandardTests() {
            super();
            mFile = new File("");
        }

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            executeMockedAndVerifiedDownloadThread();
            verifyGetFileStreamCall();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockGetFileStreamCall();
            executeMockedWithExceptionDownloadThread();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileOutputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileOutputStream(mFile);
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.downloadFile(mDownloadId, mNodeId, mFile, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithFileSyncEncryptedTests extends BaseDownloadFileSyncEncryptedTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            executeMockedAndVerifiedDownloadThread();
            verifyGetFileStreamCall();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            mockGetFileStreamCall();
            executeMockedWithExceptionDownloadThread();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileOutputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileOutputStream(mFile);
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.downloadFile(mDownloadId, mNodeId, mFile, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithStreamSyncStandardTests extends BaseDownloadFileSyncStandardTests {

        @Override
        protected void executeMocked() throws Exception {
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            executeMockedAndVerifiedDownloadThread();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            executeMockedWithExceptionDownloadThread();
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.downloadFile(mDownloadId, mNodeId, mStream, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithStreamSyncEncryptedTests extends BaseDownloadFileSyncEncryptedTests {

        @Override
        protected void executeMocked() throws Exception {
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            executeMockedAndVerifiedDownloadThread();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            executeMockedWithExceptionDownloadThread();
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.downloadFile(mDownloadId, mNodeId, mStream, mFileDownloadCallback);
        }

    }

    // --- Asynchronous download tests ---

    @SuppressWarnings("unused")
    private abstract class BaseDownloadFileAsyncStandardTests extends BaseDownloadFileStandardTests {

        @Test
        void testDownloadThreadExists() throws Exception {
            // Execute method to test
            executeMocked();

            // Assert download thread matches mocked download thread
            DownloadThread downloadThread = mDni.getDownloadThread(mDownloadId);
            assertEquals(mDownloadThread, downloadThread);
        }

        @Test
        void testDownloadThreadExitsAfterStart() throws Exception {
            executeTestDownloadThreadExists(c -> c.onStarted(mDownloadId));
        }

        @Test
        void testDownloadThreadExitsAfterProgress() throws Exception {
            executeTestDownloadThreadExists(c -> c.onRunning(mDownloadId, 0L, 0L));
        }

        private void executeTestDownloadThreadExists(
                Consumer<FileDownloadCallback> callbackConsumer) throws Exception {
            // Execute method to test
            executeMockedWithCallback(callbackConsumer);

            // Assert download thread exists
            DownloadThread downloadThread = mDni.getDownloadThread(mDownloadId);
            assertNotNull(downloadThread);
        }

        @Test
        void testDownloadThreadIsRemovedAfterFinish() throws Exception {
            executeTestDownloadThreadIsRemoved(c -> c.onFinished(mDownloadId));
        }

        @Test
        void testDownloadThreadIsRemovedAfterCancel() throws Exception {
            executeTestDownloadThreadIsRemoved(c -> c.onCanceled(mDownloadId));
        }

        @Test
        void testDownloadThreadIsRemovedAfterFail() throws Exception {
            executeTestDownloadThreadIsRemoved(c -> c.onFailed(mDownloadId, null));
        }

        private void executeTestDownloadThreadIsRemoved(
                Consumer<FileDownloadCallback> callbackConsumer) throws Exception {
            // Execute method to test
            executeMockedWithCallback(callbackConsumer);

            // Assert download thread is not found
            DownloadThread downloadThread = mDni.getDownloadThread(mDownloadId);
            assertNull(downloadThread);
        }

        private void executeMockedWithCallback(Consumer<FileDownloadCallback> callbackConsumer)
                throws Exception {
            ArgumentCaptor<FileDownloadCallback> captor = ArgumentCaptor.forClass(
                    FileDownloadCallback.class);
            doNothing().when(mDownloadThread).addCallback(captor.capture());
            executeMocked();
            List<FileDownloadCallback> callbacks = captor.getAllValues();
            callbacks.forEach(callbackConsumer);
        }

        @Override
        protected void executeMockedWithException() {
            // No need to implement this method here
        }

        @Override
        protected void verifyDownloadThreadCall() {
            verify(mDownloadThread).addCallback(mFileDownloadCallback);
            verify(mDownloadThread).start();
        }

        @Override
        protected void mockDownloadThreadCallException() {
            // No need to implement this method here
        }

    }

    @SuppressWarnings("unused")
    private abstract class BaseDownloadFileAsyncEncryptedTests
            extends BaseDownloadFileEncryptedTests {

        @Override
        protected void executeMockedWithException() {
            // No need to implement this method here
        }

        @Override
        protected void verifyDownloadThreadCall() {
            verify(mDownloadThread).addCallback(mFileDownloadCallback);
            verify(mDownloadThread).start();
        }

        @Override
        protected void mockDownloadThreadCallException() {
            // No need to implement this method here
        }

    }

    @Nested
    class DownloadFileWithFileAsyncStandardTests extends BaseDownloadFileAsyncStandardTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            executeMockedAndVerifiedDownloadThread();
            verifyGetFileStreamCall();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileOutputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileOutputStream(mFile);
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.startDownloadFileAsync(mDownloadId, mNodeId, mFile, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithFileAsyncEncryptedTests extends BaseDownloadFileAsyncEncryptedTests {

        private final File mFile = new File("");

        @Override
        protected void executeMocked() throws Exception {
            mockGetFileStreamCall();
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            mockGetFileStreamCall();
            executeMockedAndVerifiedDownloadThread();
            verifyGetFileStreamCall();
        }

        private void mockGetFileStreamCall() throws Exception {
            when(mFileStreamHelper.getFileOutputStream(any())).thenReturn(mStream);
        }

        private void verifyGetFileStreamCall() throws Exception {
            verify(mFileStreamHelper).getFileOutputStream(mFile);
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.startDownloadFileAsync(mDownloadId, mNodeId, mFile, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithStreamAsyncStandardTests extends BaseDownloadFileAsyncStandardTests {

        @Override
        protected void executeMocked() throws Exception {
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            executeMockedAndVerifiedDownloadThread();
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.startDownloadFileAsync(mDownloadId, mNodeId, mStream, mFileDownloadCallback);
        }

    }

    @Nested
    class DownloadFileWithStreamAsyncEncryptedTests extends BaseDownloadFileAsyncEncryptedTests {

        DownloadFileWithStreamAsyncEncryptedTests() {
            super();
        }

        @Override
        protected void executeMocked() throws Exception {
            executeMockedDownloadThread();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            executeMockedAndVerifiedDownloadThread();
        }

        @Override
        protected void executeDownload() throws Exception {
            mDni.startDownloadFileAsync(mDownloadId, mNodeId, mStream, mFileDownloadCallback);
        }

    }

    @Nested
    class CancelDownloadFileAsyncTests {

        protected final String mDownloadId = "test";

        @Mock
        protected ThreadHelper mThreadHelper;

        @Mock
        protected DownloadThread mDownloadThread;

        @BeforeEach
        void setup() {
            mDracoonClientImpl.setThreadHelper(mThreadHelper);
        }

        @Test
        void testDownloadThreadStillExists() {
            // Add download thread
            mDni.putDownloadThread(mDownloadId, mDownloadThread);

            // Execute method to test
            mDni.cancelDownloadFileAsync("-");

            // Assert download thread still exists
            DownloadThread downloadThread = mDni.getDownloadThread(mDownloadId);
            assertNotNull(downloadThread);
        }

        @Test
        void testDownloadThreadIsInterrupted() {
            // Add download thread
            mDni.putDownloadThread(mDownloadId, mDownloadThread);
            mockThreadHelperCalls();

            // Execute method to test
            mDni.cancelDownloadFileAsync(mDownloadId);

            // Assert download thread is interrupted
            verifyThreadHelperCalls();
        }

        @Test
        void testDownloadThreadIsRemoved() {
            // Add download thread
            mDni.putDownloadThread(mDownloadId, mDownloadThread);
            mockThreadHelperCalls();

            // Execute method to test
            mDni.cancelDownloadFileAsync(mDownloadId);

            // Assert download thread is not found
            DownloadThread downloadThread = mDni.getDownloadThread(mDownloadId);
            assertNull(downloadThread);
        }

        private void mockThreadHelperCalls() {
            when(mThreadHelper.isThreadAlive(any())).thenReturn(true);
        }

        private void verifyThreadHelperCalls() {
            verify(mThreadHelper).isThreadAlive(mDownloadThread);
            verify(mThreadHelper).interruptThread(mDownloadThread);
        }

    }

    // --- Stream download tests ---

    @SuppressWarnings({
            "unused",
            "resource",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseCreateDownloadStreamTests extends BaseTests {

        private final String mDownloadId = "test";
        private final long mNodeId;

        @Mock
        protected DownloadStream mDownloadStream;

        @Mock
        protected FileDownloadCallback mFileDownloadCallback;

        protected BaseCreateDownloadStreamTests(String dataPath) {
            super(dataPath);
            mNodeId = 2L;
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDependencyError() {
            assertThrows(DracoonNetIOException.class, this::executeMockedWithException);
        }

        protected abstract void executeMocked() throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        protected void executeMockedDownloadStream() throws Exception {
            try (MockedStatic<DownloadStream> mock = Mockito.mockStatic(DownloadStream.class)) {
                mock.when(() -> DownloadStream.create(any(), anyString(), anyLong(), any()))
                        .thenReturn(mDownloadStream);
                executeCreateDownloadStream();
            }
        }

        protected void executeMockedAndVerifiedDownloadStream() throws Exception {
            executeMockedAndVerifiedDownloadStream(null);
        }

        protected void executeMockedAndVerifiedDownloadStream(PlainFileKey fileKey) throws Exception {
            try (MockedStatic<DownloadStream> mock = Mockito.mockStatic(DownloadStream.class)) {
                mock.when(() -> DownloadStream.create(any(), anyString(), anyLong(), any()))
                        .thenReturn(mDownloadStream);
                executeCreateDownloadStream();
                mock.verify(() -> DownloadStream.create(mDracoonClientImpl, mDownloadId, mNodeId,
                        fileKey));
            }

            verify(mDownloadStream).start();
        }

        protected void executedMockedWithExceptionDownloadStream() throws Exception {
            doThrow(new DracoonNetIOException()).when(mDownloadStream).start();

            try (MockedStatic<DownloadStream> mock = Mockito.mockStatic(DownloadStream.class)) {
                mock.when(() -> DownloadStream.create(any(), anyString(), anyLong(), any()))
                        .thenReturn(mDownloadStream);
                executeCreateDownloadStream();
            }
        }

        private void executeCreateDownloadStream() throws Exception {
            mDni.createFileDownloadStream(mDownloadId, mNodeId, mFileDownloadCallback);
        }

    }

    @Nested
    class CreateDownloadStreamStandardTests extends BaseCreateDownloadStreamTests {

        CreateDownloadStreamStandardTests() {
            super("/nodes/download/");
        }

        @Override
        protected void executeMocked() throws Exception {
            executeMockedDownloadStream();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            executeMockedAndVerifiedDownloadStream();
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            executedMockedWithExceptionDownloadStream();
        }

    }

    @Nested
    class CreateDownloadStreamEncryptedTests extends BaseCreateDownloadStreamTests {

        CreateDownloadStreamEncryptedTests() {
            super("/nodes/download_encrypted/");
        }

        @Override
        protected void executeMocked() throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(readPlainFileKeyData());
            executeMockedDownloadStream();
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            PlainFileKey plainFileKey = readPlainFileKeyData();
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(plainFileKey);
            executeMockedAndVerifiedDownloadStream(plainFileKey);
            verify(mFileKeyFetcher).getPlainFileKey(2L);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(readPlainFileKeyData());
            executedMockedWithExceptionDownloadStream();
        }

    }

}
