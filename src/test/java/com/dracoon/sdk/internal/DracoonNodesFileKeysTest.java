package com.dracoon.sdk.internal;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DracoonNodesFileKeysTest extends DracoonRequestHandlerTest {

    private DracoonNodesImpl mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDni = new DracoonNodesImpl(mDracoonClientImpl);
    }

    // --- Get file key tests ---

    @Nested
    class GetFileKeyTests {

        private final String DATA_PATH = "/nodes/get_file_key/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue response
            enqueueResponse(DATA_PATH + "get_file_key_response.json");

            // Execute method to test
            mDni.getFileKey(4L);

            // Assert request are valid
            checkRequest(DATA_PATH + "get_file_key_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue response
            enqueueResponse(DATA_PATH + "get_file_key_response.json");

            // Execute method to test
            EncryptedFileKey encFileKey = mDni.getFileKey(4L);

            // Assert data is correct
            EncryptedFileKey expectedEncFileKey = readData(EncryptedFileKey.class, DATA_PATH +
                    "enc_file_key.json");
            assertDeepEquals(expectedEncFileKey, encFileKey);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseFileKeyQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "file_key_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> mDni.getFileKey(4L));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        @Test
        void testErrorUnknownVersion() {
            // Enqueue response
            enqueueResponse(DATA_PATH + "file_key_unknown_version_response.json");

            // Execute method to test
            DracoonCryptoException thrown = assertThrows(DracoonCryptoException.class,
                    () -> mDni.getFileKey(4L));

            // Assert correct error code
            DracoonCryptoCode expectedCode = DracoonCryptoCode.UNKNOWN_ALGORITHM_VERSION_ERROR;
            assertEquals(expectedCode, thrown.getCode());
        }

    }

}
