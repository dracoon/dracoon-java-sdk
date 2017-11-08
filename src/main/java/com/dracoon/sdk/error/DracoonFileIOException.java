package com.dracoon.sdk.error;

public class DracoonFileIOException extends DracoonException {

    private static final long serialVersionUID = 2534557880347011715L;

    public DracoonFileIOException() {

    }

    public DracoonFileIOException(String message) {
        super(message);
    }

    public DracoonFileIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
