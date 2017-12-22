package com.dracoon.sdk.error;

/**
 * Signals a Dracoon client error.
 *
 * @see DracoonApiException
 * @see DracoonCryptoException
 * @see DracoonFileIOException
 * @see DracoonNetIOException
 */
public class DracoonException extends Exception {

    private static final long serialVersionUID = 5642720416433996541L;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonException() {

    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DracoonException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.<br>
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     */
    public DracoonException(String message, Throwable cause) {
        super(message, cause);
    }

}
