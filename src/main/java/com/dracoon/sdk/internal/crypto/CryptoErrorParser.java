package com.dracoon.sdk.internal.crypto;

import com.dracoon.sdk.crypto.error.BadFileException;
import com.dracoon.sdk.crypto.error.CryptoSystemException;
import com.dracoon.sdk.crypto.error.InvalidFileKeyException;
import com.dracoon.sdk.crypto.error.InvalidKeyPairException;
import com.dracoon.sdk.crypto.error.InvalidPasswordException;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.error.DracoonCryptoCode;

public class CryptoErrorParser {

    private CryptoErrorParser() {

    }

    public static DracoonCryptoCode parseCause(Exception e) {
        if (e instanceof InvalidPasswordException) {
            return DracoonCryptoCode.INVALID_PASSWORD_ERROR;
        } else if (e instanceof BadFileException) {
            return DracoonCryptoCode.BAD_FILE_ERROR;
        } else if (e instanceof UnknownVersionException) {
            return DracoonCryptoCode.UNKNOWN_ALGORITHM_VERSION_ERROR;
        } else if (e instanceof InvalidKeyPairException
                || e instanceof InvalidFileKeyException
                || e instanceof IllegalStateException) {
            return DracoonCryptoCode.INVALID_KEY_ERROR;
        } else if (e instanceof IllegalArgumentException
                || e instanceof CryptoSystemException) {
            return DracoonCryptoCode.INTERNAL_ERROR;
        } else {
            return DracoonCryptoCode.UNKNOWN_ERROR;
        }
    }

}
