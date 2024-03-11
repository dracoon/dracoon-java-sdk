package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.FileDecryptionCipher;
import com.dracoon.sdk.crypto.FileEncryptionCipher;
import com.dracoon.sdk.crypto.error.CryptoException;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;

class CryptoWrapper {

    private static final String LOG_TAG = CryptoWrapper.class.getSimpleName();

    private final Log mLog;

    CryptoWrapper(Log log) {
        mLog = log;
    }

    public UserKeyPair generateUserKeyPair(UserKeyPair.Version userKeyPairVersion, char[] password)
            throws DracoonCryptoException {
        try {
            return Crypto.generateUserKeyPair(userKeyPairVersion, password);
        } catch (CryptoException e) {
            String errorText = String.format("Generation of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public boolean checkUserKeyPairPassword(UserKeyPair userKeyPair, char[] password)
            throws DracoonCryptoException {
        try {
            return Crypto.checkUserKeyPair(userKeyPair, password);
        } catch (CryptoException e) {
            String errorText = String.format("Check of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public PlainFileKey generateFileKey(PlainFileKey.Version version) {
        return Crypto.generateFileKey(version);
    }

    public EncryptedFileKey encryptFileKey(Long nodeId, PlainFileKey plainFileKey,
            UserPublicKey userPublicKey) throws DracoonCryptoException {
        try {
            return Crypto.encryptFileKey(plainFileKey, userPublicKey);
        } catch (CryptoException e) {
            String errorText;
            if (nodeId != null) {
                errorText = String.format("Encryption of file key for node '%d' failed! %s", nodeId,
                        e.getMessage());
            } else {
                errorText = String.format("Encryption of file key failed! %s", e.getMessage());
            }
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public PlainFileKey decryptFileKey(Long nodeId, EncryptedFileKey encFileKey,
            UserPrivateKey userPrivateKey, char[] userPrivateKeyPassword)
            throws DracoonCryptoException {
        try {
            return Crypto.decryptFileKey(encFileKey, userPrivateKey, userPrivateKeyPassword);
        } catch (CryptoException e) {
            String errorText;
            if (nodeId != null) {
                errorText = String.format("Decryption of file key for node '%d' failed! %s", nodeId,
                        e.getMessage());
            } else {
                errorText = String.format("Decryption of file key failed! %s", e.getMessage());
            }
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public FileEncryptionCipher createFileEncryptionCipher(PlainFileKey plainFileKey)
            throws CryptoException {
        return Crypto.createFileEncryptionCipher(plainFileKey);
    }

    public FileDecryptionCipher createFileDecryptionCipher(PlainFileKey plainFileKey)
            throws CryptoException {
        return Crypto.createFileDecryptionCipher(plainFileKey);
    }

}
