package com.dracoon.sdk.internal;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.UploadShare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DracoonSharesTest extends DracoonRequestHandlerTest {

    private DracoonSharesImpl mDsi;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDsi = new DracoonSharesImpl(mDracoonClientImpl);
    }

    private interface SharesTest<T> {
        T execute() throws Exception;
    }

    private abstract class BaseSharesTests<T> {

        private final Class<T> mDataClass;
        private final String mDataPath;

        protected BaseSharesTests(Class<T> dataClass, String dataPath) {
            mDataClass = dataClass;
            mDataPath = dataPath;
        }

        protected void executeTest(String responseFilename, SharesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            test.execute();
        }

        protected void executeTestRequestsValid(String requestFilename, String responseFilename,
                SharesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            test.execute();

            // Assert requests are valid
            checkRequest(mDataPath + requestFilename);
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename,
                SharesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            T data = test.execute();

            // Assert data is correct
            T expectedData = readData(mDataClass, mDataPath + dataFilename);
            assertDeepEquals(expectedData, data);
        }

        protected void executeTestError(String responseFilename, ErrorParserFunction errorParserFunc,
                DracoonApiCode expectedCode, SharesTest<T> test) {
            // Mock error parsing
            mockParseError(errorParserFunc, expectedCode);

            // Enqueue response
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        protected <DT> DT readDataWithPath(Class<? extends DT> clazz, String filename) {
            return readData(clazz, mDataPath + filename);
        }

    }

    // --- Create download share tests ---

    @SuppressWarnings("unused")
    private abstract class BaseCreateDownloadShareTests extends BaseSharesTests<DownloadShare> {

        @Mock
        protected CryptoWrapper mCryptoWrapper;
        @Mock
        protected FileKeyFetcher mFileKeyFetcher;

        @Mock
        protected DracoonServerSettingsImpl mDracoonServerSettingsImpl;

        protected CreateDownloadShareRequest mCreateDownloadShareRequest;

        protected BaseCreateDownloadShareTests(String dataPath) {
            super(DownloadShare.class, dataPath);
        }

        @BeforeEach
        protected void setup() {
            mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);
            mDracoonClientImpl.setFileKeyFetcher(mFileKeyFetcher);

            mDracoonClientImpl.setServerSettingsImpl(mDracoonServerSettingsImpl);

            mCreateDownloadShareRequest = readDataWithPath(CreateDownloadShareRequest.class,
                    "create_dl_share_request.json");
        }

        @Test
        protected void testRequestsValid() throws Exception {
            executeMocked(() -> executeTestRequestsValid("create_dl_share_request.json",
                    "create_dl_share_response.json",
                    () -> mDsi.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testDependencyCallsValid() throws Exception {
            executeMockedAndVerified(() -> executeTest("create_dl_share_response.json",
                    () -> mDsi.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testDataCorrect() throws Exception {
            executeMocked(() -> executeTestDataCorrect("create_dl_share_response.json",
                    "dl_share.json",
                    () -> mDsi.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testError() throws Exception {
            executeMocked(() -> executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseDownloadShareCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mDsi.createDownloadShare(mCreateDownloadShareRequest)));
        }

        protected abstract void executeMocked(Executable e) throws Exception;

        protected abstract void executeMockedAndVerified(Executable e) throws Exception;

    }

    @Nested
    class CreateDownloadShareStandardTests extends BaseCreateDownloadShareTests {

        CreateDownloadShareStandardTests() {
            super("/shares/create_download_share_standard/");
        }

        @Override
        protected void executeMocked(Executable e) throws Exception {
            mockGetPlainFileKey();
            e.execute();
        }

        @Override
        protected void executeMockedAndVerified(Executable e) throws Exception {
            mockGetPlainFileKey();
            e.execute();
            verifyGetPlainFileKey();
        }

        private void mockGetPlainFileKey() throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong())).thenReturn(null);
        }

        private void verifyGetPlainFileKey() throws Exception {
            verify(mFileKeyFetcher).getPlainFileKey(mCreateDownloadShareRequest.getNodeId());
        }

    }

    @Nested
    class CreateDownloadShareEncryptedTests extends BaseCreateDownloadShareTests {

        CreateDownloadShareEncryptedTests() {
            super("/shares/create_download_share_encrypted/");
        }

        @Override
        protected void executeMocked(Executable e) throws Exception {
            mockGetPlainFileKey(readPlainFileKeyData());
            mockGetUserKeyPairVersion();
            mockGenerateUserKeyPair(readUserKeyPairData());
            mockEncryptFileKey(readEncryptedFileKeyData());
            e.execute();
        }

        @Override
        protected void executeMockedAndVerified(Executable e) throws Exception {
            PlainFileKey plainFileKey = readPlainFileKeyData();
            UserKeyPair userKeyPair = readUserKeyPairData();
            EncryptedFileKey encFileKey = readEncryptedFileKeyData();

            mockGetPlainFileKey(plainFileKey);
            mockGetUserKeyPairVersion();
            mockGenerateUserKeyPair(userKeyPair);
            mockEncryptFileKey(encFileKey);

            e.execute();

            verifyGetPlainFileKey(mCreateDownloadShareRequest.getNodeId());
            verifyGetUserKeyPairVersion();
            verifyGenerateUserKeyPair(mCreateDownloadShareRequest.getEncryptionPassword());
            verifyEncryptFileKey(mCreateDownloadShareRequest.getNodeId(), plainFileKey,
                    userKeyPair.getUserPublicKey());
        }

        private void mockGetPlainFileKey(PlainFileKey plainFileKey) throws Exception {
            when(mFileKeyFetcher.getPlainFileKey(anyLong()))
                    .thenReturn(plainFileKey);
        }

        private void mockGetUserKeyPairVersion() throws Exception {
            when(mDracoonServerSettingsImpl.getPreferredUserKeyPairVersion())
                    .thenReturn(UserKeyPair.Version.RSA4096);
        }

        private void mockGenerateUserKeyPair(UserKeyPair userKeyPair) throws Exception {
            when(mCryptoWrapper.generateUserKeyPair(any(), any()))
                    .thenReturn(userKeyPair);
        }

        private void mockEncryptFileKey(EncryptedFileKey encFileKey) throws Exception {
            when(mCryptoWrapper.encryptFileKey(anyLong(), any(), any()))
                    .thenReturn(encFileKey);
        }

        private void verifyGetPlainFileKey(long nodeId) throws Exception {
            verify(mFileKeyFetcher).getPlainFileKey(nodeId);
        }

        private void verifyGetUserKeyPairVersion() throws Exception {
            verify(mDracoonServerSettingsImpl).getPreferredUserKeyPairVersion();
        }

        private void verifyGenerateUserKeyPair(String password) throws Exception {
            verify(mCryptoWrapper).generateUserKeyPair(UserKeyPair.Version.RSA4096, password);
        }

        private void verifyEncryptFileKey(long nodeId, PlainFileKey plainFileKey,
                UserPublicKey userPublicKey) throws Exception {
            verify(mCryptoWrapper).encryptFileKey(nodeId, plainFileKey, userPublicKey);
        }

        private PlainFileKey readPlainFileKeyData() {
            return readDataWithPath(PlainFileKey.class, "plain_file_key.json");
        }

        private UserKeyPair readUserKeyPairData() {
            return readDataWithPath(UserKeyPair.class, "user_key_pair.json");
        }

        private EncryptedFileKey readEncryptedFileKeyData() {
            return readDataWithPath(EncryptedFileKey.class, "enc_file_key.json");
        }

    }

    // --- Create upload share tests ---

    @Nested
    class CreateUploadShareTests extends BaseSharesTests<UploadShare> {

        private CreateUploadShareRequest mCreateUploadShareRequest;

        CreateUploadShareTests() {
            super(UploadShare.class, "/shares/create_upload_share/");
        }

        @BeforeEach
        void setup() {
            mCreateUploadShareRequest = readDataWithPath(CreateUploadShareRequest.class,
                    "create_ul_share_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("create_ul_share_request.json", "create_ul_share_response.json",
                    () -> mDsi.createUploadShare(mCreateUploadShareRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_ul_share_response.json", "ul_share.json",
                    () -> mDsi.createUploadShare(mCreateUploadShareRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseUploadShareCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mDsi.createUploadShare(mCreateUploadShareRequest));
        }

    }

}
