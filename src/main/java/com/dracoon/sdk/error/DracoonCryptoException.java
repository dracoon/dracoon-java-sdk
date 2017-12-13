package com.dracoon.sdk.error;

public class DracoonCryptoException extends DracoonException {

    private static final long serialVersionUID = 2941100473405716679L;

    private DracoonCryptoCode mCode;

    public DracoonCryptoException() {
        mCode = DracoonCryptoCode.UNKNOWN_ERROR;
    }

    public DracoonCryptoException(DracoonCryptoCode code) {
        super(code.getText());
        mCode = code;
    }

    public DracoonCryptoException(DracoonCryptoCode code, Throwable cause) {
        super(code.getText(), cause);
        mCode = code;
    }

    public DracoonCryptoCode getCode() {
        return mCode;
    }

}
