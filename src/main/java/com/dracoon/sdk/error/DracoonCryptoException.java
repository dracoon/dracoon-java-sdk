package com.dracoon.sdk.error;

/**
 * Signals a Dracoon crypto error.<br>
 * <br>
 * The exception stores an error code which can be used to determine the error cause.
 */
public class DracoonCryptoException extends DracoonException {

    private static final long serialVersionUID = 2941100473405716679L;

    private DracoonCryptoCode mCode;

    /**
     * Constructs a new exception with an unknown error code. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonCryptoException() {
        mCode = DracoonCryptoCode.UNKNOWN_ERROR;
    }

    /**
     * Constructs a new exception with a specified error code. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param code The error code.
     */
    public DracoonCryptoException(DracoonCryptoCode code) {
        super(code.getText());
        mCode = code;
    }

    /**
     * Constructs a new exception with a specified error code and cause.<br>
     *
     * @param code  The error code.
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A <code>null</code> value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     */
    public DracoonCryptoException(DracoonCryptoCode code, Throwable cause) {
        super(code.getText(), cause);
        mCode = code;
    }

    /**
     * Returns an error code which describes what caused the error.
     *
     * @return the error code
     */
    public DracoonCryptoCode getCode() {
        return mCode;
    }

}
