package com.dracoon.sdk.error;

/**
 * Signals a insecure network connection.
 */
public class DracoonNetInsecureException extends DracoonNetIOException {

    private static final long serialVersionUID = -7328327402956962496L;

    /**
     * Constructs a new exception with <code>null</code> as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonNetInsecureException() {

    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DracoonNetInsecureException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.<br>
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A <code>null</code> value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     */
    public DracoonNetInsecureException(String message, Throwable cause) {
        super(message, cause);
    }

}
