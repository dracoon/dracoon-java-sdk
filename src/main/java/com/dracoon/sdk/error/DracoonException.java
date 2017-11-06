package com.dracoon.sdk.error;

public class DracoonException extends Exception {

    private static final long serialVersionUID = 5642720416433996541L;

    public DracoonException() {

    }

    public DracoonException(String message) {
        super(message);
    }

    public DracoonException(String message, Throwable cause) {
        super(message, cause);
    }

}
