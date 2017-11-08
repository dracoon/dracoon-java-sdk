package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Response;

import java.io.IOException;

public class DracoonErrorParser {

    private static final String LOG_TAG = DracoonErrorParser.class.getSimpleName();

    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    public static DracoonApiCode parseStandardError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = errorResponse.errorCode != null ? errorResponse.errorCode : 0;

        return parseStandardError(statusCode, errorCode);
    }

    public static DracoonApiCode parseNodesQueryError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_READ_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    private static DracoonApiCode parseStandardError(int statusCode, int errorCode) {
        switch (HttpStatus.valueOf(statusCode)) {
            case FORBIDDEN:
                if (errorCode == -10003 || errorCode == -10007)
                    return DracoonApiCode.AUTH_USER_LOCKED;
                else if (errorCode == -10004)
                    return DracoonApiCode.AUTH_USER_EXPIRED;
                else if (errorCode == -10005)
                    return DracoonApiCode.AUTH_USER_TEMPORARY_LOCKED;
                else
                    return DracoonApiCode.PERMISSION_ERROR;
            case UNAUTHORIZED:
                return DracoonApiCode.AUTH_UNAUTHORIZED;
            case PRECONDITION_FAILED:
                if (errorCode == -10103)
                    return DracoonApiCode.PRECONDITION_MUST_ACCEPT_EULA;
                else if (errorCode == -10104)
                    return DracoonApiCode.PRECONDITION_MUST_CHANGE_PASSWORD;
                else if (errorCode == -10106)
                    return DracoonApiCode.PRECONDITION_MUST_CHANGE_USER_NAME;
                else
                    return DracoonApiCode.PRECONDITION_UNKNOWN_ERROR;
            default:
                return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }
    }

    private static ApiErrorResponse getErrorResponse(Response response) {
        if (response.errorBody() == null) {
            return null;
        }

        Gson gson = gsonBuilder.create();
        try {
            ApiErrorResponse er = null;
            switch (response.errorBody().contentType().subtype()) {
                case "json":
                    er = gson.fromJson(response.errorBody().string(), ApiErrorResponse.class);
                    break;
                case "octet-stream":
                    er = gson.fromJson(response.errorBody().charStream(), ApiErrorResponse.class);
                    break;
                default:
            }

            if (er != null) {
                Log.d(LOG_TAG, er.toString());
            }

            return er;
        } catch (IOException e) {
            return null;
        }
    }

}
