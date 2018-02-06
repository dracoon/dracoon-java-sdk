package com.dracoon.sdk.error;

/**
 * Signals a interrupted network connection.
 */
public class DracoonNetIOInterruptedException extends DracoonNetIOException {

    private static final long serialVersionUID = 4537171225954265678L;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DracoonNetIOInterruptedException() {

    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DracoonNetIOInterruptedException(String message) {
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
    public DracoonNetIOInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

}
