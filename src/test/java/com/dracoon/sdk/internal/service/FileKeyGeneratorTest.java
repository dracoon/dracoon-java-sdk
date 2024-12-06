package com.dracoon.sdk.internal.service;

import java.util.Arrays;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FileKeyGeneratorTest extends BaseServiceTest {

    private static abstract class MultiAnswer<T> implements Answer<T> {

        private int mCount = 0;

        public T answer(InvocationOnMock invocation) {
            return getItem(mCount++);
        }

        protected abstract T getItem(int index);

    }

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    @Mock
    protected AccountService mDracoonAccountImpl;

    private FileKeyGenerator mFkg;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);

        mDracoonClientImpl.setUserService(mDracoonAccountImpl);

        mFkg = new FileKeyGenerator(mDracoonClientImpl);
    }

    @SuppressWarnings("unused")
    private abstract class BaseGenerateMissingFileKeysTests {

        protected final char[] CRYPTO_PW = {'t','e','s','t'};

        protected final String mDataPath;

        protected BaseGenerateMissingFileKeysTests(String dataPath) {
            mDataPath = dataPath;
        }

        @BeforeEach
        protected void setup() {
            mDracoonClientImpl.setEncryptionPassword(CRYPTO_PW);
        }

        // --- Tests: No missing keys ---

        @Test
        void testGetRequestValidNoMissingKeys() throws Exception {
            // Execute test
            executeTestNoMissingKeys();

            // Assert request is valid
            checkRequests("get_missing_keys_no_missing_request.json");
        }

        @Test
        void testResultCorrectNoMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTestNoMissingKeys();

            // Assert result is correct (no remaining keys)
            assertTrue(expectedResult);
        }

        private boolean executeTestNoMissingKeys() throws Exception {
            enqueueResponses("get_missing_keys_no_missing_response.json");

            return executeMockedNoMissingKeys();
        }

        protected abstract boolean executeMockedNoMissingKeys() throws Exception;

        // --- Tests: One Batch, limit > total ---

        @Test
        void testGetRequestValid1BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimGreaterTot();

            // Assert request is valid
            checkRequests("get_missing_keys_1b_request.json");
        }

        @Test
        void testSetRequestValid1BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimGreaterTot();

            // Assert request is valid
            dropRequest();
            checkRequests("set_keys_1b_lgt_request.json");
        }

        @Test
        void testResultCorrect1BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest1BatchLimGreaterTot();

            // Assert result is correct (no remaining keys)
            assertTrue(expectedResult);
        }

        private boolean executeTest1BatchLimGreaterTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_1b_lgt_response.json",
                    "set_keys_response.json");

            return executeMocked1BatchLimGreaterTot();
        }

        protected abstract boolean executeMocked1BatchLimGreaterTot() throws Exception;

        // --- Tests: One Batch, limit == total ---

        @Test
        void testGetRequestsValid1BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimEqualTot();

            // Assert request is valid
            checkRequests("get_missing_keys_1b_request.json");
        }

        @Test
        void testSetRequestsValid1BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimEqualTot();

            // Assert request is valid
            dropRequest();
            checkRequests("set_keys_1b_let_request.json");
        }

        @Test
        void testResultCorrect1BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest1BatchLimEqualTot();

            // Assert result is correct (no remaining keys)
            assertTrue(expectedResult);
        }

        private boolean executeTest1BatchLimEqualTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_1b_let_response.json",
                    "set_keys_response.json");

            return executeMocked1BatchLimEqualTot();
        }

        protected abstract boolean executeMocked1BatchLimEqualTot() throws Exception;

        // --- Tests: One Batch, limit < total ---

        @Test
        void testGetRequestsValid1BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimLowerTot();

            // Assert request is valid
            checkRequests("get_missing_keys_1b_request.json");
        }

        @Test
        void testSetRequestsValid1BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            executeTest1BatchLimLowerTot();

            // Assert request is valid
            dropRequest();
            checkRequests("set_keys_1b_llt_request.json");
        }

        @Test
        void testResultCorrect1BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest1BatchLimLowerTot();

            // Assert result is correct (still remaining keys)
            assertFalse(expectedResult);
        }

        private boolean executeTest1BatchLimLowerTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_1b_llt_response.json",
                    "set_keys_response.json");

            return executeMocked1BatchLimLowerTot();
        }

        protected abstract boolean executeMocked1BatchLimLowerTot() throws Exception;

        // --- Tests: Two Batches, limit > total ---

        @Test
        void testGetRequestValid2BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimGreaterTot();

            // Assert requests are valid
            checkRequests("get_missing_keys_2b1_request.json");
            dropRequest();
            checkRequests("get_missing_keys_2b2_request.json");
        }

        @Test
        void testSetRequestValid2BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimGreaterTot();

            // Assert requests are valid
            dropRequest();
            checkRequests("set_keys_2b1_request.json");
            dropRequest();
            checkRequests("set_keys_2b2_lgt_request.json");
        }

        @Test
        void testResultCorrect2BatchLimGreaterTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest2BatchLimGreaterTot();

            // Assert result is correct (no remaining keys)
            assertTrue(expectedResult);
        }

        private boolean executeTest2BatchLimGreaterTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_2b1_lgt_response.json",
                    "set_keys_response.json",
                    "get_missing_keys_2b2_lgt_response.json",
                    "set_keys_response.json");

            return executeMocked2BatchLimGreaterTot();
        }

        protected abstract boolean executeMocked2BatchLimGreaterTot() throws Exception;

        // --- Tests: Two Batches, limit == total ---

        @Test
        void testGetRequestsValid2BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimEqualTot();

            // Assert requests are valid
            checkRequests("get_missing_keys_2b1_request.json");
            dropRequest();
            checkRequests("get_missing_keys_2b2_request.json");
        }

        @Test
        void testSetRequestsValid2BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimEqualTot();

            // Assert requests are valid
            dropRequest();
            checkRequests("set_keys_2b1_request.json");
            dropRequest();
            checkRequests("set_keys_2b2_let_request.json");
        }

        @Test
        void testResultCorrect2BatchLimEqualTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest2BatchLimEqualTot();

            // Assert result is correct (no remaining keys)
            assertTrue(expectedResult);
        }

        private boolean executeTest2BatchLimEqualTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_2b1_let_response.json",
                    "set_keys_response.json",
                    "get_missing_keys_2b2_let_response.json",
                    "set_keys_response.json");

            return executeMocked2BatchLimEqualTot();
        }

        protected abstract boolean executeMocked2BatchLimEqualTot() throws Exception;

        // --- Tests: Two Batches, limit < total ---

        @Test
        void testGetRequestsValid2BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimLowerTot();

            // Assert requests are valid
            checkRequests("get_missing_keys_2b1_request.json");
            dropRequest();
            checkRequests("get_missing_keys_2b2_request.json");
        }

        @Test
        void testSetRequestsValid2BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            executeTest2BatchLimLowerTot();

            // Assert requests are valid
            dropRequest();
            checkRequests("set_keys_2b1_request.json");
            dropRequest();
            checkRequests("set_keys_2b2_llt_request.json");
        }

        @Test
        void testResultCorrect2BatchLimLowerTotMissingKeys() throws Exception {
            // Execute test
            boolean expectedResult = executeTest2BatchLimLowerTot();

            // Assert result is correct (still remaining keys)
            assertFalse(expectedResult);
        }

        private boolean executeTest2BatchLimLowerTot() throws Exception {
            enqueueResponses(
                    "get_missing_keys_2b1_llt_response.json",
                    "set_keys_response.json",
                    "get_missing_keys_2b2_llt_response.json",
                    "set_keys_response.json");

            return executeMocked2BatchLimLowerTot();
        }

        protected abstract boolean executeMocked2BatchLimLowerTot() throws Exception;

        // --- Error tests ---

        @Test
        void testApiGetError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_FILE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseMissingFileKeysQueryError, expectedCode);

            // Enqueue response
            enqueueResponses("file_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> executeMocked(1, new int[]{}, new int[]{}));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testApiSetError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_USER_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseFileKeysSetError, expectedCode);

            // Enqueue response
            enqueueResponses("get_missing_keys_1b_lgt_response.json");
            enqueueResponses("user_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> executeMocked(4, new int[]{1}, new int[]{1,2}));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        // --- Helper methods ---

        protected void enqueueResponses(String... responseFilenames) {
            for (String responseFilename : responseFilenames) {
                enqueueResponse(mDataPath + responseFilename);
            }
        }

        protected void checkRequests(String... requestFilenames) throws Exception {
            for (String requestFilename : requestFilenames) {
                checkRequest(mDataPath + requestFilename);
            }
        }

        protected boolean executeMocked(int limit, int[] pfkDataNums, int[] efkDataNums)
                throws Exception {
            mockGetUserKeyPairsCall();
            mockCryptoCalls(pfkDataNums, efkDataNums);
            return executeGenerateMissingFileKeys(limit);
        }

        protected abstract boolean executeGenerateMissingFileKeys(int limit) throws Exception;

        protected void mockGetUserKeyPairsCall() throws Exception {
            UserKeyPair[] userKeyPairs = readData(UserKeyPair[].class,
                    mDataPath + "user_key_pairs.json");
            when(mDracoonAccountImpl.getAndCheckUserKeyPairs())
                    .thenReturn(Arrays.asList(userKeyPairs));
        }

        protected void mockCryptoCalls(int[] pfkDataNums, int[] efkDataNums) throws Exception {
            if (pfkDataNums.length > 0) {
                when(mCryptoWrapper.decryptFileKey(any(), any(), any(), any()))
                        .thenAnswer(new MultiAnswer<PlainFileKey>() {
                            @Override
                            protected PlainFileKey getItem(int index) {
                                return readPlainFileKeyData(pfkDataNums[index]);
                            }
                        });
            }
            if (efkDataNums.length > 0) {
                when(mCryptoWrapper.encryptFileKey(any(), any(), any()))
                        .thenAnswer(new MultiAnswer<EncryptedFileKey>() {
                            @Override
                            protected EncryptedFileKey getItem(int index) {
                                return readEncFileKeyData(efkDataNums[index]);
                            }
                        });
            }
        }

        private PlainFileKey readPlainFileKeyData(int pfkDataNum) {
            return readData(PlainFileKey.class, String.format("%splain_file_key_%d.json",
                    mDataPath, pfkDataNum));
        }

        private EncryptedFileKey readEncFileKeyData(int efkDataNum) {
            return readData(EncryptedFileKey.class, String.format("%senc_file_key_%d.json",
                    mDataPath, efkDataNum));
        }

    }

    @Nested
    class GenerateMissingFileKeysAllFilesTests extends BaseGenerateMissingFileKeysTests {

        GenerateMissingFileKeysAllFilesTests() {
            super("/file_keys/generate_missing_file_keys/all_files/");
        }

        // Execute test with:
        // - no crypto mocking
        @Override
        protected boolean executeMockedNoMissingKeys() throws Exception {
            return executeMocked(4, new int[]{}, new int[]{});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2
        @Override
        protected boolean executeMocked1BatchLimGreaterTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2,efk3,efk4
        @Override
        protected boolean executeMocked1BatchLimEqualTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2, 3, 4});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2,efk3,efk4
        @Override
        protected boolean executeMocked1BatchLimLowerTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2, 3, 4});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk2,pfk3,pfk4
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11
        @Override
        protected boolean executeMocked2BatchLimGreaterTot() throws Exception {
            return executeMocked(12, new int[]{1, 2, 3, 4},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk2,pfk3,pfk4
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11,efk12
        @Override
        protected boolean executeMocked2BatchLimEqualTot() throws Exception {
            return executeMocked(12, new int[]{1, 2, 3, 4},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk2,pfk3,pfk4
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11,efk12
        @Override
        protected boolean executeMocked2BatchLimLowerTot() throws Exception {
            return executeMocked(12, new int[]{1, 2, 3, 4},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        }

        @Override
        protected boolean executeGenerateMissingFileKeys(int limit) throws Exception {
            return mFkg.generateMissingFileKeys(null, limit);
        }

    }

    @Nested
    class GenerateMissingFileKeysOneFileTests extends BaseGenerateMissingFileKeysTests {

        GenerateMissingFileKeysOneFileTests() {
            super("/file_keys/generate_missing_file_keys/one_file/");
        }

        // Execute test with:
        // - no crypto mocking
        @Override
        protected boolean executeMockedNoMissingKeys() throws Exception {
            return executeMocked(4, new int[]{}, new int[]{});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2
        @Override
        protected boolean executeMocked1BatchLimGreaterTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2,efk3,efk4
        @Override
        protected boolean executeMocked1BatchLimEqualTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2, 3, 4});
        }

        // Execute test with:
        // - decrypt=pfk1
        // - encrypt=efk1,efk2,efk3,efk4
        @Override
        protected boolean executeMocked1BatchLimLowerTot() throws Exception {
            return executeMocked(4, new int[]{1}, new int[]{1, 2, 3, 4});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk1
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11
        @Override
        protected boolean executeMocked2BatchLimGreaterTot() throws Exception {
            return executeMocked(12, new int[]{1, 1},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk1
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11,efk12
        @Override
        protected boolean executeMocked2BatchLimEqualTot() throws Exception {
            return executeMocked(12, new int[]{1, 1},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        }

        // Execute test with:
        // - decrypt=pfk1,pfk1
        // - encrypt=efk1,efk2,efk3,efk4,efk5,efk6,efk7,efk8,efk9,efk10,efk11,efk12
        @Override
        protected boolean executeMocked2BatchLimLowerTot() throws Exception {
            return executeMocked(12, new int[]{1, 1},
                    new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        }

        @Override
        protected boolean executeGenerateMissingFileKeys(int limit) throws Exception {
            return mFkg.generateMissingFileKeys(1L, limit);
        }

    }

}
