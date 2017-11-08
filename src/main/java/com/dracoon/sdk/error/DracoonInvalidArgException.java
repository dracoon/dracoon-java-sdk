package com.dracoon.sdk.error;

public class DracoonInvalidArgException extends DracoonException {

    private static final long serialVersionUID = -3714860039372129568L;

    public DracoonInvalidArgException() {

    }

    public DracoonInvalidArgException(String message) {
        super(message);
    }

    public DracoonInvalidArgException(String message, Throwable cause) {
        super(message, cause);
    }

}
