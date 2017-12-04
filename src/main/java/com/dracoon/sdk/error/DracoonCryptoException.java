package com.dracoon.sdk.error;

public class DracoonCryptoException extends DracoonException {

    private static final long serialVersionUID = 2941100473405716679L;

    // TODO: Added error codes to allow easy determination of error cause

    public DracoonCryptoException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

}
