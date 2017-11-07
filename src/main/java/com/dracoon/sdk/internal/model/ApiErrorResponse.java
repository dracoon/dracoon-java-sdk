package com.dracoon.sdk.internal.model;

public class ApiErrorResponse {

    public Integer code;
    public String message;
    public String debugInfo;
    public Integer errorCode;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code=" + code + ", " +
                "message='" + message + "', " +
                "debugInfo='" + debugInfo + "', " +
                "errorCode=" + errorCode +
                '}';
    }

}
