package com.dracoon.sdk.internal;

import com.dracoon.sdk.crypto.BadFileException;
import com.dracoon.sdk.crypto.CryptoSystemException;
import com.dracoon.sdk.crypto.InvalidFileKeyException;
import com.dracoon.sdk.crypto.InvalidKeyPairException;
import com.dracoon.sdk.crypto.InvalidPasswordException;
import com.dracoon.sdk.error.DracoonCryptoCode;

public class CryptoErrorParser {

    private CryptoErrorParser() {

    }

    public static DracoonCryptoCode parseCause(Exception e) {
        if (e instanceof InvalidPasswordException) {
            return DracoonCryptoCode.INVALID_PASSWORD_ERROR;
        } else if (e instanceof BadFileException) {
            return DracoonCryptoCode.BAD_FILE_ERROR;
        } else if (e instanceof IllegalArgumentException
                || e instanceof InvalidKeyPairException
                || e instanceof InvalidFileKeyException
                || e instanceof IllegalStateException) {
            return DracoonCryptoCode.INTERNAL_ERROR;
        } else if (e instanceof CryptoSystemException) {
            return DracoonCryptoCode.SYSTEM_ERROR;
        } else {
            return DracoonCryptoCode.UNKNOWN_ERROR;
        }
    }

}
