package com.dracoon.sdk.error;

import com.dracoon.sdk.crypto.CryptoException;

public class DracoonCryptoException extends DracoonException {

    private static final long serialVersionUID = 2941100473405716679L;

    // TODO: Added error codes to allow easy determination of error cause

    public DracoonCryptoException(CryptoException e) {
        super(e.getMessage(), e.getCause());
    }

}
