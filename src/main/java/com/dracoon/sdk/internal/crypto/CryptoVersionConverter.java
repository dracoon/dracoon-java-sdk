package com.dracoon.sdk.internal.crypto;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;

public final class CryptoVersionConverter {

    private CryptoVersionConverter() {

    }

    public static UserKeyPair.Version toUserKeyPairVersion(UserKeyPairAlgorithm.Version version)
            throws DracoonCryptoException {
        switch (version) {
            case RSA2048:
                return UserKeyPair.Version.RSA2048;
            case RSA4096:
                return UserKeyPair.Version.RSA4096;
            default:
                throw new DracoonCryptoException(DracoonCryptoCode.INTERNAL_ERROR);
        }
    }

    public static UserKeyPairAlgorithm.Version fromUserKeyPairVersion(UserKeyPair.Version version)
            throws DracoonCryptoException {
        switch (version) {
            case RSA2048:
                return UserKeyPairAlgorithm.Version.RSA2048;
            case RSA4096:
                return UserKeyPairAlgorithm.Version.RSA4096;
            default:
                throw new DracoonCryptoException(DracoonCryptoCode.INTERNAL_ERROR);
        }
    }

    public static UserKeyPair.Version determineUserKeyPairVersion(EncryptedFileKey.Version version)
            throws DracoonCryptoException {
        switch (version) {
            case RSA2048_AES256GCM:
                return UserKeyPair.Version.RSA2048;
            case RSA4096_AES256GCM:
                return UserKeyPair.Version.RSA4096;
            default:
                throw new DracoonCryptoException(DracoonCryptoCode.INTERNAL_ERROR);
        }
    }

    public static EncryptedFileKey.Version determineEncryptedFileKeyVersion(
            UserKeyPair.Version version) throws DracoonCryptoException {
        switch (version) {
            case RSA2048:
                return EncryptedFileKey.Version.RSA2048_AES256GCM;
            case RSA4096:
                return EncryptedFileKey.Version.RSA4096_AES256GCM;
            default:
                throw new DracoonCryptoException(DracoonCryptoCode.INTERNAL_ERROR);
        }
    }

    public static PlainFileKey.Version determinePlainFileKeyVersion(UserKeyPair.Version version)
            throws DracoonCryptoException {
        switch (version) {
            case RSA2048:
            case RSA4096:
                return PlainFileKey.Version.AES256GCM;
            default:
                throw new DracoonCryptoException(DracoonCryptoCode.INTERNAL_ERROR);
        }
    }

}
