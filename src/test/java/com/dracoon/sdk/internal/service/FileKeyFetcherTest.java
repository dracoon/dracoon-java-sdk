package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileKeyFetcherTest extends BaseServiceTest {

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    @Mock
    protected AccountService mAccountService;
    @Mock
    protected NodesService mNodesService;

    private FileKeyFetcher mFkf;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);

        mServiceLocator.setAccountService(mAccountService);
        mServiceLocator.setNodesService(mNodesService);

        mFkf = new FileKeyFetcher(mDracoonClientImpl);
    }

    @Nested
    class GetFileKeyTests {

        private final char[] CRYPTO_PW = {'t','e','s','t'};

        private final String DATA_PATH = "/file_keys/get_file_key/";

        private final long NODE_ID = 4L;

        @BeforeEach
        void setup() {
            mDracoonClientImpl.setEncryptionPassword(CRYPTO_PW);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue response
            enqueueOkResponse();

            // Execute method to test
            executeMocked(true);

            // Assert request are valid
            checkRequest(DATA_PATH + "get_file_key_request.json");
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            // Enqueue response
            enqueueOkResponse();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testNoDataCorrect() throws Exception {
            // Execute method to test
            PlainFileKey fileKey = executeMockedWithNoReturn();

            // Assert data is correct
            assertNull(fileKey);
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue response
            enqueueOkResponse();

            // Execute method to test
            PlainFileKey expectedFileKey = readPlainFileKeyData();
            PlainFileKey fileKey = executeMockedWithReturn(expectedFileKey);

            // Assert data is correct
            assertDeepEquals(expectedFileKey, fileKey);
        }

        @Test
        void testApiErrorFileKeyNotFound() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseFileKeyQueryError, expectedCode);

            // Enqueue response
            enqueueNotFoundErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> executeMocked(false));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testApiErrorUnknownVersion() {
            // Enqueue response
            enqueueUnknownVersionErrorResponse();

            // Execute method to test
            DracoonCryptoCode expectedCode = DracoonCryptoCode.UNKNOWN_ALGORITHM_VERSION_ERROR;
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    () -> executeMocked(false));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testDependencyError() {
            // Enqueue response
            enqueueOkResponse();

            // Execute method to test
            DracoonCryptoCode expectedCode = DracoonCryptoCode.INVALID_KEY_ERROR;
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    () -> executeMockedWithException(expectedCode));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_file_key_response.json");
        }

        private void enqueueNotFoundErrorResponse() {
            enqueueResponse(DATA_PATH + "file_key_not_found_response.json");
        }

        private void enqueueUnknownVersionErrorResponse() {
            enqueueResponse(DATA_PATH + "file_key_unknown_version_response.json");
        }

        private void executeMocked(boolean mockDecrypt) throws Exception {
            mockIsNodeEncryptedCall(true);

            if (mockDecrypt) {
                mockGetUserKeyPairCall(readUserKeyPairData());
                when(mCryptoWrapper.decryptFileKey(any(), any(), any(), any()))
                        .thenReturn(readPlainFileKeyData());
            }

            mFkf.getPlainFileKey(NODE_ID);
        }

        private PlainFileKey executeMockedWithNoReturn() throws Exception {
            mockIsNodeEncryptedCall(false);
            return mFkf.getPlainFileKey(NODE_ID);
        }

        private PlainFileKey executeMockedWithReturn(PlainFileKey expectedPlainFileKey)
                throws Exception {
            mockIsNodeEncryptedCall(true);
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.decryptFileKey(any(), any(), any(), any()))
                    .thenReturn(expectedPlainFileKey);

            return mFkf.getPlainFileKey(NODE_ID);
        }

        private void executeMockedAndVerified() throws Exception {
            mockIsNodeEncryptedCall(true);

            UserKeyPair userKeyPair = readUserKeyPairData();
            mockGetUserKeyPairCall(userKeyPair);

            PlainFileKey plainFileKey = readPlainFileKeyData();
            when(mCryptoWrapper.decryptFileKey(any(), any(), any(), any()))
                    .thenReturn(plainFileKey);

            mFkf.getPlainFileKey(NODE_ID);

            verify(mCryptoWrapper).decryptFileKey(
                    eq(NODE_ID),
                    argThat(arg -> deepEquals(arg, readEncFileKeyData())),
                    eq(userKeyPair.getUserPrivateKey()),
                    eq(CRYPTO_PW));

            verifyIsNodeEncryptedCall(NODE_ID);
            verifyGetUserKeyPairCall();
        }

        private void executeMockedWithException(DracoonCryptoCode expectedCode) throws Exception {
            mockIsNodeEncryptedCall(true);
            mockGetUserKeyPairCall(readUserKeyPairData());

            when(mCryptoWrapper.decryptFileKey(any(), any(), any(), any()))
                    .thenThrow(new DracoonCryptoException(expectedCode));

            mFkf.getPlainFileKey(NODE_ID);
        }

        private void mockIsNodeEncryptedCall(boolean isEncrypted) throws Exception {
            when(mNodesService.isNodeEncrypted(anyLong())).thenReturn(isEncrypted);
        }

        private void verifyIsNodeEncryptedCall(long nodeId) throws Exception {
            verify(mNodesService).isNodeEncrypted(nodeId);
        }

        private void mockGetUserKeyPairCall(UserKeyPair userKeyPair) throws Exception {
            when(mAccountService.getAndCheckUserKeyPair(any())).thenReturn(userKeyPair);
        }

        private void verifyGetUserKeyPairCall() throws Exception {
            verify(mAccountService).getAndCheckUserKeyPair(UserKeyPair.Version.RSA4096);
        }

        private UserKeyPair readUserKeyPairData() {
            return readData(UserKeyPair.class, DATA_PATH + "user_key_pair_4096.json");
        }

        private PlainFileKey readPlainFileKeyData() {
            return readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
        }

        private EncryptedFileKey readEncFileKeyData() {
            return readData(EncryptedFileKey.class, DATA_PATH + "enc_file_key.json");
        }

    }

}
