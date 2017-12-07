package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Response;

import java.io.IOException;

public class DracoonErrorParser {

    private static final String LOG_TAG = DracoonErrorParser.class.getSimpleName();

    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    private Log mLog;

    public DracoonErrorParser(Log log) {
        mLog = log;
    }

    // --- Methods to parse Retrofit error responses ---

    public DracoonApiCode parseStandardError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = errorResponse.errorCode != null ? errorResponse.errorCode : 0;

        return parseStandardError(statusCode, errorCode);
    }

    public DracoonApiCode parseUserKeyPairSetError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -70022 || errorCode == -70023)
                    return DracoonApiCode.VALIDATION_INVALID_USER_KEY_PAIR;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case CONFLICT:
                if (errorCode == -70021)
                    return DracoonApiCode.SERVER_USER_KEY_PAIR_ALREADY_SET;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseUserKeyPairQueryError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseUserKeyPairDeleteError(Response response) {
        return parseUserKeyPairQueryError(response);
    }

    public DracoonApiCode parseNodesQueryError(Response response) {
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

    public DracoonApiCode parseRoomCreateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40755)
                    return DracoonApiCode.VALIDATION_BAD_FILE_NAME;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_ROOM_NOT_FOUND;
                else if (errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_ROOM_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseRoomUpdateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40755)
                    return DracoonApiCode.VALIDATION_BAD_FILE_NAME;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_ROOM_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_ROOM_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFolderCreateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_FOLDER_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFolderUpdateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_FOLDER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_FOLDER_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileUpdateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40755)
                    return DracoonApiCode.VALIDATION_BAD_FILE_NAME;
                else if (errorCode == -40756)
                    return DracoonApiCode.VALIDATION_INVALID_FILE_CLASSIFICATION;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_FILE_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseNodesDeleteError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -41054)
                    return DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_NODE_NOT_FOUND;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseCreateFileUploadError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -80001)
                    return DracoonApiCode.VALIDATION_INVALID_TARGET_NODE;
                else if (errorCode == -40755)
                    return DracoonApiCode.VALIDATION_BAD_FILE_NAME;
                else if (errorCode == -40756)
                    return DracoonApiCode.VALIDATION_INVALID_FILE_CLASSIFICATION;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            case INSUFFICIENT_STORAGE:
                return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileUploadError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -80021)
                    return DracoonApiCode.SERVER_UPLOAD_SEGMENT_INVALID;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case INSUFFICIENT_STORAGE:
                return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseCompleteFileUploadError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40763)
                    return DracoonApiCode.VALIDATION_FILE_KEY_MISSING;
                else
                    return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
            case CONFLICT:
                if (errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_CONTAINER_NODE;
                else
                    return DracoonApiCode.VALIDATION_FILE_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseDownloadTokenError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileKeyQueryError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                if (errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (errorCode == -40761)
                    return DracoonApiCode.SERVER_FILE_KEY_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    private DracoonApiCode parseStandardError(int statusCode, int errorCode) {
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

    private ApiErrorResponse getErrorResponse(Response response) {
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
                mLog.d(LOG_TAG, er.toString());
            }

            return er;
        } catch (IOException e) {
            return null;
        }
    }

    // --- Methods to parse OkHttp error responses ---

    public DracoonApiCode parseDownloadError(okhttp3.Response response) {
        switch (HttpStatus.valueOf(response.code())) {
            case UNAUTHORIZED:
                return DracoonApiCode.AUTH_UNAUTHORIZED;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_FILE_NOT_FOUND;
            case RANGE_NOT_SATISFIABLE:
                return DracoonApiCode.SERVER_DOWNLOAD_SEGMENT_INVALID;
            default:
                return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }
    }

}
