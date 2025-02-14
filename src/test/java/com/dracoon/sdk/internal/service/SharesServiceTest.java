package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.filter.GetDownloadSharesFilter;
import com.dracoon.sdk.filter.GetUploadSharesFilter;
import com.dracoon.sdk.filter.NodeIdFilter;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.DownloadShareList;
import com.dracoon.sdk.model.UploadShare;
import com.dracoon.sdk.model.UploadShareList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SharesServiceTest extends BaseServiceTest {

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    private SharesService mSrv;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        setCryptoWrapper(mCryptoWrapper);

        mSrv = new SharesService(mDracoonClientImpl);
    }

    private interface SharesTest<T> {
        T execute() throws Exception;
    }

    private abstract class BaseSharesTests<T> {

        protected final Class<T> mDataClass;
        protected final String mDataPath;

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

    private abstract class BaseShareQrCodeTests extends BaseSharesTests<byte[]> {

        protected BaseShareQrCodeTests(String dataPath) {
            super(byte[].class, dataPath);
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename,
                SharesTest<byte[]> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            byte[] data = test.execute();

            // Assert data is correct
            byte[] expectedData = readFile(mDataPath + dataFilename);
            assertArrayEquals(expectedData, data);
        }

    }

    // --- Create download share tests ---

    @SuppressWarnings("unused")
    private abstract class BaseCreateDownloadShareTests extends BaseSharesTests<DownloadShare> {

        @Mock
        protected ServerSettingsService mServerSettingsService;
        @Mock
        protected FileKeyFetcher mFileKeyFetcher;

        protected CreateDownloadShareRequest mCreateDownloadShareRequest;

        protected BaseCreateDownloadShareTests(String dataPath) {
            super(DownloadShare.class, dataPath);
        }

        @BeforeEach
        protected void setup() {
            mServiceLocator.setServerSettingsService(mServerSettingsService);
            mServiceLocator.setFileKeyFetcher(mFileKeyFetcher);

            mCreateDownloadShareRequest = readDataWithPath(CreateDownloadShareRequest.class,
                    "create_dl_share_request.json");
        }

        @Test
        protected void testRequestsValid() throws Exception {
            executeMocked(() -> executeTestRequestsValid("create_dl_share_request.json",
                    "create_dl_share_response.json",
                    () -> mSrv.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testDependencyCallsValid() throws Exception {
            executeMockedAndVerified(() -> executeTest("create_dl_share_response.json",
                    () -> mSrv.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testDataCorrect() throws Exception {
            executeMocked(() -> executeTestDataCorrect("create_dl_share_response.json",
                    "dl_share.json",
                    () -> mSrv.createDownloadShare(mCreateDownloadShareRequest)));
        }

        @Test
        protected void testError() throws Exception {
            executeMocked(() -> executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseDownloadShareCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mSrv.createDownloadShare(mCreateDownloadShareRequest)));
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
            when(mServerSettingsService.getPreferredUserKeyPairVersion())
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
            verify(mServerSettingsService).getPreferredUserKeyPairVersion();
        }

        private void verifyGenerateUserKeyPair(char[] password) throws Exception {
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

    // --- Get download shares tests ---

    @SuppressWarnings("unused")
    private abstract class BaseGetDownloadSharesTests extends BaseSharesTests<DownloadShareList> {

        protected BaseGetDownloadSharesTests() {
            super(DownloadShareList.class, "/shares/get_download_shares/");
        }

        protected void executeTestError(String responseFilename, SharesTest<DownloadShareList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseDownloadSharesQueryError,
                    DracoonApiCode.PRECONDITION_UNKNOWN_ERROR, test);
        }

        protected abstract DownloadShareList getDownloadShares() throws Exception;

    }

    @Nested
    class GetDownloadSharesTests extends BaseGetDownloadSharesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_dl_shares_request.json", "get_dl_shares_response.json",
                    this::getDownloadShares);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_shares_empty_response.json", "dl_shares_empty.json",
                    this::getDownloadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_shares_response.json", "dl_shares.json",
                    this::getDownloadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getDownloadShares);
        }

        @Override
        protected DownloadShareList getDownloadShares() throws Exception {
            return mSrv.getDownloadShares();
        }

    }

    @Nested
    class GetDownloadSharesWithFiltersTests extends BaseGetDownloadSharesTests {

        private final GetDownloadSharesFilter mFilters;

        GetDownloadSharesWithFiltersTests() {
            mFilters = new GetDownloadSharesFilter();
            mFilters.addNodeIdFilter(new NodeIdFilter.Builder().eq(3L).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_dl_shares_with_filter_request.json",
                    "get_dl_shares_with_filter_response.json",
                    this::getDownloadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_shares_with_filter_response.json",
                    "dl_shares_filtered.json",
                    this::getDownloadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getDownloadShares);
        }

        @Override
        protected DownloadShareList getDownloadShares() throws Exception {
            return mSrv.getDownloadShares(mFilters);
        }

    }

    @Nested
    class GetDownloadSharesPagedTests extends BaseGetDownloadSharesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_dl_shares_paged_request.json",
                    "get_dl_shares_paged_response.json",
                    this::getDownloadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_shares_paged_response.json", "dl_shares_paged.json",
                    this::getDownloadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getDownloadShares);
        }

        @Override
        protected DownloadShareList getDownloadShares() throws Exception {
            return mSrv.getDownloadShares(1L, 2L);
        }

    }

    @Nested
    class GetDownloadSharesPagedWithFiltersTests extends BaseGetDownloadSharesTests {

        private final GetDownloadSharesFilter mFilters;

        GetDownloadSharesPagedWithFiltersTests() {
            super();
            mFilters = new GetDownloadSharesFilter();
            mFilters.addNodeIdFilter(new NodeIdFilter.Builder().eq(3L).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_dl_shares_paged_with_filter_request.json",
                    "get_dl_shares_paged_with_filter_response.json",
                    this::getDownloadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_shares_paged_with_filter_response.json",
                    "dl_shares_paged_filtered.json",
                    this::getDownloadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getDownloadShares);
        }

        @Override
        protected DownloadShareList getDownloadShares() throws Exception {
            return mSrv.getDownloadShares(mFilters, 1L, 2L);
        }

    }

    // --- Get download share QR code tests ---

    @Nested
    class GetDownloadShareQrCodeTests extends BaseShareQrCodeTests {

        GetDownloadShareQrCodeTests() {
            super("/shares/get_download_share_qr/");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_dl_share_qr_request.json", "get_dl_share_qr_response.json",
                    this::getDownloadShareQrCode);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_dl_share_qr_response.json", "dl_share_qr.png",
                    this::getDownloadShareQrCode);
        }

        @Test
        void testError() {
            executeTestError("share_not_found_response.json",
                    mDracoonErrorParser::parseDownloadSharesQueryError,
                    DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND,
                    this::getDownloadShareQrCode);
        }

        private byte[] getDownloadShareQrCode() throws Exception {
            return mSrv.getDownloadShareQrCode(1L);
        }

    }

    // --- Delete download share tests ---

    @Nested
    class DeleteDownloadShareTests extends BaseSharesTests<Void> {

        DeleteDownloadShareTests() {
            super(Void.class, "/shares/delete_download_share/");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("delete_dl_share_request.json", "delete_dl_share_response.json",
                    this::executeDeleteDownloadShare);
        }

        @Test
        void testError() {
            executeTestError("share_not_found_response.json",
                    mDracoonErrorParser::parseDownloadShareDeleteError,
                    DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND,
                    this::executeDeleteDownloadShare);
        }

        private Void executeDeleteDownloadShare() throws Exception {
            mSrv.deleteDownloadShare(2L);
            return null;
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
                    () -> mSrv.createUploadShare(mCreateUploadShareRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_ul_share_response.json", "ul_share.json",
                    () -> mSrv.createUploadShare(mCreateUploadShareRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseUploadShareCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mSrv.createUploadShare(mCreateUploadShareRequest));
        }

    }

    // --- Get upload shares tests ---

    @SuppressWarnings("unused")
    private abstract class BaseGetUploadSharesTests extends BaseSharesTests<UploadShareList> {

        protected BaseGetUploadSharesTests() {
            super(UploadShareList.class, "/shares/get_upload_shares/");
        }

        protected void executeTestError(String responseFilename, SharesTest<UploadShareList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseUploadSharesQueryError,
                    DracoonApiCode.PRECONDITION_UNKNOWN_ERROR, test);
        }

        protected abstract UploadShareList getUploadShares() throws Exception;

    }

    @Nested
    class GetUploadSharesTests extends BaseGetUploadSharesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_ul_shares_request.json", "get_ul_shares_response.json",
                    this::getUploadShares);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_shares_empty_response.json", "ul_shares_empty.json",
                    this::getUploadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_shares_response.json", "ul_shares.json",
                    this::getUploadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getUploadShares);
        }

        @Override
        protected UploadShareList getUploadShares() throws Exception {
            return mSrv.getUploadShares();
        }

    }

    @Nested
    class GetUploadSharesWithFiltersTests extends BaseGetUploadSharesTests {

        private final GetUploadSharesFilter mFilters;

        GetUploadSharesWithFiltersTests() {
            mFilters = new GetUploadSharesFilter();
            mFilters.addNodeIdFilter(new NodeIdFilter.Builder().eq(3L).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_ul_shares_with_filter_request.json",
                    "get_ul_shares_with_filter_response.json",
                    this::getUploadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_shares_with_filter_response.json",
                    "ul_shares_filtered.json",
                    this::getUploadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getUploadShares);
        }

        @Override
        protected UploadShareList getUploadShares() throws Exception {
            return mSrv.getUploadShares(mFilters);
        }

    }

    @Nested
    class GetUploadSharesPagedTests extends BaseGetUploadSharesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_ul_shares_paged_request.json",
                    "get_ul_shares_paged_response.json",
                    this::getUploadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_shares_paged_response.json", "ul_shares_paged.json",
                    this::getUploadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getUploadShares);
        }

        @Override
        protected UploadShareList getUploadShares() throws Exception {
            return mSrv.getUploadShares(1L, 2L);
        }

    }

    @Nested
    class GetUploadSharesPagedWithFiltersTests extends BaseGetUploadSharesTests {

        private final GetUploadSharesFilter mFilters;

        GetUploadSharesPagedWithFiltersTests() {
            super();
            mFilters = new GetUploadSharesFilter();
            mFilters.addNodeIdFilter(new NodeIdFilter.Builder().eq(3L).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_ul_shares_paged_with_filter_request.json",
                    "get_ul_shares_paged_with_filter_response.json",
                    this::getUploadShares);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_shares_paged_with_filter_response.json",
                    "ul_shares_paged_filtered.json",
                    this::getUploadShares);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getUploadShares);
        }

        @Override
        protected UploadShareList getUploadShares() throws Exception {
            return mSrv.getUploadShares(mFilters, 1L, 2L);
        }

    }

    // --- Get upload share QR code tests ---

    @Nested
    class GetUploadShareQrCodeTests extends BaseShareQrCodeTests {

        GetUploadShareQrCodeTests() {
            super("/shares/get_upload_share_qr/");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_ul_share_qr_request.json", "get_ul_share_qr_response.json",
                    this::getUploadShareQrCode);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_ul_share_qr_response.json", "ul_share_qr.png",
                    this::getUploadShareQrCode);
        }

        @Test
        void testError() {
            executeTestError("share_not_found_response.json",
                    mDracoonErrorParser::parseUploadSharesQueryError,
                    DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND,
                    this::getUploadShareQrCode);
        }

        private byte[] getUploadShareQrCode() throws Exception {
            return mSrv.getUploadShareQrCode(1L);
        }

    }

    // --- Delete upload share tests ---

    @Nested
    class DeleteUploadShareTests extends BaseSharesTests<Void> {

        DeleteUploadShareTests() {
            super(Void.class, "/shares/delete_upload_share/");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("delete_ul_share_request.json", "delete_ul_share_response.json",
                    this::executeDeleteUploadShare);
        }

        @Test
        void testError() {
            executeTestError("share_not_found_response.json",
                    mDracoonErrorParser::parseUploadShareDeleteError,
                    DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND,
                    this::executeDeleteUploadShare);
        }

        private Void executeDeleteUploadShare() throws Exception {
            mSrv.deleteUploadShare(2L);
            return null;
        }

    }

}
