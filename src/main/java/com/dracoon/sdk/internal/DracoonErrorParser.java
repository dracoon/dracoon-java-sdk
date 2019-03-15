package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.ResponseBody;
import retrofit2.Response;

@SuppressWarnings("Duplicates")
public class DracoonErrorParser {

    private static final String LOG_TAG = DracoonErrorParser.class.getSimpleName();

    private static final GsonBuilder sGsonBuilder = new GsonBuilder();

    private Log mLog = new NullLog();

    public DracoonErrorParser() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
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

    public DracoonApiCode parseServerVersionError(Response response) {
        if (HttpStatus.valueOf(response.code()) == HttpStatus.NOT_FOUND) {
            return DracoonApiCode.API_NOT_FOUND;
        }

        return parseStandardError(response);
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
                    return DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID;
                else
                    return parseValidationError(errorCode);
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
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND;
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
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
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
                if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
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
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
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
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return parseValidationError(errorCode);
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
                return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_DELETE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_NODE_NOT_FOUND;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseNodesCopyError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40001)
                    return DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED;
                else if (errorCode == -40002)
                    return DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED;
                else if (errorCode == -41052)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_ROOM;
                else if (errorCode == -41053)
                    return DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE;
                else if (errorCode == -41054)
                    return DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (errorCode == -41302 || errorCode == -41303)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40014)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (errorCode == -41050)
                    return DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND;
                else if (errorCode == -41051)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else if (errorCode == -41001)
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
                else if (errorCode == -41304)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_TO_CHILD;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseNodesMoveError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40001)
                    return DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED;
                else if (errorCode == -40002)
                    return DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED;
                else if (errorCode == -41052)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_ROOM;
                else if (errorCode == -41053)
                    return DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE;
                else if (errorCode == -41054)
                    return DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (errorCode == -41302)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_NODE_TO_OWN_PLACE;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (errorCode == -40014)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (errorCode == -41050)
                    return DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND;
                else if (errorCode == -41051)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else if (errorCode == -41001)
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
                else if (errorCode == -41304)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_TO_CHILD;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileUploadCreateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40755)
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            case INSUFFICIENT_STORAGE:
                if (errorCode == -40200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA;
                else if (errorCode == -50504)
                    return DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA;
                else if (errorCode == -90200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA;
                else
                    return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileUploadError(Response response) {
        int statusCode = response.code();
        if (HttpStatus.valueOf(statusCode) == HttpStatus.MALICIOUS_FILE_DETECTED) {
            return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
        }

        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case INSUFFICIENT_STORAGE:
                if (errorCode == -40200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA;
                else if (errorCode == -50504)
                    return DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA;
                else if (errorCode == -90200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA;
                else
                    return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileUploadCompleteError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case CONFLICT:
                if (errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else
                    return DracoonApiCode.VALIDATION_FILE_ALREADY_EXISTS;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseDownloadTokenGetError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_FILE_NOT_FOUND;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseDownloadShareCreateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -10002)
                    return DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE;
                else if (errorCode == -50004)
                    return DracoonApiCode
                            .VALIDATION_DL_SHARE_CAN_NOT_CREATE_ON_ENCRYPTED_ROOM_FOLDER;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else if (errorCode == -80009)
                    return DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID;
                else if (errorCode == -80030)
                    return DracoonApiCode.SERVER_SMS_IS_DISABLED;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR;
            case NOT_FOUND:
                if (errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case BAD_GATEWAY:
                if (errorCode == -90090)
                    return DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseDownloadSharesGetError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseDownloadShareDeleteError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case NOT_FOUND:
                if (errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else if (errorCode == -60000)
                    return DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseUploadShareCreateError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -10002)
                    return DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE;
                else if (errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else if (errorCode == -80009)
                    return DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID;
                else if (errorCode == -80030)
                    return DracoonApiCode.SERVER_SMS_IS_DISABLED;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                return DracoonApiCode.PERMISSION_MANAGE_UL_SHARES_ERROR;
            case NOT_FOUND:
                if (errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_UL_SHARE_NAME_ALREADY_EXISTS;
            case BAD_GATEWAY:
                if (errorCode == -90090)
                    return DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseUploadSharesGetError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseUploadShareDeleteError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else if (errorCode == -60500)
                    return DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
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
                    return DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseMissingFileKeysQueryError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40001)
                    return DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED;
                else
                    return parseValidationError(errorCode);
            case NOT_FOUND:
                if (errorCode == -40000)
                    return DracoonApiCode.SERVER_ROOM_NOT_FOUND;
                else if (errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFileKeysSetError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                if (errorCode == -40001)
                    return DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED;
                else
                    return parseValidationError(errorCode);
            case FORBIDDEN:
                if (errorCode == -40761)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case NOT_FOUND:
                if (errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    public DracoonApiCode parseFavoriteMarkError(Response response) {
        ApiErrorResponse errorResponse = getErrorResponse(response);
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = response.code();
        int errorCode = (errorResponse.errorCode != null) ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

    private DracoonApiCode parseStandardError(int statusCode, int errorCode) {
        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case FORBIDDEN:
                if (errorCode == -10003 || errorCode == -10007)
                    return DracoonApiCode.AUTH_USER_LOCKED;
                else if (errorCode == -10004)
                    return DracoonApiCode.AUTH_USER_EXPIRED;
                else if (errorCode == -10005)
                    return DracoonApiCode.AUTH_USER_TEMPORARY_LOCKED;
                else
                    return DracoonApiCode.PERMISSION_UNKNOWN_ERROR;
            case UNAUTHORIZED:
                if (errorCode == -10006)
                    return DracoonApiCode.AUTH_OAUTH_CLIENT_NO_PERMISSION;
                else
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

    private DracoonApiCode parseValidationError(int errorCode) {
        if (errorCode == -80000)
            return DracoonApiCode.VALIDATION_FIELD_CAN_NOT_BE_EMPTY;
        else if (errorCode == -80001)
            return DracoonApiCode.VALIDATION_FIELD_NOT_POSITIVE;
        else if (errorCode == -80003)
            return DracoonApiCode.VALIDATION_FIELD_NOT_ZERO_POSITIVE;
        else if (errorCode == -80007)
            return DracoonApiCode.VALIDATION_FIELD_MAX_LENGTH_EXCEEDED;
        else if (errorCode == -80018)
            return DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_9999;
        else if (errorCode == -80019)
            return DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_1_9999;
        else if (errorCode == -80024)
            return DracoonApiCode.VALIDATION_INVALID_OFFSET_OR_LIMIT;
        else if (errorCode == -80035)
            return DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_10;
        else
            return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
    }

    private ApiErrorResponse getErrorResponse(Response response) {
        if (response.errorBody() == null) {
            mLog.e(LOG_TAG, "Invalid server API error response!");
            mLog.e(LOG_TAG, String.format("(Server API HTTP code '%d'.)", response.code()));
            return null;
        }

        ResponseBody responseBody = response.errorBody();

        if (responseBody.contentType() == null || responseBody.contentType().subtype() == null) {
            mLog.e(LOG_TAG, "Invalid server API error response!");
            mLog.e(LOG_TAG, String.format("(Server API HTTP code '%d'.)", response.code()));
            try {
                mLog.d(LOG_TAG, responseBody.string());
            } catch (IOException e) {
                // Nothing to do here
            }
            return null;
        }

        Gson gson = sGsonBuilder.create();
        try {
            ApiErrorResponse er = null;
            switch (responseBody.contentType().subtype()) {
                case "json":
                    er = gson.fromJson(responseBody.string(), ApiErrorResponse.class);
                    break;
                case "octet-stream":
                    er = gson.fromJson(responseBody.charStream(), ApiErrorResponse.class);
                    break;
                default:
            }

            if (er != null) {
                mLog.d(LOG_TAG, "Server API error response:");
                mLog.d(LOG_TAG, er.toString());
            }

            return er;
        } catch (IOException e) {
            return null;
        }
    }

    // --- Methods to parse OkHttp error responses ---

    public DracoonApiCode parseDownloadError(okhttp3.Response response) {
        int statusCode = response.code();

        mLog.d(LOG_TAG, "Server HTTP error: " + statusCode);

        switch (HttpStatus.valueOf(statusCode)) {
            case UNAUTHORIZED:
                return DracoonApiCode.AUTH_UNAUTHORIZED;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_FILE_NOT_FOUND;
            case MALICIOUS_FILE_DETECTED:
                return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
            default:
                return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }
    }

}
