package com.dracoon.sdk.internal;

import com.dracoon.sdk.BaseTest;
import com.dracoon.sdk.TestLogger;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.InvalidFileKeyException;
import com.dracoon.sdk.crypto.error.InvalidKeyPairException;
import com.dracoon.sdk.crypto.error.InvalidPasswordException;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class CryptoWrapperTest extends BaseTest {

    private final char[] CRYPTO_PW = {'t','e','s','t'};

    private CryptoWrapper mCrypto;

    @BeforeEach
    void setup() throws Exception {
        mCrypto = new CryptoWrapper(new TestLogger());
    }

    // --- User key pair tests ---

    private static abstract class UserKeyPairTests {

        protected final String DATA_PATH = "/crypto/user_key_pair/";

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

    // --- File key tests ---

    private static abstract class FileKeyTests {

        protected final String DATA_PATH = "/crypto/file_key/";

        protected UserKeyPair getUserKeyPair() {
            return readData(UserKeyPair.class, DATA_PATH + "user_key_pair_2048.json");
        }

        protected PlainFileKey getPlainFileKey() {
            return readData(PlainFileKey.class, DATA_PATH + "plain_file_key.json");
        }

        protected EncryptedFileKey getEncryptedFileKey() {
            return readData(EncryptedFileKey.class, DATA_PATH + "enc_file_key.json");
        }

    }

    @Nested
    class GenerateFileKeyTests extends FileKeyTests {

        @Test
        void testCryptoSdkCallsValid() {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Read expect data
            PlainFileKey expectedPlainFileKey = getPlainFileKey();

            // Execute method to test
            PlainFileKey plainFileKey = executeMockedWithReturn(expectedPlainFileKey);

            // Assert data is correct
            assertDeepEquals(expectedPlainFileKey, plainFileKey);
        }

        private void executeMockedAndVerified() {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateFileKey(any()))
                        .thenReturn(getPlainFileKey());
                mCrypto.generateFileKey(PlainFileKey.Version.AES256GCM);
                mock.verify(() -> Crypto.generateFileKey(PlainFileKey.Version.AES256GCM));
            }
        }

        private PlainFileKey executeMockedWithReturn(PlainFileKey expectedPlainFileKey) {
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.generateFileKey(any()))
                        .thenReturn(expectedPlainFileKey);
                return mCrypto.generateFileKey(PlainFileKey.Version.AES256GCM);
            }
        }

    }

    @Nested
    class EncryptFileKeyTests extends FileKeyTests {

        @Test
        void testCryptoSdkCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Read expect data
            EncryptedFileKey expectedEncFileKey = getEncryptedFileKey();

            // Execute method to test
            EncryptedFileKey encFileKey = executeMockedWithReturn(expectedEncFileKey);

            // Assert data is correct
            assertDeepEquals(expectedEncFileKey, encFileKey);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {1L})
        void testCryptoSdkError(Long nodeId) {
            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    () -> executeMockedWithException(nodeId));

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_KEY_ERROR, thrown.getCode());
        }

        private void executeMockedAndVerified() throws Exception {
            PlainFileKey plainFileKey = getPlainFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.encryptFileKey(any(), any()))
                        .thenReturn(getEncryptedFileKey());
                mCrypto.encryptFileKey(null, plainFileKey, userKeyPair.getUserPublicKey());
                mock.verify(() -> Crypto.encryptFileKey(plainFileKey,
                        userKeyPair.getUserPublicKey()));
            }
        }

        private EncryptedFileKey executeMockedWithReturn(EncryptedFileKey expectedEncFileKey)
                throws Exception {
            PlainFileKey plainFileKey = getPlainFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.encryptFileKey(any(), any()))
                        .thenReturn(expectedEncFileKey);
                return mCrypto.encryptFileKey(null, plainFileKey, userKeyPair.getUserPublicKey());
            }
        }

        private void executeMockedWithException(Long nodeId) throws Exception {
            PlainFileKey plainFileKey = getPlainFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.encryptFileKey(any(), any()))
                        .thenThrow(new InvalidFileKeyException());
                mCrypto.encryptFileKey(nodeId, plainFileKey, userKeyPair.getUserPublicKey());
            }
        }

    }

    @Nested
    class DecryptFileKeyTests extends FileKeyTests {

        @Test
        void testCryptoSdkCallsValid() throws Exception {
            executeMockedAndVerified();
        }

        @Test
        void testDataCorrect() throws Exception {
            // Read expect data
            PlainFileKey expectedPlainFileKey = getPlainFileKey();

            // Execute method to test
            PlainFileKey plainFileKey = executeMockedWithReturn(expectedPlainFileKey);

            // Assert data is correct
            assertDeepEquals(expectedPlainFileKey, plainFileKey);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {1L})
        void testCryptoSdkError(Long nodeId) {
            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    () -> executeMockedWithException(nodeId));

            // Assert correct error code
            assertEquals(DracoonCryptoCode.INVALID_KEY_ERROR, thrown.getCode());
        }

        private void executeMockedAndVerified() throws Exception {
            EncryptedFileKey encFileKey = getEncryptedFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.decryptFileKey(any(), any(), any()))
                        .thenReturn(getPlainFileKey());
                mCrypto.decryptFileKey(null, encFileKey, userKeyPair.getUserPrivateKey(),
                        CRYPTO_PW);
                mock.verify(() -> Crypto.decryptFileKey(encFileKey, userKeyPair.getUserPrivateKey(),
                        CRYPTO_PW));
            }
        }

        private PlainFileKey executeMockedWithReturn(PlainFileKey expectedPlainFileKey)
                throws Exception {
            EncryptedFileKey encFileKey = getEncryptedFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.decryptFileKey(any(), any(), any()))
                        .thenReturn(expectedPlainFileKey);
                return mCrypto.decryptFileKey(null, encFileKey, userKeyPair.getUserPrivateKey(),
                        CRYPTO_PW);
            }
        }

        private void executeMockedWithException(Long nodeId) throws Exception {
            EncryptedFileKey encFileKey = getEncryptedFileKey();
            UserKeyPair userKeyPair = getUserKeyPair();
            try (MockedStatic<Crypto> mock = Mockito.mockStatic(Crypto.class)) {
                mock.when(() -> Crypto.decryptFileKey(any(), any(), any()))
                        .thenThrow(new InvalidFileKeyException());
                mCrypto.decryptFileKey(nodeId, encFileKey, userKeyPair.getUserPrivateKey(),
                        CRYPTO_PW);
            }
        }

    }

}
