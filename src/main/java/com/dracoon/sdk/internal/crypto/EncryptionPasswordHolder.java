package com.dracoon.sdk.internal.crypto;

import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;

public class EncryptionPasswordHolder {

    private char[] mPassword;

    public char[] get() {
        return mPassword;
    }

    public char[] getOrAbort() throws DracoonCryptoException {
        char[] password = mPassword;
        if (password == null) {
            throw new DracoonCryptoException(DracoonCryptoCode.MISSING_PASSWORD_ERROR);
        }
        return password;
    }

    public void set(char[] password) {
        mPassword = password;
    }

}
