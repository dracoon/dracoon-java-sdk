package com.dracoon.sdk.error;

public class DracoonNetIOException extends DracoonException {

    private static final long serialVersionUID = -1497143863572050306L;

    public DracoonNetIOException() {

    }

    public DracoonNetIOException(String message) {
        super(message);
    }

    public DracoonNetIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
