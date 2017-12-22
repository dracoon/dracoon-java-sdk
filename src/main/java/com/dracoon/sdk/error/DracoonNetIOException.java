package com.dracoon.sdk.error;

/**
 * Signals a network IO error.
 */
public class DracoonNetIOException extends DracoonException {

    private static final long serialVersionUID = -1497143863572050306L;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonNetIOException() {

    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DracoonNetIOException(String message) {
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
    public DracoonNetIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
