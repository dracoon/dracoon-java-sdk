package com.dracoon.sdk.internal.http;

import java.io.IOException;

public class InterceptedIOException extends IOException {

    public InterceptedIOException(Throwable cause) {
        super(cause);
    }

}
