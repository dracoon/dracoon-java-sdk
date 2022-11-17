package com.dracoon.sdk.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.InvalidKeyPairException;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DracoonAccountTest extends DracoonRequestHandlerTest {

    private DracoonAccountImpl mDai;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDai = new DracoonAccountImpl(mDracoonClientImpl);
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
            mDai.pingUser();

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
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mDai::pingUser);

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
            mDai.getUserAccount();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_user_account_request.json");
            checkRequest(DATA_PATH + "get_user_avatar_info_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponses();

            // Execute method to test
            UserAccount userAccount = mDai.getUserAccount();

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
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mDai::getUserAccount);

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
            DracoonApiException thrown = assertThrows(DracoonApiException.class, mDai::getUserAccount);

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
            mDai.getCustomerAccount();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_customer_account_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueOkResponse();

            // Execute method to test
            CustomerAccount customerAccount = mDai.getCustomerAccount();

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
                    mDai::getCustomerAccount);

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
            executeTestRequestsValid("get_key_pair_response.json", "get_key_pair_request.json");
        }

        @Test
        void testRequestsValidNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            executeTestRequestsValid("get_key_pairs_response.json", "get_key_pairs_request.json");
        }

        private void executeTestRequestsValid(String requestFilename, String responseFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + requestFilename);

            // Execute method to test
            mDai.getUserKeyPairAlgorithmVersions();

            // Assert requests are valid
            checkRequest(DATA_PATH + responseFilename);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestNoDataCorrect();
        }

        @Test
        void testNoDataCorrectNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            executeTestNoDataCorrect();
        }

        private void executeTestNoDataCorrect() throws Exception {
            // Enqueue response
            enqueueResponse(DATA_PATH + "not_found_response.json");

            // Execute method to test
            List<UserKeyPairAlgorithm.Version> versions = mDai.getUserKeyPairAlgorithmVersions();

            // Assert data is correct
            assertEquals(new ArrayList<UserKeyPairAlgorithm.Version>(), versions);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_key_pair_response.json", "user_key_pair_algo_version.json");
        }

        @Test
        void testDataCorrectNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            executeTestDataCorrect("get_key_pairs_response.json", "user_key_pair_algo_versions.json");
        }

        private void executeTestDataCorrect(String requestFilename, String dataFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + requestFilename);

            // Execute method to test
            List<UserKeyPairAlgorithm.Version> versions = mDai.getUserKeyPairAlgorithmVersions();

            // Assert data is correct
            List<UserKeyPairAlgorithm.Version> expectedVersions = readData(List.class, DATA_PATH +
                    dataFilename);
            assertDeepEquals(expectedVersions, versions);
        }

        @Test
        void testError() {
            executeTestError();
        }

        @Test
        void testErrorNewCrypto() {
            setApiVersionNewCryptoAlgos();
            executeTestError();
        }

        private void executeTestError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairsQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    mDai::getUserKeyPairAlgorithmVersions);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Set user key pair tests ---

    @Nested
    class SetUserKeyPairTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        private final String CRYPTO_PW = "test";

        @BeforeEach
        void setup() {
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
        void testCryptoSdkCallsValid() throws Exception {
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
        void testCryptoSdkError() {
            assertThrows(DracoonCryptoException.class, this::executeMockedWithException);
        }

        private void executeMocked() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(userKeyPair);
                mDai.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
            }
        }

        private void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(userKeyPair);
                mDai.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
                mock.verify(() -> Crypto.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW));
            }
        }

        private void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> crypto = Mockito.mockStatic(Crypto.class)) {
                crypto.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mDai.setUserKeyPair(UserKeyPairAlgorithm.Version.RSA2048);
            }
        }

        private void enqueueOkResponse() {
            enqueueResponse(DATA_PATH + "set_key_pair_response.json");
        }

        private void enqueueErrorResponse() {
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");
        }

    }

    // --- Generate user key pair tests ---

    @Nested
    class GenerateUserKeyPairTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        private final String CRYPTO_PW = "test";

        @Test
        void testCryptoSdkCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Read expect data
            UserKeyPair expectedUserKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");

            // Execute method to test
            UserKeyPair userKeyPair = executeMockedWithReturn(expectedUserKeyPair);

            // Assert data is correct
            assertDeepEquals(expectedUserKeyPair, userKeyPair);
        }

        @Test
        void testCryptoSdkError() {
            assertThrows(DracoonCryptoException.class, this::executeMockedWithException);
        }

        private void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                    "user_key_pair_2048.json");
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(userKeyPair);
                mDai.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
                mock.verify(() -> Crypto.generateUserKeyPair(UserKeyPair.Version.RSA2048,
                        CRYPTO_PW));
            }
        }

        private UserKeyPair executeMockedWithReturn(UserKeyPair expectedUserKeyPair) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(expectedUserKeyPair);
                return mDai.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
            }
        }

        private void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> crypto = Mockito.mockStatic(Crypto.class)) {
                crypto.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mDai.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
            }
        }

    }

    // --- Get preferred user key pair tests ---

    @Nested
    class GetPreferredUserKeyPairTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected DracoonServerSettingsImpl mServerSettingsImpl;

        private List<UserKeyPairAlgorithm> keyPairAlgorithms;
        private List<UserKeyPairAlgorithm> keyPairAlgorithmsNewCrypto;

        @BeforeEach
        void setup() {
            mDracoonClientImpl.setServerSettingsImpl(mServerSettingsImpl);

            keyPairAlgorithms = Arrays.asList(
                    createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version.RSA2048));

            keyPairAlgorithmsNewCrypto = Arrays.asList(
                    createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version.RSA4096),
                    createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version.RSA2048));
        }

        @Test
        void testRequestsValid() throws Exception {
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithms);
            executeTestRequestsValid("get_key_pair_response.json", "get_key_pair_request.json");
        }

        @Test
        void testRequestsValidNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithmsNewCrypto);
            executeTestRequestsValid("get_key_pairs_response.json", "get_key_pairs_request.json");
        }

        private void executeTestRequestsValid(String requestFilename, String responseFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + requestFilename);

            // Execute method to test
            mDai.getPreferredUserKeyPair();

            // Assert requests are valid
            checkRequest(DATA_PATH + responseFilename);
        }

        @Test
        void testDataCorrect() throws Exception {
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithms);
            executeTestDataCorrect("get_key_pair_response.json", "user_key_pair_2048.json");
        }

        @Test
        void testDataCorrectNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithmsNewCrypto);
            executeTestDataCorrect("get_key_pairs_response.json", "user_key_pair_4096.json");
        }

        private void executeTestDataCorrect(String requestFilename, String dataFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + requestFilename);

            // Execute method to test
            UserKeyPair userKeyPair = mDai.getPreferredUserKeyPair();

            // Assert data is correct
            UserKeyPair expectedUserKeyPair = readData(UserKeyPair.class, DATA_PATH + dataFilename);
            assertDeepEquals(expectedUserKeyPair, userKeyPair);
        }

        @Test
        void testError() throws Exception {
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithms);
            executeTestError();
        }

        @Test
        void testErrorNewCrypto() throws Exception {
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithms);
            setApiVersionNewCryptoAlgos();
            executeTestError();
        }

        private void executeTestError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            mockParseError(mDracoonErrorParser::parseUserKeyPairsQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "precondition_failed_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mDai.getPreferredUserKeyPair());

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testErrorUserHasNoKeyPair() throws Exception {
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithms);
            executeTestErrorUserHasNoKeyPair();
        }

        @Test
        void testErrorUserHasNoKeyPairNewCrypto() throws Exception {
            setApiVersionNewCryptoAlgos();
            mockGetAvailableUserKeyPairAlgorithms(keyPairAlgorithmsNewCrypto);
            executeTestErrorUserHasNoKeyPair();
        }

        private void executeTestErrorUserHasNoKeyPair() {
            // Enqueue response
            enqueueResponse(DATA_PATH + "not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    mDai.getPreferredUserKeyPair());

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND, thrown.getCode());
        }

        private void mockGetAvailableUserKeyPairAlgorithms(List<UserKeyPairAlgorithm> algorithms)
                throws Exception {
            when(mServerSettingsImpl.getAvailableUserKeyPairAlgorithms()).thenReturn(algorithms);
        }

    }

    // --- Check user key pair tests ---

    @Nested
    class GetAndCheckUserKeyPairsTests {

        private final String DATA_PATH = "/account/user_key_pair/";

        @BeforeEach
        void setup() {
            setApiVersionNewCryptoAlgos();
            mDracoonClientImpl.setEncryptionPassword("test");
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
        void testCryptoSdkCallsValid() throws Exception {
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
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    executeMocked(true));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testCryptoSdkError() {
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

        private void executeMocked(boolean ok) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(ok);
                mDai.getAndCheckUserKeyPairs();
            }
        }

        private void executeMockedAndVerified() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                List<UserKeyPair> userKeyPairs = mDai.getAndCheckUserKeyPairs();
                for (UserKeyPair userKeyPair : userKeyPairs) {
                    mock.verify(() -> Crypto.checkUserKeyPair(userKeyPair,
                            mDracoonClientImpl.getEncryptionPassword()));
                }
            }
        }

        private List<UserKeyPair> executeMockedWithReturn() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                return mDai.getAndCheckUserKeyPairs();
            }
        }

        private void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> crypto = Mockito.mockStatic(Crypto.class)) {
                crypto.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mDai.getAndCheckUserKeyPairs();
            }
        }

    }

    @SuppressWarnings({
            "unused",
            "java:S2699" // SONAR: Assert statements are located in subclasses
    })
    abstract class BaseCheckUserKeyPairTests {

        protected final String DATA_PATH = "/account/user_key_pair/";

        @Mock
        protected DracoonServerSettingsImpl mServerSettingsImpl;

        @BeforeEach
        void setup() throws Exception {
            mDracoonClientImpl.setServerSettingsImpl(mServerSettingsImpl);

            mockGetAvailableUserKeyPairVersions();

            setApiVersionNewCryptoAlgos();
            mDracoonClientImpl.setEncryptionPassword("test");
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
        void testCryptoSdkCallsValid() throws Exception {
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
            DracoonApiException thrown = assertThrows(DracoonApiException.class, () ->
                    executeMocked(true));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testCryptoSdkError() {
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

        protected abstract void executeMocked(boolean ok) throws Exception;

        protected abstract void executeMockedAndVerified() throws Exception;

        protected abstract void executeMockedWithException() throws Exception;

        private void mockGetAvailableUserKeyPairVersions()
                throws Exception {
            when(mServerSettingsImpl.getAvailableUserKeyPairVersions())
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
        protected void executeMocked(boolean ok) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(ok);
                mDai.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
            }
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                UserKeyPair userKeyPair = mDai.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
                mock.verify(() -> Crypto.checkUserKeyPair(userKeyPair,
                            mDracoonClientImpl.getEncryptionPassword()));
            }
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> crypto = Mockito.mockStatic(Crypto.class)) {
                crypto.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mDai.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
            }
        }

        private UserKeyPair executeMockedWithReturn() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                return mDai.getAndCheckUserKeyPair(UserKeyPair.Version.RSA2048);
            }
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
        protected void executeMocked(boolean ok) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(ok);
                mDai.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
            }
        }

        @Override
        protected void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = readData(UserKeyPair.class, DATA_PATH +
                "user_key_pair_2048.json");
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                mDai.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
                mock.verify(() -> Crypto.checkUserKeyPair(
                        argThat(arg -> deepEquals(arg, userKeyPair)),
                        eq(mDracoonClientImpl.getEncryptionPassword())));
            }
        }

        @Override
        protected void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> crypto = Mockito.mockStatic(Crypto.class)) {
                crypto.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mDai.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);;
            }
        }

        private boolean executeMockedWithReturn(boolean ok) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(ok);
                return mDai.checkUserKeyPairPassword(UserKeyPairAlgorithm.Version.RSA2048);
            }
        }

    }

    // --- Delete user key pair tests ---

    // TODO

    // --- Set user profile attribute tests ---

    // TODO

    // --- Get user profile attribute tests ---

    // TODO

    // --- Set user avatar tests ---

    // TODO

    // --- Get user avatar tests ---

    // TODO

    // --- Delete user avatar tests ---

    // TODO

    // --- Helper methods ---

    private void setApiVersionNewCryptoAlgos() {
        mDracoonClientImpl.setApiVersion(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS);
    }

    private UserKeyPairAlgorithm createUserKeyPairAlgorithm(UserKeyPairAlgorithm.Version version) {
        UserKeyPairAlgorithm algorithm = new UserKeyPairAlgorithm();
        algorithm.setVersion(version);
        algorithm.setState(UserKeyPairAlgorithm.State.REQUIRED);
        return algorithm;
    }

    private void mockParseStandardError(DracoonApiCode code) {
        when(mDracoonErrorParser.parseStandardError(any(retrofit2.Response.class)))
                .thenReturn(code);
    }

    private void mockParseError(Function<Response, DracoonApiCode> func, DracoonApiCode code) {
        when(func.apply(any(retrofit2.Response.class)))
                .thenReturn(code);
    }

}
