package com.dracoon.sdk.error;

import com.dracoon.sdk.internal.DracoonConstants;

/**
 * Enumeration of Dracoon API error codes.
 */
@SuppressWarnings("unused")
public enum DracoonApiCode {

    API_NOT_FOUND(-1, "API could not be found. (Wrong server URL?)"),
    API_VERSION_NOT_SUPPORTED(-2, "API versions < " + DracoonConstants.API_MIN_VERSION +
            " are not supported."),

    // General
    AUTH_UNKNOWN_ERROR(-1000, "An authentication/authorization error occurred."),
    // OAuth
    AUTH_OAUTH_CLIENT_UNKNOWN(-1100, "OAuth client is unknown."),
    AUTH_OAUTH_CLIENT_UNAUTHORIZED(-1101, "OAuth client is unauthorized."),
    AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED(-1102, "OAuth grant type is not allowed."),
    AUTH_OAUTH_AUTHORIZATION_REQUEST_INVALID(-1103, "OAuth authorization request is invalid."),
    AUTH_OAUTH_AUTHORIZATION_SCOPE_INVALID(-1104, "OAuth scope is invalid."),
    AUTH_OAUTH_AUTHORIZATION_ACCESS_DENIED(-1105, "OAuth access was denied."),
    AUTH_OAUTH_TOKEN_REQUEST_INVALID(-1106, "OAuth token request is invalid."),
    AUTH_OAUTH_TOKEN_CODE_INVALID(-1107, "OAuth authorization code is invalid."),
    AUTH_OAUTH_REFRESH_REQUEST_INVALID(-1108, "OAuth token refresh request is invalid."),
    AUTH_OAUTH_REFRESH_TOKEN_INVALID(-1109, "OAuth refresh token is invalid."),
    AUTH_OAUTH_REVOKE_REQUEST_INVALID(-1110, "OAuth token revoke request is invalid."),
    AUTH_OAUTH_CLIENT_NO_PERMISSION(-1150, "OAuth client has no permissions to execute the " +
            "action."),
    // Authorization
    AUTH_UNAUTHORIZED(-1200, "Unauthorized."),
    // User
    AUTH_USER_TEMPORARY_LOCKED(-1300, "User is temporary locked."),
    AUTH_USER_LOCKED(-1301, "User is locked."),
    AUTH_USER_EXPIRED(-1302, "User is expired."),

    PRECONDITION_UNKNOWN_ERROR(-2000, "A precondition is not fulfilled."),
    PRECONDITION_MUST_ACCEPT_EULA(-2101, "User must accept EULA."),
    PRECONDITION_MUST_CHANGE_USER_NAME(-2102, "User must change his user name."),
    PRECONDITION_MUST_CHANGE_PASSWORD(-2103, "User must change his password."),

    // General
    VALIDATION_UNKNOWN_ERROR(-3000, "The server request was invalid."),
    VALIDATION_FIELD_CAN_NOT_BE_EMPTY(-3001, "Mandatory fields cannot be empty."),
    VALIDATION_FIELD_NOT_ZERO_POSITIVE(-3002, "Field value must be zero or positive."),
    VALIDATION_FIELD_NOT_POSITIVE(-3003, "Field value must be positive."),
    VALIDATION_FIELD_MAX_LENGTH_EXCEEDED(-3004, "Field length exceeded."),
    VALIDATION_FIELD_NOT_BETWEEN_0_10(-3005, "Field value must be between 0 and 10."),
    VALIDATION_FIELD_NOT_BETWEEN_0_9999(-3006, "Field value must be between 0 and 9999."),
    VALIDATION_FIELD_NOT_BETWEEN_1_9999(-3007, "Field value must be between 1 and 9999."),
    VALIDATION_INVALID_OFFSET_OR_LIMIT(-3008, "Invalid offset or limit."),
    VALIDATION_INVALID_KEY(-3009, "Invalid key."),
    // Nodes
    VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE(-3100, "A file can't be a target node."),
    VALIDATION_FILE_NAME_INVALID(-3101, "File name invalid."),
    VALIDATION_EXPIRATION_DATE_IN_PAST(-3102, "Expiration date is in past."),
    VALIDATION_EXPIRATION_DATE_TOO_LATE(-3103, "Expiration date is too late. Max year is limited " +
            "to 9999."),
    VALIDATION_NODE_ALREADY_EXISTS(-3104, "A node with the same name already exits."),
    VALIDATION_NODES_NOT_IN_SAME_PARENT(-3108, "Folders/files must be in same parent."),
    VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME(-3109, "A node can\'t be copied to " +
            "its own place without renaming."),
    VALIDATION_CAN_NOT_MOVE_NODE_TO_OWN_PLACE(-3110, "A node can\'t be moved to its own place."),
    VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER(-3111, "A room or folder can\'t be overwritten."),
    VALIDATION_CAN_NOT_COPY_TO_CHILD(-3112, "A node can\'t be copied to its child node."),
    VALIDATION_CAN_NOT_MOVE_TO_CHILD(-3113, "A node can\'t be moved to its child node."),
    VALIDATION_CAN_NOT_COPY_ROOM(-3114, "Rooms can\'t be copied."),
    VALIDATION_CAN_NOT_MOVE_ROOM(-3115, "Rooms can\'t be moved."),
    VALIDATION_PATH_TOO_LONG(-3116, "Path is too long."),
    VALIDATION_NODE_IS_NO_FAVORITE(-3117, "Node is not marked as favorite."),
    VALIDATION_ROOM_NOT_ENCRYPTED(-3118, "Room not encrypted."),
    VALIDATION_SOURCE_ROOM_ENCRYPTED(-3119, "Encrypted files can\'t be copied or moved to an not " +
            "encrypted room."),
    VALIDATION_TARGET_ROOM_ENCRYPTED(-3120, "Not encrypted files can\'t be copied or moved to an " +
            "encrypted room."),
    // Shares
    VALIDATION_DL_SHARE_CAN_NOT_CREATE_ON_ENCRYPTED_ROOM_FOLDER(-3200, "A download share can\'t " +
            "be created on a encrypted room or folder."),
    VALIDATION_UL_SHARE_NAME_ALREADY_EXISTS(-3201, "Upload share name already exits."),
    // Customers
    // Users
    VALIDATION_USER_HAS_NO_KEY_PAIR(-3550, "User has no encryption key pair."),
    VALIDATION_USER_KEY_PAIR_INVALID(-3551, "Encryption key pair invalid."),
    VALIDATION_USER_HAS_NO_FILE_KEY(-3552, "User has no encryption file key."),
    // Groups
    // Other
    VALIDATION_PASSWORD_NOT_SECURE(-3800, "Password is not secure."),
    VALIDATION_EMAIL_ADDRESS_INVALID(-3801, "Email address invalid."),

    PERMISSION_UNKNOWN_ERROR(-4000, "User has no permissions to execute the action in this room."),
    PERMISSION_MANAGE_ERROR(-4100, "User has no permission to manage this room."),
    PERMISSION_READ_ERROR(-4101, "User has no permission to read nodes in this room."),
    PERMISSION_CREATE_ERROR(-4102, "User has no permission to create nodes in this room."),
    PERMISSION_UPDATE_ERROR(-4103, "User has no permission to change nodes in this room."),
    PERMISSION_DELETE_ERROR(-4104, "User has no permission to change nodes in this room."),
    PERMISSION_MANAGE_DL_SHARES_ERROR(-4105, "User has no permission to manage download shares " +
            "in this room."),
    PERMISSION_MANAGE_UL_SHARES_ERROR(-4106, "User has no permission to manage upload shares in " +
            "this room."),
    PERMISSION_READ_RECYCLE_BIN_ERROR(-4107, "User has no permission to read recycle bin in this " +
            "room."),
    PERMISSION_RESTORE_RECYCLE_BIN_ERROR(-4108, "User has no permission to restore recycle bin " +
            "items in this room."),
    PERMISSION_DELETE_RECYCLE_BIN_ERROR(-4109, "User has no permission to delete recycle bin " +
            "items in this room."),

    // General
    SERVER_UNKNOWN_ERROR(-5000, "A server error occurred."),
    SERVER_MALICIOUS_FILE_DETECTED(-5090, "Malicious file detected."),
    // Nodes
    SERVER_NODE_NOT_FOUND(-5100, "Requested room/folder/file was not found."),
    SERVER_ROOM_NOT_FOUND(-5101, "Requested room was not found."),
    SERVER_FOLDER_NOT_FOUND(-5102, "Requested folder was not found."),
    SERVER_FILE_NOT_FOUND(-5103, "Requested file was not found."),
    SERVER_SOURCE_NODE_NOT_FOUND(-5104, "Target room or folder was not found."),
    SERVER_TARGET_NODE_NOT_FOUND(-5105, "Target room or folder was not found."),
    SERVER_TARGET_ROOM_NOT_FOUND(-5106, "Target room was not found."),
    SERVER_INSUFFICIENT_STORAGE(-5107, "Not enough free storage on the server."),
    SERVER_INSUFFICIENT_CUSTOMER_QUOTA(-5108, "Not enough quota for the customer."),
    SERVER_INSUFFICIENT_ROOM_QUOTA(-5109, "Not enough quota for the room."),
    SERVER_INSUFFICIENT_UL_SHARE_QUOTA(-5110, "Not enough quota for the upload share."),
    SERVER_S3_COMMUNICATION_FAILED(-5120, "S3 communication failed."),
    // Shares
    SERVER_DL_SHARE_NOT_FOUND(5200, "Download share could not be found."),
    SERVER_UL_SHARE_NOT_FOUND(5201, "Upload share could not be found."),
    // Customers
    SERVER_CUSTOMER_NOT_FOUND(-5400, "Customer could not be found."),
    // Users
    SERVER_USER_NOT_FOUND(-5500, "User could not be found."),
    SERVER_USER_KEY_PAIR_NOT_FOUND(-5550, "Encryption key pair was not found."),
    SERVER_USER_KEY_PAIR_ALREADY_SET(-5551, "Encryption key pair was already set."),
    SERVER_USER_FILE_KEY_NOT_FOUND(-5552, "Encryption file key was not found."),
    // Groups
    SERVER_GROUP_NOT_FOUND(-5600, "Group could not be found."),
    // Config
    SERVER_SMS_IS_DISABLED(-5800, "SMS sending is disabled."),
    SERVER_SMS_COULD_NOT_BE_SEND(-5801, "SMS could not be send.");

    private final int mNumber;
    private final String mText;

    /**
     * Constructs a new enumeration constant with the provided error number and message.
     *
     * @param number The error number.
     * @param text   The error message.
     */
    DracoonApiCode(int number, String text) {
        mNumber = number;
        mText = text;
    }

    /**
     * Returns the error number of the enumeration constant.<br>
     * <br>
     * This number can be used to map localized error messages.
     *
     * @return the error number of this enum constant
     */
    public int getNumber() {
        return mNumber;
    }

    /**
     * Returns the error message of the enumeration constant.
     *
     * @return the error message of this enum constant
     */
    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mNumber + " " + mText;
    }

    /**
     * Finds a enumeration constant by a provided error number.
     *
     * @param number The error number of the constant to return.
     *
     * @return the appropriate enumeration constant
     *
     * @throws IllegalArgumentException if no enumeration constant could be found
     */
    public static DracoonApiCode valueOf(int number) {
        for (DracoonApiCode code : values()) {
            if (code.mNumber == number) {
                return code;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + number + "]");
    }

    /**
     * Checks if the error is a authorization error.
     *
     * @return <code>true</code> if error is a authorization error; <code>false</code> otherwise
     */
    public boolean isAuthError() {
        return mNumber <= -1000 && mNumber > -2000;
    }

    /**
     * Checks if the error is a precondition error.
     *
     * @return <code>true</code> if error is a precondition error; <code>false</code> otherwise
     */
    public boolean isPreconditionError() {
        return mNumber <= -2000 && mNumber > -3000;
    }

    /**
     * Checks if the error is a validation error.
     *
     * @return <code>true</code> if error is a validation error; <code>false</code> otherwise
     */
    public boolean isValidationError() {
        return mNumber <= -3000 && mNumber > -4000;
    }

    /**
     * Checks if the error is a permission error.
     *
     * @return <code>true</code> if error is a permission error; <code>false</code> otherwise
     */
    public boolean isPermissionError() {
        return mNumber <= -4000 && mNumber > -5000;
    }

    /**
     * Checks if the error is a server error.
     *
     * @return <code>true</code> if error is a server error; <code>false</code> otherwise
     */
    public boolean isServerError() {
        return mNumber <= -5000 && mNumber > -6000;
    }

}
