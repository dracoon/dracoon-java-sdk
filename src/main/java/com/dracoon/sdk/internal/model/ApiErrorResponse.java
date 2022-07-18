package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "WeakerAccess", // Weaker access is not possible (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
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
