package com.dracoon.sdk.error;

/**
 * Signals a Dracoon REST API error.<br>
 * <br>
 * The exception stores an error code which can be used to determine the error cause.
 */
public class DracoonApiException extends DracoonException {

    private static final long serialVersionUID = -363709480322215168L;

    private DracoonApiCode mCode;

    /**
     * Constructs a new exception with an unknown error code.
     */
    public DracoonApiException() {
        mCode = DracoonApiCode.SERVER_UNKNOWN_ERROR;
    }

    /**
     * Constructs a new exception with a specified error code.
     *
     * @param code The error code.
     */
    public DracoonApiException(DracoonApiCode code) {
        super(code.getText());
        mCode = code;
    }

    /**
     * Returns an error code which describes what caused the error.
     *
     * @return the error code
     */
    public DracoonApiCode getCode() {
        return mCode;
    }

}
