package com.dracoon.sdk.internal;

import com.dracoon.sdk.BaseTest;
import com.dracoon.sdk.TestLogger;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.InvalidKeyPairException;
import com.dracoon.sdk.crypto.error.InvalidPasswordException;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class CryptoWrapperTest extends BaseTest {

    private CryptoWrapper mCrypto;

    @BeforeEach
    void setup() throws Exception {
        mCrypto = new CryptoWrapper(new TestLogger());
    }

    // --- User key pair tests ---

    private static abstract class UserKeyPairTests {

        protected final String DATA_PATH = "/crypto/user_key_pair/";

        protected final String CRYPTO_PW = "test";

        protected UserKeyPair getUserKeyPair() {
            return readData(UserKeyPair.class, DATA_PATH + "user_key_pair_2048.json");
        }

    }

    @Nested
    class GenerateUserKeyPairTests extends UserKeyPairTests {

        @Test
        void testCryptoSdkCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Read expect data
            UserKeyPair expectedUserKeyPair = getUserKeyPair();

            // Execute method to test
            UserKeyPair userKeyPair = executeMockedWithReturn(expectedUserKeyPair);

            // Assert data is correct
            assertDeepEquals(expectedUserKeyPair, userKeyPair);
        }

        @Test
        void testCryptoSdkError() {
            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    this::executeMockedWithException);

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_PASSWORD_ERROR, thrown.getCode());
        }

        private void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(userKeyPair);
                mCrypto.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
                mock.verify(() -> Crypto.generateUserKeyPair(UserKeyPair.Version.RSA2048,
                        CRYPTO_PW));
            }
        }

        private UserKeyPair executeMockedWithReturn(UserKeyPair expectedUserKeyPair)
                throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenReturn(expectedUserKeyPair);
                return mCrypto.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
            }
        }

        private void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateUserKeyPair(any(), any()))
                        .thenThrow(new InvalidPasswordException());
                mCrypto.generateUserKeyPair(UserKeyPair.Version.RSA2048, CRYPTO_PW);
            }
        }

    }

    @Nested
    class CheckUserKeyPairTests extends UserKeyPairTests {

        @Test
        void testCryptoSdkCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void testDataCorrect(boolean expectedResult) throws Exception {
            // Execute method to test
            boolean result = executeMockedWithReturn(expectedResult);

            // Assert data is correct
            assertEquals(expectedResult, result);
        }

        @Test
        void testCryptoSdkError() {
            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    this::executeMockedWithException);

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_KEY_ERROR, thrown.getCode());
        }

        private void executeMockedAndVerified() throws Exception {
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(true);
                mCrypto.checkUserKeyPairPassword(userKeyPair, CRYPTO_PW);
                mock.verify(() -> Crypto.checkUserKeyPair(userKeyPair, CRYPTO_PW));
            }
        }

        private boolean executeMockedWithReturn(boolean expectedResult) throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenReturn(expectedResult);
                return mCrypto.checkUserKeyPairPassword(getUserKeyPair(), CRYPTO_PW);
            }
        }

        private void executeMockedWithException() throws Exception {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.checkUserKeyPair(any(), any()))
                        .thenThrow(new InvalidKeyPairException());
                mCrypto.checkUserKeyPairPassword(getUserKeyPair(), CRYPTO_PW);
            }
        }

    }

}
