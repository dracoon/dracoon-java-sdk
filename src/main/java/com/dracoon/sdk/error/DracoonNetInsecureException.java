package com.dracoon.sdk.error;

public class DracoonNetInsecureException extends DracoonException {

    private static final long serialVersionUID = -7328327402956962496L;

    public DracoonNetInsecureException() {

    }

    public DracoonNetInsecureException(String message) {
        super(message);
    }

    public DracoonNetInsecureException(String message, Throwable cause) {
        super(message, cause);
    }

}
