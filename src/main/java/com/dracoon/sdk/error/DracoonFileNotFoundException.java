package com.dracoon.sdk.error;

public class DracoonFileNotFoundException extends DracoonException {

    private static final long serialVersionUID = -8826376459563710187L;

    public DracoonFileNotFoundException() {

    }

    public DracoonFileNotFoundException(String message) {
        super(message);
    }

    public DracoonFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
