package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ApiErrorResponse {

    public Integer code;
    public String message;
    public String debugInfo;
    public Integer errorCode;
    public ApiErrorInfos errorInfos;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code=" + code + ", " +
                "message='" + message + "', " +
                "debugInfo='" + debugInfo + "', " +
                "errorCode=" + errorCode + ", " +
                "errorInfos=" + errorInfos +
                '}';
    }

}
