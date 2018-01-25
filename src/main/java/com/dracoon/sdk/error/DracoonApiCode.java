package com.dracoon.sdk.error;

@SuppressWarnings("unused")
public enum DracoonApiCode {

    AUTH_UNKNOWN_ERROR(-100, "An authentication error occurred."),
    AUTH_UNAUTHORIZED(-101, "Unauthorized."),
    AUTH_USER_TEMPORARY_LOCKED(-102, "User is temporary locked."),
    AUTH_USER_LOCKED(-103, "User is locked."),
    AUTH_USER_EXPIRED(-104, "User is expired."),

    PRECONDITION_UNKNOWN_ERROR(-120, "A precondition is not fulfilled."),
    PRECONDITION_MUST_ACCEPT_EULA(-121, "User must accept EULA"),
    PRECONDITION_MUST_CHANGE_USER_NAME(-122, "User must change his username."),
    PRECONDITION_MUST_CHANGE_PASSWORD(-123, "User must change his password."),

    VALIDATION_UNKNOWN_ERROR(-130, "The server request was invalid."),
    VALIDATION_NOT_A_ROOM(-131, "Not a room."),
    VALIDATION_NOT_A_FOLDER(-132, "Not a folder."),
    VALIDATION_NOT_A_FILE(-133, "Not a file."),
    VALIDATION_INVALID_TARGET_NODE(-134, "Invalid target room or folder."),
    VALIDATION_BAD_FILE_NAME(-135, "Bad file name."),
    VALIDATION_INVALID_FILE_CLASSIFICATION(-136, "Invalid file classification."),
    VALIDATION_EXPIRATION_DATE_IN_PAST(-137, "Expiration date is in past."),
    VALIDATION_EXPIRATION_DATE_TOO_LATE(-138, "Expiration date is too late."),
    VALIDATION_FILE_KEY_MISSING(-139, "File key has to be provided."),
    VALIDATION_FOLDER_FILE_ALREADY_EXISTS(-140, "A folder/file with the same name already exits."),
    VALIDATION_ROOM_ALREADY_EXISTS(-141, "A room with the same name already exits."),
    VALIDATION_FOLDER_ALREADY_EXISTS(-142, "A folder with the same name already exits."),
    VALIDATION_FILE_ALREADY_EXISTS(-143, "A file with the same name already exits."),
    VALIDATION_CAN_NOT_OVERWRITE_CONTAINER_NODE(-144, "A room or folder can\'t be overwritten."),
    VALIDATION_NODES_NOT_IN_SAME_PARENT(-145, "Folders/files must be in same parent."),
    VALIDATION_PATH_TOO_LONG(-146, "Path is too long."),
    VALIDATION_INVALID_USER_KEY_PAIR(-147, "Invalid encryption key pair."),
    VALIDATION_ROOM_NOT_ENCRYPTED(-148, "Room not encrypted."),
    VALIDATION_SOURCE_ROOM_NOT_ENCRYPTED(-149, "Encrypted files can\'t be copied or moved to an not encrypted room."),
    VALIDATION_TARGET_ROOM_NOT_ENCRYPTED(-150, "Not encrypted files can\'t be copied or moved to an encrypted room."),

    PERMISSION_ERROR(-160, "User has no permissions to execute the action in this room."),
    PERMISSION_SHARE_ERROR(-161, "User has no permission to share files and folders from this room."),
    PERMISSION_READ_ERROR(-162, "User has no permission to read files and folders in this room."),
    PERMISSION_CREATE_ERROR(-163, "User has no permission to create files and folders in this room."),
    PERMISSION_UPDATE_ERROR(-164, "User has no permission to change files and folders in this room."),

    SERVER_UNKNOWN_ERROR(-200, "A server error occurred."),
    SERVER_NODE_NOT_FOUND(-201, "Requested room/folder/file was not found."),
    SERVER_FOLDER_FILE_NOT_FOUND(-202, "Requested folder/file was not found."),
    SERVER_ROOM_NOT_FOUND(-203, "Requested room was not found."),
    SERVER_FOLDER_NOT_FOUND(-204, "Requested folder was not found."),
    SERVER_FILE_NOT_FOUND(-205, "Requested file was not found."),
    SERVER_TARGET_NODE_NOT_FOUND(-206, "Target room or folder was not found."),
    SERVER_TARGET_ROOM_NOT_FOUND(-207, "Target room was not found."),
    SERVER_TARGET_FOLDER_NOT_FOUND(-208, "Target folder was not found."),
    SERVER_FILE_KEY_NOT_FOUND(-209, "File key could not be found."),
    SERVER_UPLOAD_SEGMENT_INVALID(-210, "Provided file segment is invalid."),
    SERVER_DOWNLOAD_SEGMENT_INVALID(-211, "Requested file segment is invalid."),
    SERVER_INSUFFICIENT_STORAGE(-212, "Not enough free storage on the server."),
    SERVER_USER_NOT_FOUND(-213, "User could not be found."),
    SERVER_USER_ALREADY_APPROVED_EULA(-214, "User already approved EULA."),
    SERVER_USER_CAN_NOT_CHANGE_NAME(-215, "User is not allowed to change his user name."),
    SERVER_USER_ALREADY_EXISTS(-216, "A user with this user name already exists."),
    SERVER_USER_CAN_NOT_CHANGE_PASSWORD(-217, "User is not allowed to change his password."),
    SERVER_USER_CAN_NOT_RESET_PASSWORD(-218, "Password can\'t be reset for this user."),
    SERVER_USER_RESET_PASSWORD_EVERY_5_MINUTES(-219, "Password can only be reset every 5 minutes."),
    SERVER_USER_KEY_PAIR_ALREADY_SET(-220, "Encryption key pair was already set."),
    SERVER_USER_KEY_PAIR_NOT_FOUND(-221, "Encryption key pair was not found.");

    private final int mNumber;
    private final String mText;

    DracoonApiCode(int number, String text) {
        mNumber = number;
        mText = text;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mNumber + " " + mText;
    }

    public static DracoonApiCode valueOf(int dracoonApiCode) {
        for (DracoonApiCode code : values()) {
            if (code.mNumber == dracoonApiCode) {
                return code;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + dracoonApiCode + "]");
    }

}
