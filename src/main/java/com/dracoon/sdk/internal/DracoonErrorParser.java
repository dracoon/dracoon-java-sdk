package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@SuppressWarnings({"Duplicates", "rawtypes"})
public class DracoonErrorParser {

    private static final String LOG_TAG = DracoonErrorParser.class.getSimpleName();

    private static final String HEADER_X_FORBIDDEN = "X-Forbidden";

    private static final GsonBuilder sGsonBuilder = new GsonBuilder();

    private static class Error {
        int statusCode;
        int errorCode;
    }

    private Log mLog = new NullLog();

    public DracoonErrorParser() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    // --- Methods to parse Retrofit error responses ---

    public DracoonApiCode parseStandardError(Response response) {
        Error error = getError(response);
        return parseStandardError(error.statusCode, error.errorCode);
    }

    public DracoonApiCode parseServerInfoQueryError(Response response) {
        if (HttpStatus.valueOf(response.code()) == HttpStatus.NOT_FOUND) {
            return DracoonApiCode.API_NOT_FOUND;
        }

        return parseStandardError(response);
    }

    public DracoonApiCode parseUserKeyPairSetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -70022 || error.errorCode == -70023)
                    return DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID;
                else
                    return parseValidationError(error.errorCode);
            case CONFLICT:
                if (error.errorCode == -70021)
                    return DracoonApiCode.SERVER_USER_KEY_PAIR_ALREADY_SET;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUserKeyPairsQueryError(Response response) {
        Error error = getError(response);
        return parseStandardError(error.statusCode, error.errorCode);
    }

    public DracoonApiCode parseUserKeyPairQueryError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUserKeyPairDeleteError(Response response) {
        return parseUserKeyPairQueryError(response);
    }

    public DracoonApiCode parseUserProfileAttributesSetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -80023)
                    return DracoonApiCode.VALIDATION_INVALID_KEY;
                else
                    return parseValidationError(error.errorCode);
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUserProfileAttributesQueryError(Response response) {
        Error error = getError(response);
        return parseStandardError(error.statusCode, error.errorCode);
    }

    public DracoonApiCode parseUserProfileAttributeDeleteError(Response response) {
        return parseUserProfileAttributesSetError(response);
    }

    public DracoonApiCode parseUserAvatarSetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -80042 || error.errorCode == -80043 || error.errorCode ==
                        -80044)
                    return DracoonApiCode.VALIDATION_INVALID_IMAGE;
                else
                    return parseValidationError(error.errorCode);
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUserAvatarDeleteError(Response response) {
        return parseStandardError(response);
    }

    public DracoonApiCode parseNodesQueryError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_READ_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseRoomCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40755)
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND;
                else if (error.errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseRoomUpdateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40755)
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_ROOM_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFolderCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFolderUpdateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_FOLDER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFileUpdateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40755)
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (error.errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (error.errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodesDeleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_DELETE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_NODE_NOT_FOUND;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodesCopyError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40001)
                    return DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED;
                else if (error.errorCode == -40002)
                    return DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED;
                else if (error.errorCode == -41052)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_ROOM;
                else if (error.errorCode == -41053)
                    return DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE;
                else if (error.errorCode == -41054)
                    return DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (error.errorCode == -41302 || error.errorCode == -41303)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40014)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (error.errorCode == -41050)
                    return DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND;
                else if (error.errorCode == -41051)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (error.errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else if (error.errorCode == -41001)
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
                else if (error.errorCode == -41304)
                    return DracoonApiCode.VALIDATION_CAN_NOT_COPY_TO_CHILD;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodesMoveError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40001)
                    return DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED;
                else if (error.errorCode == -40002)
                    return DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED;
                else if (error.errorCode == -41052)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_ROOM;
                else if (error.errorCode == -41053)
                    return DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE;
                else if (error.errorCode == -41054)
                    return DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (error.errorCode == -41302)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_NODE_TO_OWN_PLACE;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_UPDATE_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40014)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (error.errorCode == -41050)
                    return DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND;
                else if (error.errorCode == -41051)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (error.errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else if (error.errorCode == -41001)
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
                else if (error.errorCode == -41304)
                    return DracoonApiCode.VALIDATION_CAN_NOT_MOVE_TO_CHILD;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40755)
                    return DracoonApiCode.VALIDATION_FILE_NAME_INVALID;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (error.errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (error.errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_CREATE_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
            case GATEWAY_TIMEOUT:
                if (error.errorCode == -90027)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case INSUFFICIENT_STORAGE:
                if (error.errorCode == -40200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA;
                else if (error.errorCode == -50504)
                    return DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA;
                else if (error.errorCode == -90200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA;
                else
                    return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case FORBIDDEN:
                String avHeader = response.headers().get(HEADER_X_FORBIDDEN);
                if (avHeader != null && avHeader.equals("403"))
                    return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case INSUFFICIENT_STORAGE:
                if (error.errorCode == -40200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA;
                else if (error.errorCode == -50504)
                    return DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA;
                else if (error.errorCode == -90200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA;
                else
                    return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            case MALICIOUS_FILE_DETECTED:
                return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadCompleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (error.errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseS3UploadGetUrlsError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -20501 || error.errorCode == -90034)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case GATEWAY_TIMEOUT:
                if (error.errorCode == -90027)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case INSUFFICIENT_STORAGE:
                if (error.errorCode == -40200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA;
                else if (error.errorCode == -90200)
                    return DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA;
                else
                    return DracoonApiCode.SERVER_INSUFFICIENT_STORAGE;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseS3UploadCompleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -20501 || error.errorCode == -90034)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (error.errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            case GATEWAY_TIMEOUT:
                if (error.errorCode == -90027)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseS3UploadStatusError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -20501 || error.errorCode == -90034)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseDownloadTokenGetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_FILE_NOT_FOUND;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseDownloadShareCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -10002)
                    return DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE;
                else if (error.errorCode == -50004)
                    return DracoonApiCode
                            .VALIDATION_DL_SHARE_CAN_NOT_CREATE_ON_ENCRYPTED_ROOM_FOLDER;
                else if (error.errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (error.errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else if (error.errorCode == -80009)
                    return DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID;
                else if (error.errorCode == -80030)
                    return DracoonApiCode.SERVER_SMS_IS_DISABLED;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case BAD_GATEWAY:
                if (error.errorCode == -90090)
                    return DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseDownloadSharesGetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseDownloadShareDeleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else if (error.errorCode == -60000)
                    return DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadShareCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -10002)
                    return DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE;
                else if (error.errorCode == -41200)
                    return DracoonApiCode.VALIDATION_PATH_TOO_LONG;
                else if (error.errorCode == -80006)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST;
                else if (error.errorCode == -80008)
                    return DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE;
                else if (error.errorCode == -80009)
                    return DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID;
                else if (error.errorCode == -80030)
                    return DracoonApiCode.SERVER_SMS_IS_DISABLED;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.PERMISSION_MANAGE_UL_SHARES_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                return DracoonApiCode.VALIDATION_UL_SHARE_NAME_ALREADY_EXISTS;
            case BAD_GATEWAY:
                if (error.errorCode == -90090)
                    return DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadSharesGetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseUploadShareDeleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -40000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else if (error.errorCode == -60500)
                    return DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFileKeyQueryError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case NOT_FOUND:
                if (error.errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (error.errorCode == -40761)
                    return DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseMissingFileKeysQueryError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40001)
                    return DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED;
                else
                    return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -40000)
                    return DracoonApiCode.SERVER_ROOM_NOT_FOUND;
                else if (error.errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (error.errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFileKeysSetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -40001)
                    return DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED;
                else
                    return parseValidationError(error.errorCode);
            case FORBIDDEN:
                if (error.errorCode == -40761)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY;
                else if (error.errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case NOT_FOUND:
                if (error.errorCode == -40751)
                    return DracoonApiCode.SERVER_FILE_NOT_FOUND;
                else if (error.errorCode == -70501)
                    return DracoonApiCode.SERVER_USER_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseFavoriteMarkError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodeCommentsGetError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodeCommentCreateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -80023)
                    return DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS;
                else
                    return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -40000 || error.errorCode == -41000)
                    return DracoonApiCode.SERVER_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodeCommentUpdateError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -80023)
                    return DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS;
                else if (error.errorCode == -80039)
                    return DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED;
                else
                    return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -41400)
                    return DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    public DracoonApiCode parseNodeCommentDeleteError(Response response) {
        Error error = getError(response);

        switch (HttpStatus.valueOf(error.statusCode)) {
            case BAD_REQUEST:
                if (error.errorCode == -80039)
                    return DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED;
                else
                    return parseValidationError(error.errorCode);
            case NOT_FOUND:
                if (error.errorCode == -41400)
                    return DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(error.statusCode, error.errorCode);
        }
    }

    private DracoonApiCode parseStandardError(int statusCode, int errorCode) {
        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case PAYMENT_REQUIRED:
                return DracoonApiCode.PRECONDITION_PAYMENT_REQUIRED;
            case FORBIDDEN:
                if (errorCode == -10003 || errorCode == -10007)
                    return DracoonApiCode.AUTH_USER_LOCKED;
                else if (errorCode == -10004)
                    return DracoonApiCode.AUTH_USER_EXPIRED;
                else if (errorCode == -10005)
                    return DracoonApiCode.AUTH_USER_TEMPORARY_LOCKED;
                else if (errorCode == -70020)
                    return DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR;
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
        else if (errorCode == -80023)
            return DracoonApiCode.VALIDATION_FIELD_CONTAINS_INVALID_CHARACTERS;
        else if (errorCode == -80024)
            return DracoonApiCode.VALIDATION_INVALID_OFFSET_OR_LIMIT;
        else if (errorCode == -80035)
            return DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_10;
        else
            return DracoonApiCode.VALIDATION_UNKNOWN_ERROR;
    }

    private Error getError(Response response) {
        mLog.d(LOG_TAG, "Server API error: " + response.code());

        Error ei = new Error();
        ei.statusCode = response.code();

        ApiErrorResponse errorResponse = getApiErrorResponse(response.errorBody());
        if (errorResponse != null && errorResponse.errorCode != null) {
            ei.errorCode = errorResponse.errorCode;
        }

        return ei;
    }

    private ApiErrorResponse getApiErrorResponse(ResponseBody responseBody) {
        if (responseBody == null) {
            return null;
        }

        MediaType contentType = responseBody.contentType();

        if (contentType == null) {
            mLog.d(LOG_TAG, "Invalid server API error response!");
            try {
                mLog.d(LOG_TAG, responseBody.string());
            } catch (IOException e) {
                // Nothing to do here
            }
            return null;
        }

        ApiErrorResponse errorResponse = null;
        try {
            Gson gson = sGsonBuilder.create();
            switch (contentType.subtype()) {
                case "json":
                    errorResponse = gson.fromJson(responseBody.string(), ApiErrorResponse.class);
                    break;
                case "octet-stream":
                    errorResponse = gson.fromJson(responseBody.charStream(), ApiErrorResponse.class);
                    break;
                default:
            }
        } catch (IOException e) {
            // Nothing to do here
        }
        if (errorResponse != null) {
            mLog.d(LOG_TAG, "Server API error response:");
            mLog.d(LOG_TAG, errorResponse.toString());
        }

        return errorResponse;
    }

    // --- Methods to parse OkHttp error responses ---

    public DracoonApiCode parseS3UploadError(okhttp3.Response response) {
        int statusCode = response.code();

        mLog.d(LOG_TAG, "S3 error: " + statusCode);

        return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
    }

    public DracoonApiCode parseDownloadError(okhttp3.Response response) {
        int statusCode = response.code();

        mLog.d(LOG_TAG, "Server API error: " + statusCode);

        switch (HttpStatus.valueOf(statusCode)) {
            case UNAUTHORIZED:
                return DracoonApiCode.AUTH_UNAUTHORIZED;
            case FORBIDDEN:
                String avHeader = response.headers().get(HEADER_X_FORBIDDEN);
                if (avHeader != null && avHeader.equals("403"))
                    return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case NOT_FOUND:
                return DracoonApiCode.SERVER_FILE_NOT_FOUND;
            case MALICIOUS_FILE_DETECTED:
                return DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED;
            default:
                return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }
    }

    public DracoonApiCode parseAvatarDownloadError(okhttp3.Response response) {
        int statusCode = response.code();

        mLog.d(LOG_TAG, "Server API error: " + statusCode);

        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND:
                return DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND;
            default:
                return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }
    }

    // --- Methods to parse error response ---

    public DracoonApiCode parseS3UploadStatusError(ApiErrorResponse errorResponse) {
        if (errorResponse == null) {
            return DracoonApiCode.SERVER_UNKNOWN_ERROR;
        }

        int statusCode = errorResponse.code != null ? errorResponse.code : 0;
        int errorCode = errorResponse.errorCode != null ? errorResponse.errorCode : 0;

        switch (HttpStatus.valueOf(statusCode)) {
            case BAD_REQUEST:
                return parseValidationError(errorCode);
            case NOT_FOUND:
                if (errorCode == -40000 || errorCode == -41000)
                    return DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            case CONFLICT:
                if (errorCode == -40010)
                    return DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER;
                else
                    return DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS;
            case GATEWAY_TIMEOUT:
                if (errorCode == -90027)
                    return DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED;
                else
                    return DracoonApiCode.SERVER_UNKNOWN_ERROR;
            default:
                return parseStandardError(statusCode, errorCode);
        }
    }

}
