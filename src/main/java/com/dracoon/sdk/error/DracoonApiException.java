package com.dracoon.sdk.error;

public class DracoonApiException extends DracoonException {

    private static final long serialVersionUID = -363709480322215168L;

    private DracoonApiCode mCode;

    public DracoonApiException() {
        mCode = DracoonApiCode.SERVER_UNKNOWN_ERROR;
    }

    public DracoonApiException(DracoonApiCode code) {
        super(code.getText());
        mCode = code;
    }

    public DracoonApiException(DracoonApiCode code, Throwable cause) {
        super(code.getText(), cause);
        mCode = code;
    }

    public DracoonApiCode getCode() {
        return mCode;
    }

}
