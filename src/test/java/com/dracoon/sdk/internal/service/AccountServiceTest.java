package com.dracoon.sdk.internal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class AccountServiceTest extends BaseServiceTest {

    @Mock
    protected CryptoWrapper mCryptoWrapper;

    private AccountService mSrv;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setCryptoWrapper(mCryptoWrapper);

        mSrv = new AccountService(mDracoonClientImpl);
    }

    // --- Ping tests ---

    @Nested
    class PingTests {

        private final String DATA_PATH = "/account/ping/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "ping_response.json");

            // Execute method to test
            mSrv.pingUser();

            // Assert requests are valid
            checkRequest(DATA_PATH + "ping_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseStandardError(expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mSrv::pingUser);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Get user account tests ---

    @Nested
    class GetUserAccountTests {

        private final String DATA_PATH = "/account/user_account/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            mSrv.getUserAccount();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_user_account_request.json");
            checkRequest(DATA_PATH + "get_user_avatar_info_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            UserAccount userAccount = mSrv.getUserAccount();

            // Assert data is correct
            UserAccount expectedUserAccount = readData(UserAccount.class, DATA_PATH +
                    "user_account.json");
            assertDeepEquals(expectedUserAccount, userAccount);
        }

        @Test
        void testGetUserAccountError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseStandardError(expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mSrv::getUserAccount);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testGetUserAvatarInfoError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseStandardError(expectedCode);

            // Enqueue response
            enqueueUserAccountOkResponses();
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mSrv::getUserAccount);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        private void enqueueOkResponses() {
            enqueueUserAccountOkResponses();
            enqueueUserAvatarInfoOkResponses();
        }

        private void enqueueUserAccountOkResponses() {
            enqueueResponse(DATA_PATH + "get_user_account_response.json");
        }

        private void enqueueUserAvatarInfoOkResponses() {
            enqueueResponse(DATA_PATH + "get_user_avatar_info_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

    }

    // --- Get customer account tests ---

    @Nested
    class GetCustomerAccountTests {

        private final String DATA_PATH = "/account/customer_account/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            mSrv.getCustomerAccount();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_customer_account_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            CustomerAccount customerAccount = mSrv.getCustomerAccount();

            // Assert data is correct
            CustomerAccount expectedCustomerAccount = readData(CustomerAccount.class, DATA_PATH +
                    "customer_account.json");
            assertDeepEquals(expectedCustomerAccount, customerAccount);
        }

        @Test
        void testGetCustomerAccountError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseStandardError(expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    mSrv::getCustomerAccount);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_customer_account_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

    }

    // --- Get user key pair algorithm versions tests ---

    @Nested
    class GetUserKeyPairAlgorithmVersionsTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_key_pairs_response.json");

            // Execute method to test
            mSrv.getUserKeyPairAlgorithmVersions();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_key_pairs_request.json");
        }

        @Test
        void testNoDataCorrect() throws Exception {
            // Enqueue response
            enqueueResponse(DATA_PATH + "not_found_response.json");

            // Execute method to test
            List<UserKeyPairAlgorithm.Version> versions = mSrv.getUserKeyPairAlgorithmVersions();

            // Assert data is correct
            assertEquals(new ArrayList<UserKeyPairAlgorithm.Version>(), versions);
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_key_pairs_response.json");

            // Execute method to test
            List<UserKeyPairAlgorithm.Version> versions = mSrv.getUserKeyPairAlgorithmVersions();

            // Assert data is correct
            List<UserKeyPairAlgorithm.Version> expectedVersions = readData(List.class, DATA_PATH +
                    "user_key_pair_algo_versions.json");
            assertDeepEquals(expectedVersions, versions);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairsQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    mSrv::getUserKeyPairAlgorithmVersions);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Set user key pair tests ---

    @Nested
    class SetUserKeyPairTests {

        private final char[] CRYPTO_PW = {'t','e','s','t'};

        private final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected ServerSettingsService mServerSettingsService;

        @BeforeEach
        void setup() throws Exception {
            mServiceLocator.setServerSettingsService(mServerSettingsService);

            mockGetAvailableUserKeyPairVersions();

            mDracoonClientImpl.setEncryptionPassword(CRYPTO_PW);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMocked();

            // Assert requests are valid
            checkRequest(DATA_PATH + "set_key_pair_request.json");
        }

        @Test
        void testCryptoCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairSetError, expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, this::executeMocked);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testCryptoError() {
            assertThrows(DracoonCryptoException.class, this::executeMockedWithException);
        }

        private void executeMocked() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            when(mCryptoWrapper.generateUserKeyPair(any(), any()))
                    .thenReturn(userKeyPair);
            mSrv.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
        }

        private void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            when(mCryptoWrapper.generateUserKeyPair(any(), any()))
                    .thenReturn(userKeyPair);
            mSrv.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
            verify(mCryptoWrapper).generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
        }

        private void executeMockedWithException() throws Exception {
            when(mCryptoWrapper.generateUserKeyPair(any(), any()))
                    .thenThrow(new DracoonCryptoException());
            mSrv.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "set_key_pair_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

        private void mockGetAvailableUserKeyPairVersions()
                throws Exception {
            when(mServerSettingsService.getAvailableUserKeyPairVersions())
                    .thenReturn(Collections.singletonList(UserKeyPair.Version.RSA2048));
        }

    }

    // --- Get preferred user key pair tests ---

    @Nested
    class GetPreferredUserKeyPairTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected ServerSettingsService mServerSettingsService;

        @BeforeEach
        void setup() throws Exception {
            mServiceLocator.setServerSettingsService(mServerSettingsService);

            mockGetAvailableUserKeyPairAlgorithms();
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_key_pairs_response.json");

            // Execute method to test
            mSrv.getPreferredUserKeyPair();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_key_pairs_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_key_pairs_response.json");

            // Execute method to test
            UserKeyPair userKeyPair = mSrv.getPreferredUserKeyPair();

            // Assert data is correct
            UserKeyPair expectedUserKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_4096.json");
            assertDeepEquals(expectedUserKeyPair, userKeyPair);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairsQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.getPreferredUserKeyPair());

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testErrorUserHasNoKeyPair() {
            // Enqueue response
            enqueueResponse(DATA_PATH + "not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.getPreferredUserKeyPair());

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND, thrown.getCode());
        }

        private void mockGetAvailableUserKeyPairAlgorithms()
                throws Exception {
            when(mServerSettingsService.getAvailableUserKeyPairAlgorithms())
                    .thenReturn(Arrays.asList(
                            createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version.RSA4096),
                            createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version.RSA2048)));
        }

    }

    // --- Check user key pair tests ---

    @Nested
    class GetAndCheckUserKeyPairsTests {

        private final char[] CRYPTO_PW = {'t','e','s','t'};

        private final String DATA_PATH = "/account/user_key_pair/";

        @BeforeEach
        void setup() {
            mDracoonClientImpl.setEncryptionPassword(CRYPTO_PW);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMocked(true);

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_key_pairs_request.json");
        }

        @Test
        void testCryptoCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            List<UserKeyPair> userKeyPairs = executeMockedWithReturn();

            // Assert data is correct
            List<UserKeyPair> expectedUserKeyPairs = readData(List.class, DATA_PATH +
                    "user_key_pairs.json");
            assertDeepEquals(expectedUserKeyPairs, userKeyPairs);
        }

        @Test
        void testInvalidPassword() {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class, () ->
                    executeMocked(false));

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_PASSWORD_ERROR, thrown.getCode());
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairsQueryError, expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, this::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testErrorUserHasNoKeyPair() {
            // Enqueue response
            enqueueResponse(DATA_PATH + "get_key_pairs_empty_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.getAndCheckUserKeyPairs());

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND, thrown.getCode());
        }

        @Test
        void testCryptoError() {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            assertThrows(DracoonCryptoException.class, this::executeMockedWithException);
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_key_pairs_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

        private void execute() throws Exception {
            mSrv.getAndCheckUserKeyPairs();
        }


        private void executeMocked(boolean ok) throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(ok);
            mSrv.getAndCheckUserKeyPairs();
        }

        private void executeMockedAndVerified() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(true);
            List<UserKeyPair> userKeyPairs = mSrv.getAndCheckUserKeyPairs();
            for (UserKeyPair userKeyPair : userKeyPairs) {
                verify(mCryptoWrapper).checkUserKeyPairPassword(userKeyPair, CRYPTO_PW);
            }
        }

        private List<UserKeyPair> executeMockedWithReturn() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(true);
            return mSrv.getAndCheckUserKeyPairs();
        }

        private void executeMockedWithException() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenThrow(new DracoonCryptoException());
            mSrv.getAndCheckUserKeyPairs();
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    private abstract class BaseCheckUserKeyPairTests {

        protected final char[] CRYPTO_PW = {'t','e','s','t'};

        protected final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected ServerSettingsService mServerSettingsService;

        @BeforeEach
        void setup() throws Exception {
            mServiceLocator.setServerSettingsService(mServerSettingsService);

            mockGetAvailableUserKeyPairVersions();

            mDracoonClientImpl.setEncryptionPassword(CRYPTO_PW);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMocked(true);

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_key_pair_by_version_request.json");
        }

        @Test
        void testCryptoCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairQueryError, expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, this::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testCryptoError() {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            assertThrows(DracoonCryptoException.class, this::executeMockedWithException);
        }

        protected void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_key_pair_by_version_response.json");
        }

        protected void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

        protected abstract void execute() throws Exception;

        protected abstract void executeMocked(boolean ok) throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        private void mockGetAvailableUserKeyPairVersions()
                throws Exception {
            when(mServerSettingsService.getAvailableUserKeyPairVersions())
                    .thenReturn(Collections.singletonList(UserKeyPair.Version.RSA2048));
        }

    }

    @Nested
    class GetAndCheckUserKeyPairTests extends BaseCheckUserKeyPairTests {

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            UserKeyPair userKeyPair = executeMockedWithReturn();

            // Assert data is correct
            UserKeyPair expectedUserKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            assertDeepEquals(expectedUserKeyPair, userKeyPair);
        }

        @Test
        void testInvalidPassword() {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class, () ->
                    executeMocked(false));

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_PASSWORD_ERROR, thrown.getCode());
        }

        @Override
        protected void execute() throws Exception {
            mSrv.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
        }

        @Override
        protected void executeMocked(boolean ok) throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(ok);
            mSrv.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(true);
            UserKeyPair userKeyPair = mSrv.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
            verify(mCryptoWrapper).checkUserKeyPairPassword(userKeyPair, CRYPTO_PW);
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenThrow(new DracoonCryptoException());
            mSrv.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
        }

        private UserKeyPair executeMockedWithReturn() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(true);
            return mSrv.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
        }

    }

    @Nested
    class CheckUserKeyPairPasswordTests extends BaseCheckUserKeyPairTests {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void testResult(boolean expectedOk) throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            boolean ok = executeMockedWithReturn(expectedOk);

            // Assert result is correct
            assertEquals(expectedOk, ok);
        }

        @Override
        protected void execute() throws Exception {
            mSrv.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
        }

        @Override
        protected void executeMocked(boolean ok) throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(ok);
            mSrv.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                "user_key_pair_2048.json");
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(true);
            mSrv.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
            verify(mCryptoWrapper).checkUserKeyPairPassword(
                    argThat(arg -> deepEquals(arg, userKeyPair)),
                    eq(CRYPTO_PW));
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenThrow(new DracoonCryptoException());
            mSrv.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
        }

        private boolean executeMockedWithReturn(boolean ok) throws Exception {
            when(mCryptoWrapper.checkUserKeyPairPassword(any(), any()))
                    .thenReturn(ok);
            return mSrv.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
        }

    }

    // --- Delete user key pair tests ---

    @Nested
    class DeleteUserKeyPairTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected ServerSettingsService mServerSettingsService;

        @BeforeEach
        void setup() throws Exception {
            mServiceLocator.setServerSettingsService(mServerSettingsService);

            mockGetAvailableUserKeyPairVersions();
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "delete_key_pair_by_version_response.json");

            // Execute method to test
            mSrv.deleteUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);

            // Assert requests are valid
            checkRequest(DATA_PATH + "delete_key_pair_by_version_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairDeleteError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.deleteUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        private void mockGetAvailableUserKeyPairVersions()
                throws Exception {
            when(mServerSettingsService.getAvailableUserKeyPairVersions())
                    .thenReturn(Collections.singletonList(UserKeyPair.Version.RSA2048));
        }

    }

    // --- Set user profile attribute tests ---

    @Nested
    class SetUserProfileAttributeTests {

        private final String DATA_PATH = "/account/user_profile_attribute/";

        private final String TEST_KEY = "test-key";
        private final String TEST_VALUE = "test-value";

        @Test
        void testSetRequestsValid() throws Exception {
            executeTestRequestsValid(TEST_VALUE, "set_attribute_request.json",
                    "set_attribute_response.json");
        }

        @Test
        void testDeleteRequestsValid() throws Exception {
            executeTestRequestsValid(null, "delete_attribute_request.json",
                    "delete_attribute_response.json");
        }

        private void executeTestRequestsValid(String value, String requestFilename,
                String responseFilename) throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            mSrv.setUserProfileAttribute(TEST_KEY, value);

            // Assert requests are valid
            checkRequest(DATA_PATH + requestFilename);
        }

        @Test
        void testSetError() {
            executeTestError(TEST_VALUE, mDracoonErrorParser::parseUserProfileAttributesSetError);
        }

        @Test
        void testDeleteError() {
            executeTestError(null, mDracoonErrorParser::parseUserProfileAttributeDeleteError);
        }

        private void executeTestError(String value, ErrorParserFunction errorParserFunc) {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(errorParserFunc, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.setUserProfileAttribute(TEST_KEY, value));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Get user profile attribute tests ---

    @Nested
    class GetUserProfileAttributeTests {

        private final String DATA_PATH = "/account/user_profile_attribute/";

        private final String TEST_KEY = "test-key";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            mSrv.getUserProfileAttribute(TEST_KEY);

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_attribute_request.json");
        }

        @Test
        void testNoDataCorrect() throws Exception {
            assertNull(executeTestDataCorrect("non-existing-key"));
        }

        @Test
        void testDataCorrect() throws Exception {
            assertEquals("test-value", executeTestDataCorrect(TEST_KEY));
        }

        private String executeTestDataCorrect(String key) throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            return mSrv.getUserProfileAttribute(key);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserProfileAttributesQueryError, expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.getUserProfileAttribute(TEST_KEY));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_attribute_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

    }

    // --- Set user avatar tests ---

    @Nested
    class SetUserAvatarTests {

        private final String DATA_PATH = "/account/user_avatar/";

        private final byte[] AVATAR_BYTES = "avatar".getBytes();

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "set_avatar_response.json");

            // Execute method to test
            mSrv.setUserAvatar(AVATAR_BYTES);

            // Assert requests are valid
            checkRequest(DATA_PATH + "set_avatar_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserAvatarSetError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.setUserAvatar(AVATAR_BYTES));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Get user avatar tests ---

    @Nested
    class GetUserAvatarTests {

        private final String DATA_PATH = "/account/user_avatar/";

        private final byte[] AVATAR_BYTES = "avatar".getBytes();

        @Mock
        protected AvatarDownloader mAvatarDownloader;

        @BeforeEach
        void setup() {
            mServiceLocator.setAvatarDownloader(mAvatarDownloader);
        }

        @Test
        void testApiRequestsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMocked();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_avatar_info_request.json");
        }

        @Test
        void testDownloaderCallsValid() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            byte[] avatarBytes = executeMockedWithReturn();

            // Assert data is correct
            assertArrayEquals(AVATAR_BYTES, avatarBytes);
        }

        @Test
        void testApiError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseStandardError(expectedCode);

            // Enqueue response
            enqueueErrorResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.getUserAvatar());

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testDownloaderError() {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeMockedWithException);

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND, thrown.getCode());
        }

        private void executeMocked() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenReturn(AVATAR_BYTES);
            mSrv.getUserAvatar();
        }

        private void executeMockedAndVerified() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenReturn(AVATAR_BYTES);
            mSrv.getUserAvatar();
            verify(mAvatarDownloader).downloadAvatar(mServerUrl +
                    "/api/v4/downloads/avatar/1/c33e748c-d05b-4af2-90e3-1a24d79b1d41");
        }

        private void executeMockedWithException() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenThrow(new DracoonApiException(DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND));
            mSrv.getUserAvatar();
        }

        private byte[] executeMockedWithReturn() throws Exception {
            when(mAvatarDownloader.downloadAvatar(any()))
                    .thenReturn(AVATAR_BYTES);
            return mSrv.getUserAvatar();
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "get_avatar_info_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

    }

    // --- Delete user avatar tests ---

    @Nested
    class DeleteUserAvatarTests {

        private final String DATA_PATH = "/account/user_avatar/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "delete_avatar_response.json");

            // Execute method to test
            mSrv.deleteUserAvatar();

            // Assert requests are valid
            checkRequest(DATA_PATH + "delete_avatar_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserAvatarDeleteError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mSrv.deleteUserAvatar());

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Helper methods ---

    private UserKeyPairAlgorithm createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version version) {
        UserKeyPairAlgorithm algorithm = new UserKeyPairAlgorithm();
        algorithm.setVersion(version);
        algorithm.setState(UserKeyPairAlgorithm.State.REQUIRED);
        return algorithm;
    }

}
