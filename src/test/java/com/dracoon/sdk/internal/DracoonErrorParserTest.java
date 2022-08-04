package com.dracoon.sdk.internal;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.model.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"rawtypes"})
class DracoonErrorParserTest {

    private static final GsonBuilder sGsonBuilder = new GsonBuilder();

    private final DracoonErrorParser mErrorParser = new DracoonErrorParser();

    // --- Retrofit error response parsing tests ---

    @SuppressWarnings("unused")
    private static Stream<Arguments> createBaseTestArguments() {
        return Stream.of(
                Arguments.of(400, -80000, DracoonApiCode.VALIDATION_FIELD_CAN_NOT_BE_EMPTY),
                Arguments.of(400, -80001, DracoonApiCode.VALIDATION_FIELD_NOT_POSITIVE),
                Arguments.of(400, -80003, DracoonApiCode.VALIDATION_FIELD_NOT_ZERO_POSITIVE),
                Arguments.of(400, -80007, DracoonApiCode.VALIDATION_FIELD_MAX_LENGTH_EXCEEDED),
                Arguments.of(400, -80018, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_9999),
                Arguments.of(400, -80019, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_1_9999),
                Arguments.of(400, -80023, DracoonApiCode.VALIDATION_FIELD_CONTAINS_INVALID_CHARACTERS),
                Arguments.of(400, -80024, DracoonApiCode.VALIDATION_INVALID_OFFSET_OR_LIMIT),
                Arguments.of(400, -80035, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_10),
                Arguments.of(400,      0, DracoonApiCode.VALIDATION_UNKNOWN_ERROR),
                Arguments.of(401, -10006, DracoonApiCode.AUTH_OAUTH_CLIENT_NO_PERMISSION),
                Arguments.of(401,      0, DracoonApiCode.AUTH_UNAUTHORIZED),
                Arguments.of(402,      0, DracoonApiCode.PRECONDITION_PAYMENT_REQUIRED),
                Arguments.of(403, -10003, DracoonApiCode.AUTH_USER_LOCKED),
                Arguments.of(403, -10007, DracoonApiCode.AUTH_USER_LOCKED),
                Arguments.of(403, -10004, DracoonApiCode.AUTH_USER_EXPIRED),
                Arguments.of(403, -10005, DracoonApiCode.AUTH_USER_TEMPORARY_LOCKED),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_UNKNOWN_ERROR),
                Arguments.of(412, -10103, DracoonApiCode.PRECONDITION_MUST_ACCEPT_EULA),
                Arguments.of(412, -10104, DracoonApiCode.PRECONDITION_MUST_CHANGE_PASSWORD),
                Arguments.of(412, -10106, DracoonApiCode.PRECONDITION_MUST_CHANGE_USER_NAME),
                Arguments.of(412,      0, DracoonApiCode.PRECONDITION_UNKNOWN_ERROR),
                Arguments.of(429,      0, DracoonApiCode.SERVER_TOO_MANY_REQUESTS),
                Arguments.of(500,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(500,   null, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    private static Stream<Arguments> createBaseTestArgumentsWithout403() {
        return createBaseTestArguments().filter(a -> {
            Object[] args = a.get();
            return args.length >= 1 && !args[0].equals(403);
        });
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseStandardError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseStandardError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseServerInfoQueryErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, null, DracoonApiCode.API_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseServerInfoQueryErrorArguments")
    void testParseServerInfoQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseServerInfoQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUserKeyPairSetErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(400, -70022, DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID),
                Arguments.of(400, -70023, DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID),
                Arguments.of(400,      0, DracoonApiCode.VALIDATION_UNKNOWN_ERROR),
                Arguments.of(409, -70021, DracoonApiCode.SERVER_USER_KEY_PAIR_ALREADY_SET),
                Arguments.of(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUserKeyPairSetErrorArguments")
    void testParseUserKeyPairSetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserKeyPairSetError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseUserKeyPairsQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserKeyPairsQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUserKeyPairQueryErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, 0, DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND),
                Arguments.of(409, 0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUserKeyPairQueryErrorArguments")
    void testParseUserKeyPairQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserKeyPairQueryError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseUserKeyPairDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserKeyPairDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUserProfileAttributesSetDeleteErrorArguments() {
        return modifyTestArguments(createBaseTestArguments(),
                Arguments.of(400, -80023, DracoonApiCode.VALIDATION_INVALID_KEY));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUserProfileAttributesSetDeleteErrorArguments")
    void testParseUserProfileAttributesSetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserProfileAttributesSetError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseUserProfileAttributesQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserProfileAttributesQueryError));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUserProfileAttributesSetDeleteErrorArguments")
    void testParseUserProfileAttributeDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserProfileAttributeDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUserAvatarSetErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(400, -80042, DracoonApiCode.VALIDATION_INVALID_IMAGE),
                Arguments.of(400, -80043, DracoonApiCode.VALIDATION_INVALID_IMAGE),
                Arguments.of(400, -80044, DracoonApiCode.VALIDATION_INVALID_IMAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUserAvatarSetErrorArguments")
    void testParseUserAvatarSetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserAvatarSetError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseUserAvatarDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUserAvatarDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesQueryErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_READ_ERROR),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesQueryErrorArguments")
    void testParseNodesQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseRoomCreateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND),
                Arguments.of(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseRoomCreateErrorArguments")
    void testParseRoomCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseRoomCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseRoomUpdateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseRoomUpdateErrorArguments")
    void testParseRoomUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseRoomUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFolderCreateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFolderCreateErrorArguments")
    void testParseFolderCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFolderCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFolderUpdateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_FOLDER_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_FOLDER_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFolderUpdateErrorArguments")
    void testParseFolderUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFolderUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileUpdateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                Arguments.of(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                Arguments.of(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileUpdateErrorArguments")
    void testParseFileUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesDeleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_DELETE_ERROR),
                Arguments.of(404,      0, DracoonApiCode.SERVER_NODE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesDeleteErrorArguments")
    void testParseNodesDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesCopyErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40001, DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED),
                Arguments.of(400, -40002, DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED),
                Arguments.of(400, -41052, DracoonApiCode.VALIDATION_CAN_NOT_COPY_ROOM),
                Arguments.of(400, -41053, DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE),
                Arguments.of(400, -41054, DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(400, -41302,
                        DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME),
                Arguments.of(400, -41303,
                        DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                Arguments.of(404, -40014, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                Arguments.of(404, -41050, DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND),
                Arguments.of(404, -41051, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                Arguments.of(409, -41001, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                Arguments.of(409, -41304, DracoonApiCode.VALIDATION_CAN_NOT_COPY_TO_CHILD),
                Arguments.of(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesCopyErrorArguments")
    void testParseNodesCopyError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesCopyError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesMoveErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40001, DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED),
                Arguments.of(400, -40002, DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED),
                Arguments.of(400, -41052, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_ROOM),
                Arguments.of(400, -41053, DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE),
                Arguments.of(400, -41054, DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(400, -41302, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_NODE_TO_OWN_PLACE),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                Arguments.of(404, -40014, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                Arguments.of(404, -41050, DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND),
                Arguments.of(404, -41051, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                Arguments.of(409, -41001, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                Arguments.of(409, -41304, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_TO_CHILD),
                Arguments.of(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesMoveErrorArguments")
    void testParseNodesMoveError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesMoveError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadCreateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                Arguments.of(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                Arguments.of(404,      0, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                Arguments.of(507, -50504, DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA),
                Arguments.of(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                Arguments.of(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadCreateErrorArguments")
    void testParseUploadCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(403,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                Arguments.of(507, -50504, DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA),
                Arguments.of(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                Arguments.of(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadErrorArguments")
    void testParseUploadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadCompleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadCompleteErrorArguments")
    void testParseUploadCompleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadCompleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadGetUrlsErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                Arguments.of(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                Arguments.of(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadGetUrlsErrorArguments")
    void testParseS3UploadGetUrlsError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadGetUrlsError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadCompleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                Arguments.of(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadCompleteErrorArguments")
    void testParseS3UploadCompleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadCompleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadStatusErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadStatusErrorArguments")
    void testParseS3UploadStatusError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadStatusError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadTokenGetErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, 0, DracoonApiCode.SERVER_FILE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadTokenGetErrorArguments")
    void testParseDownloadTokenGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadTokenGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadShareCreateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -10002, DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE),
                Arguments.of(400, -50004,
                        DracoonApiCode.VALIDATION_DL_SHARE_CAN_NOT_CREATE_ON_ENCRYPTED_ROOM_FOLDER),
                Arguments.of(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                Arguments.of(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                Arguments.of(400, -80009, DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID),
                Arguments.of(400, -80030, DracoonApiCode.SERVER_SMS_IS_DISABLED),
                Arguments.of(400, -80064, DracoonApiCode.VALIDATION_CLASSIFICATION_POLICY_VIOLATION),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(502, -90090, DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND),
                Arguments.of(502,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadShareCreateErrorArguments")
    void testParseDownloadShareCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadShareCreateError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseDownloadSharesGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadSharesGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadShareDeleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -60000, DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadShareDeleteErrorArguments")
    void testParseDownloadShareDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadShareDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadShareCreateErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -10002, DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE),
                Arguments.of(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                Arguments.of(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                Arguments.of(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                Arguments.of(400, -80009, DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID),
                Arguments.of(400, -80030, DracoonApiCode.SERVER_SMS_IS_DISABLED),
                Arguments.of(400, -80064, DracoonApiCode.VALIDATION_CLASSIFICATION_POLICY_VIOLATION),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.PERMISSION_MANAGE_UL_SHARES_ERROR),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_UL_SHARE_NAME_ALREADY_EXISTS),
                Arguments.of(502, -90090, DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND),
                Arguments.of(502,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadShareCreateErrorArguments")
    void testParseUploadShareCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadShareCreateError));
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseUploadSharesGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadSharesGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadShareDeleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -60500, DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadShareDeleteErrorArguments")
    void testParseUploadShareDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadShareDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileKeyQueryErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                Arguments.of(404, -40761, DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileKeyQueryErrorArguments")
    void testParseFileKeyQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileKeyQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseMissingFileKeysQueryErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(400, -40001, DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                Arguments.of(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                Arguments.of(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseMissingFileKeysQueryErrorArguments")
    void testParseMissingFileKeysQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseMissingFileKeysQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileKeysSetErrorArguments() {
        return extendTestArguments(
                createBaseTestArgumentsWithout403(),
                Arguments.of(400, -40001, DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED),
                Arguments.of(403, -40761, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                Arguments.of(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                Arguments.of(403,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                Arguments.of(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileKeysSetErrorArguments")
    void testParseFileKeysSetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileKeysSetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFavoriteMarkErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFavoriteMarkErrorArguments")
    void testParseFavoriteMarkError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFavoriteMarkError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentsGetErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentsGetErrorArguments")
    void testParseNodeCommentsGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentsGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentCreateErrorArguments() {
        return extendTestArguments(
                modifyTestArguments(createBaseTestArguments(), Arguments.of(400, -80023,
                        DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS)),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentCreateErrorArguments")
    void testParseNodeCommentCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentUpdateErrorArguments() {
        return extendTestArguments(
                modifyTestArguments(createBaseTestArguments(), Arguments.of(400, -80023,
                        DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS)),
                Arguments.of(400, -80039, DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED),
                Arguments.of(404, -41400, DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentUpdateErrorArguments")
    void testParseNodeCommentUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentDeleteErrorArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(400, -80039, DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED),
                Arguments.of(404, -41400, DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentDeleteErrorArguments")
    void testParseNodeCommentDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentDeleteError));
    }

    // --- OkHttp error response parsing tests ---

    @SuppressWarnings("unused")
    private static Stream<Arguments> createOkHttpBaseTestArguments() {
        return Stream.of(
                Arguments.of(401, null, DracoonApiCode.AUTH_UNAUTHORIZED),
                Arguments.of(402, null, DracoonApiCode.PRECONDITION_PAYMENT_REQUIRED),
                Arguments.of(429, null, DracoonApiCode.SERVER_TOO_MANY_REQUESTS),
                Arguments.of(500, null, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadErrorArguments() {
        return Stream.of(
                Arguments.of(404, null, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(500, null, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadErrorArguments")
    void testParseS3UploadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeOkHttpParseMethod(code, errorCode,
                mErrorParser::parseS3UploadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadErrorArguments() {
        return extendTestArguments(
                createOkHttpBaseTestArguments(),
                Arguments.of(403, null, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(404, null, DracoonApiCode.SERVER_FILE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadErrorArguments")
    void testParseDownloadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeOkHttpParseMethod(code, errorCode,
                mErrorParser::parseDownloadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseAvatarDownloadErrorArguments() {
        return extendTestArguments(
                createOkHttpBaseTestArguments(),
                Arguments.of(404, null, DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseAvatarDownloadErrorArguments")
    void testParseAvatarDownloadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeOkHttpParseMethod(code, errorCode,
                mErrorParser::parseAvatarDownloadError));
    }

    // --- Error model parsing tests ---

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadStatusErrorRawArguments() {
        return extendTestArguments(
                createBaseTestArguments(),
                Arguments.of(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                Arguments.of(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                Arguments.of(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                Arguments.of(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                Arguments.of(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                Arguments.of(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                Arguments.of(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadStatusErrorRawArguments")
    void testParseS3UploadStatusErrorRaw(int code, Integer errorCode, DracoonApiCode result) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);
        assertEquals(result, mErrorParser.parseS3UploadStatusError(errorResponse));
    }

    // --- Helper methods ---

    private static Stream<Arguments> extendTestArguments(Stream<Arguments> arguments,
            Arguments... newArguments) {
        return Stream.concat(Stream.of(newArguments), arguments);
    }

    private static Stream<Arguments> modifyTestArguments(Stream<Arguments> arguments,
            Arguments newArguments) {
        Object[] newArgs = newArguments.get();
        return arguments.map(a -> {
            Object[] args = a.get();
            if (args.length == newArgs.length && Objects.equals(args[0], newArgs[0]) &&
                    Objects.equals(args[1], newArgs[1])) {
                return newArguments;
            } else {
                return a;
            }
        });
    }

    private DracoonApiCode executeParseMethod(int code, Integer errorCode,
            Function<Response, DracoonApiCode> function) {
        Response errorResponse;
        if (errorCode == null) {
            errorResponse = createErrorResponse(code);
        } else {
            errorResponse = createErrorResponse(code, errorCode);
        }
        return function.apply(errorResponse);
    }

    private static Response createErrorResponse(int code) {
        return Response.error(code, ResponseBody.create("", MediaType.parse("application/json")));
    }

    private static Response createErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);

        Gson gson = sGsonBuilder.create();
        String json = gson.toJson(errorResponse);

        return Response.error(code, ResponseBody.create(json, MediaType.parse("application/json")));
    }

    private DracoonApiCode executeOkHttpParseMethod(int code, Integer errorCode,
            Function<okhttp3.Response, DracoonApiCode> function) {
        okhttp3.Response errorResponse;
        if (errorCode == null) {
            errorResponse = createOkHttpErrorResponse(code);
        } else {
            errorResponse = createOkHttpErrorResponse(code, errorCode);
        }
        return function.apply(errorResponse);
    }

    private static okhttp3.Response createOkHttpErrorResponse(int code) {
        return new okhttp3.Response.Builder()
                .protocol(Protocol.HTTP_1_0)
                .request(new Request.Builder().get().url("http://localhost").build())
                .code(code)
                .message("")
                .body(ResponseBody.create("", MediaType.parse("application/json")))
                .build();
    }

    private static okhttp3.Response createOkHttpErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);

        Gson gson = sGsonBuilder.create();
        String json = gson.toJson(errorResponse);

        return new okhttp3.Response.Builder()
                .protocol(Protocol.HTTP_1_0)
                .request(new Request.Builder().get().url("http://localhost").build())
                .code(code)
                .message("")
                .body(ResponseBody.create(json, MediaType.parse("application/json")))
                .build();
    }

    private static ApiErrorResponse createApiErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.code = code;
        errorResponse.errorCode = errorCode;
        return errorResponse;
    }

}
