package com.dracoon.sdk.error;

/**
 * Signals a file IO error.
 */
public class DracoonFileIOException extends DracoonException {

    private static final long serialVersionUID = 2534557880347011715L;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonFileIOException() {

    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DracoonFileIOException(String message) {
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
    public DracoonFileIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
