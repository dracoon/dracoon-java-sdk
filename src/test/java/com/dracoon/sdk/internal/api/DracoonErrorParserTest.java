package com.dracoon.sdk.internal.api;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.internal.api.model.ApiErrorResponse;
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

    private static class TestArguments implements Comparable<TestArguments> {

        int code;
        Integer errorCode;
        DracoonApiCode result;

        public TestArguments(int code, Integer errorCode, DracoonApiCode result) {
            this.code = code;
            this.errorCode = errorCode;
            this.result = result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TestArguments that = (TestArguments) o;
            return code == that.code && Objects.equals(errorCode, that.errorCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, errorCode);
        }

        @Override
        public int compareTo(TestArguments that) {
            int r = Integer.compare(code, that.code);
            if (r != 0) {
                return r;
            }

            if (errorCode == null && that.errorCode == null)
                return 0;
            else if (errorCode == null)
                return -1;
            else if (that.errorCode == null)
                return 1;
            else
                return Integer.compare(-errorCode, -that.errorCode);
        }

    }

    // --- Retrofit error response parsing tests ---

    private static Stream<TestArguments> getBaseTestArguments() {
        return Stream.of(
                new TestArguments(400, -80000, DracoonApiCode.VALIDATION_FIELD_CAN_NOT_BE_EMPTY),
                new TestArguments(400, -80001, DracoonApiCode.VALIDATION_FIELD_NOT_POSITIVE),
                new TestArguments(400, -80003, DracoonApiCode.VALIDATION_FIELD_NOT_ZERO_POSITIVE),
                new TestArguments(400, -80007, DracoonApiCode.VALIDATION_FIELD_MAX_LENGTH_EXCEEDED),
                new TestArguments(400, -80018, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_9999),
                new TestArguments(400, -80019, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_1_9999),
                new TestArguments(400, -80023, DracoonApiCode.VALIDATION_FIELD_CONTAINS_INVALID_CHARACTERS),
                new TestArguments(400, -80024, DracoonApiCode.VALIDATION_INVALID_OFFSET_OR_LIMIT),
                new TestArguments(400, -80035, DracoonApiCode.VALIDATION_FIELD_NOT_BETWEEN_0_10),
                new TestArguments(400,      0, DracoonApiCode.VALIDATION_UNKNOWN_ERROR),
                new TestArguments(401, -10006, DracoonApiCode.AUTH_OAUTH_CLIENT_NO_PERMISSION),
                new TestArguments(401,      0, DracoonApiCode.AUTH_UNAUTHORIZED),
                new TestArguments(402,      0, DracoonApiCode.PRECONDITION_PAYMENT_REQUIRED),
                new TestArguments(403, -10003, DracoonApiCode.AUTH_USER_LOCKED),
                new TestArguments(403, -10007, DracoonApiCode.AUTH_USER_LOCKED),
                new TestArguments(403, -10004, DracoonApiCode.AUTH_USER_EXPIRED),
                new TestArguments(403, -10005, DracoonApiCode.AUTH_USER_TEMPORARY_LOCKED),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UNKNOWN_ERROR),
                new TestArguments(412, -10103, DracoonApiCode.PRECONDITION_MUST_ACCEPT_EULA),
                new TestArguments(412, -10104, DracoonApiCode.PRECONDITION_MUST_CHANGE_PASSWORD),
                new TestArguments(412, -10106, DracoonApiCode.PRECONDITION_MUST_CHANGE_USER_NAME),
                new TestArguments(412,      0, DracoonApiCode.PRECONDITION_UNKNOWN_ERROR),
                new TestArguments(429,      0, DracoonApiCode.SERVER_TOO_MANY_REQUESTS),
                new TestArguments(500,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(500,   null, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createBaseTestArguments() {
        return createArguments(getBaseTestArguments());
    }

    @ParameterizedTest
    @MethodSource("createBaseTestArguments")
    void testParseStandardError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseStandardError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseServerInfoQueryErrorArguments() {
        return createArguments(Stream.of(
                new TestArguments(301, null, DracoonApiCode.API_NOT_FOUND),
                new TestArguments(302, null, DracoonApiCode.API_NOT_FOUND),
                new TestArguments(401, null, DracoonApiCode.API_NOT_FOUND),
                new TestArguments(403, null, DracoonApiCode.API_NOT_FOUND),
                new TestArguments(500,   null, DracoonApiCode.SERVER_UNKNOWN_ERROR)));
    }

    @ParameterizedTest
    @MethodSource("createTestParseServerInfoQueryErrorArguments")
    void testParseServerInfoQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseServerInfoQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUserKeyPairSetErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -70022, DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID),
                new TestArguments(400, -70023, DracoonApiCode.VALIDATION_USER_KEY_PAIR_INVALID),
                new TestArguments(409, -70021, DracoonApiCode.SERVER_USER_KEY_PAIR_ALREADY_SET),
                new TestArguments(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
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
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, 0, DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND),
                new TestArguments(409, 0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
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
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -80023, DracoonApiCode.VALIDATION_INVALID_KEY));
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
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -80042, DracoonApiCode.VALIDATION_INVALID_IMAGE),
                new TestArguments(400, -80043, DracoonApiCode.VALIDATION_INVALID_IMAGE),
                new TestArguments(400, -80044, DracoonApiCode.VALIDATION_INVALID_IMAGE));
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
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_READ_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesQueryErrorArguments")
    void testParseNodesQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseRoomCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND),
                new TestArguments(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseRoomCreateErrorArguments")
    void testParseRoomCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseRoomCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseRoomUpdateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseRoomUpdateErrorArguments")
    void testParseRoomUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseRoomUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFolderCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFolderCreateErrorArguments")
    void testParseFolderCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFolderCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFolderUpdateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_FOLDER_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_FOLDER_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFolderUpdateErrorArguments")
    void testParseFolderUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFolderUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileUpdateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                new TestArguments(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                new TestArguments(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileUpdateErrorArguments")
    void testParseFileUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesDeleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_DELETE_ERROR),
                new TestArguments(404,      0, DracoonApiCode.SERVER_NODE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesDeleteErrorArguments")
    void testParseNodesDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesCopyErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40001, DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED),
                new TestArguments(400, -40002, DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED),
                new TestArguments(400, -41052, DracoonApiCode.VALIDATION_CAN_NOT_COPY_ROOM),
                new TestArguments(400, -41053, DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE),
                new TestArguments(400, -41054, DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(400, -41302,
                        DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME),
                new TestArguments(400, -41303,
                        DracoonApiCode.VALIDATION_CAN_NOT_COPY_NODE_TO_OWN_PLACE_WITHOUT_RENAME),
                new TestArguments(403, -40764, DracoonApiCode.SERVER_VIRUS_SCAN_IN_PROGRESS),
                new TestArguments(403, -40765, DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                new TestArguments(404, -40014, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                new TestArguments(404, -41050, DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND),
                new TestArguments(404, -41051, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                new TestArguments(409, -41001, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                new TestArguments(409, -41304, DracoonApiCode.VALIDATION_CAN_NOT_COPY_TO_CHILD),
                new TestArguments(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesCopyErrorArguments")
    void testParseNodesCopyError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesCopyError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesMoveErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40001, DracoonApiCode.VALIDATION_SOURCE_ROOM_ENCRYPTED),
                new TestArguments(400, -40002, DracoonApiCode.VALIDATION_TARGET_ROOM_ENCRYPTED),
                new TestArguments(400, -41052, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_ROOM),
                new TestArguments(400, -41053, DracoonApiCode.VALIDATION_FILE_CAN_NOT_BE_TARGET_NODE),
                new TestArguments(400, -41054, DracoonApiCode.VALIDATION_NODES_NOT_IN_SAME_PARENT),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(400, -41302, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_NODE_TO_OWN_PLACE),
                new TestArguments(403, -40764, DracoonApiCode.SERVER_VIRUS_SCAN_IN_PROGRESS),
                new TestArguments(403, -40765, DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UPDATE_ERROR),
                new TestArguments(404, -40014, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                new TestArguments(404, -41050, DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND),
                new TestArguments(404, -41051, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                new TestArguments(409, -41001, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                new TestArguments(409, -41304, DracoonApiCode.VALIDATION_CAN_NOT_MOVE_TO_CHILD),
                new TestArguments(409,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesMoveErrorArguments")
    void testParseNodesMoveError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesMoveError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40755, DracoonApiCode.VALIDATION_FILE_NAME_INVALID),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                new TestArguments(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_CREATE_ERROR),
                new TestArguments(404,      0, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                new TestArguments(507, -50504, DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA),
                new TestArguments(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                new TestArguments(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadCreateErrorArguments")
    void testParseUploadCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UNKNOWN_ERROR),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                new TestArguments(507, -50504, DracoonApiCode.SERVER_INSUFFICIENT_UL_SHARE_QUOTA),
                new TestArguments(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                new TestArguments(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadErrorArguments")
    void testParseUploadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadCompleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadCompleteErrorArguments")
    void testParseUploadCompleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadCompleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadGetUrlsErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(507, -40200, DracoonApiCode.SERVER_INSUFFICIENT_ROOM_QUOTA),
                new TestArguments(507, -90200, DracoonApiCode.SERVER_INSUFFICIENT_CUSTOMER_QUOTA),
                new TestArguments(507,      0, DracoonApiCode.SERVER_INSUFFICIENT_STORAGE));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadGetUrlsErrorArguments")
    void testParseS3UploadGetUrlsError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadGetUrlsError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadCompleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                new TestArguments(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadCompleteErrorArguments")
    void testParseS3UploadCompleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadCompleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadStatusErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadStatusErrorArguments")
    void testParseS3UploadStatusError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseS3UploadStatusError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadTokenGetErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403, -40764, DracoonApiCode.SERVER_VIRUS_SCAN_IN_PROGRESS),
                new TestArguments(403, -40765, DracoonApiCode.SERVER_MALICIOUS_FILE_DETECTED),
                new TestArguments(404,      0, DracoonApiCode.SERVER_FILE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadTokenGetErrorArguments")
    void testParseDownloadTokenGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadTokenGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadShareCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -10002, DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE),
                new TestArguments(400, -50004,
                        DracoonApiCode.VALIDATION_DL_SHARE_CAN_NOT_CREATE_ON_ENCRYPTED_ROOM_FOLDER),
                new TestArguments(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                new TestArguments(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                new TestArguments(400, -80009, DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID),
                new TestArguments(400, -80030, DracoonApiCode.SERVER_SMS_IS_DISABLED),
                new TestArguments(400, -80064, DracoonApiCode.VALIDATION_CLASSIFICATION_POLICY_VIOLATION),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(502, -90090, DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND),
                new TestArguments(502,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
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
                mErrorParser::parseDownloadSharesQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadShareDeleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -60000, DracoonApiCode.SERVER_DL_SHARE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadShareDeleteErrorArguments")
    void testParseDownloadShareDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseDownloadShareDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadShareCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -10002, DracoonApiCode.VALIDATION_PASSWORD_NOT_SECURE),
                new TestArguments(400, -41200, DracoonApiCode.VALIDATION_PATH_TOO_LONG),
                new TestArguments(400, -80006, DracoonApiCode.VALIDATION_EXPIRATION_DATE_IN_PAST),
                new TestArguments(400, -80008, DracoonApiCode.VALIDATION_EXPIRATION_DATE_TOO_LATE),
                new TestArguments(400, -80009, DracoonApiCode.VALIDATION_EMAIL_ADDRESS_INVALID),
                new TestArguments(400, -80030, DracoonApiCode.SERVER_SMS_IS_DISABLED),
                new TestArguments(400, -80064, DracoonApiCode.VALIDATION_CLASSIFICATION_POLICY_VIOLATION),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_MANAGE_UL_SHARES_ERROR),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_UL_SHARE_NAME_ALREADY_EXISTS),
                new TestArguments(502, -90090, DracoonApiCode.SERVER_SMS_COULD_NOT_BE_SEND),
                new TestArguments(502,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
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
                mErrorParser::parseUploadSharesQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseUploadShareDeleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_MANAGE_DL_SHARES_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -60500, DracoonApiCode.SERVER_UL_SHARE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseUploadShareDeleteErrorArguments")
    void testParseUploadShareDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseUploadShareDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileKeyQueryErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                new TestArguments(404, -40761, DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileKeyQueryErrorArguments")
    void testParseFileKeyQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileKeyQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseMissingFileKeysQueryErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40001, DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_ROOM_NOT_FOUND),
                new TestArguments(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                new TestArguments(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseMissingFileKeysQueryErrorArguments")
    void testParseMissingFileKeysQueryError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseMissingFileKeysQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFileKeysSetErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -40001, DracoonApiCode.VALIDATION_ROOM_NOT_ENCRYPTED),
                new TestArguments(403, -40761, DracoonApiCode.VALIDATION_USER_HAS_NO_FILE_KEY),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_UNKNOWN_ERROR),
                new TestArguments(404, -40751, DracoonApiCode.SERVER_FILE_NOT_FOUND),
                new TestArguments(404, -70501, DracoonApiCode.SERVER_USER_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFileKeysSetErrorArguments")
    void testParseFileKeysSetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFileKeysSetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseFavoriteMarkErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseFavoriteMarkErrorArguments")
    void testParseFavoriteMarkError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseFavoriteMarkError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentsGetErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentsGetErrorArguments")
    void testParseNodeCommentsGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentsQueryError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentCreateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -80023,
                        DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentCreateErrorArguments")
    void testParseNodeCommentCreateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentCreateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentUpdateErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -80023,
                        DracoonApiCode.VALIDATION_NODE_COMMENT_CONTAINS_INVALID_CHARACTERS),
                new TestArguments(400, -80039, DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED),
                new TestArguments(404, -41400, DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentUpdateErrorArguments")
    void testParseNodeCommentUpdateError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentUpdateError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodeCommentDeleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -80039, DracoonApiCode.SERVER_NODE_COMMENT_ALREADY_DELETED),
                new TestArguments(404, -41400, DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodeCommentDeleteErrorArguments")
    void testParseNodeCommentDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodeCommentDeleteError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseNodesVirusProtectionInfoGetErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -41002, DracoonApiCode.VALIDATION_NODE_NOT_A_FILE),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseNodesVirusProtectionInfoGetErrorArguments")
    void testParseNodesVirusProtectionInfoGetError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseNodesVirusProtectionInfoGetError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseMaliciousFileDeleteErrorArguments() {
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(400, -41002, DracoonApiCode.VALIDATION_NODE_NOT_A_FILE),
                new TestArguments(403, -70020, DracoonApiCode.VALIDATION_USER_HAS_NO_KEY_PAIR),
                new TestArguments(403,      0, DracoonApiCode.PERMISSION_DELETE_ERROR),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_NODE_NOT_FOUND),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseMaliciousFileDeleteErrorArguments")
    void testParseMaliciousFileDeleteError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeParseMethod(code, errorCode,
                mErrorParser::parseMaliciousFileDeleteError));
    }

    // --- OkHttp error response parsing tests ---

    @SuppressWarnings("unused")
    private static Stream<TestArguments> getOkHttpBaseTestArguments() {
        return Stream.of(
                new TestArguments(401, null, DracoonApiCode.AUTH_UNAUTHORIZED),
                new TestArguments(402, null, DracoonApiCode.PRECONDITION_PAYMENT_REQUIRED),
                new TestArguments(429, null, DracoonApiCode.SERVER_TOO_MANY_REQUESTS),
                new TestArguments(500, null, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseS3UploadErrorArguments() {
        return createArguments(
                Stream.of(
                    new TestArguments(404, null, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                    new TestArguments(500, null, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED)));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadErrorArguments")
    void testParseS3UploadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeOkHttpParseMethod(code, errorCode,
                mErrorParser::parseS3UploadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseDownloadErrorArguments() {
        return createArguments(
                getOkHttpBaseTestArguments(),
                new TestArguments(403, null, DracoonApiCode.PERMISSION_UNKNOWN_ERROR),
                new TestArguments(404, null, DracoonApiCode.SERVER_FILE_NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("createTestParseDownloadErrorArguments")
    void testParseDownloadError(int code, Integer errorCode, DracoonApiCode result) {
        assertEquals(result, executeOkHttpParseMethod(code, errorCode,
                mErrorParser::parseDownloadError));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTestParseAvatarDownloadErrorArguments() {
        return createArguments(
                getOkHttpBaseTestArguments(),
                new TestArguments(404, null, DracoonApiCode.SERVER_USER_AVATAR_NOT_FOUND));
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
        return createArguments(
                getBaseTestArguments(),
                new TestArguments(404, -20501, DracoonApiCode.SERVER_UPLOAD_NOT_FOUND),
                new TestArguments(404, -40000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -41000, DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND),
                new TestArguments(404, -90034, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(404,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR),
                new TestArguments(409, -40010, DracoonApiCode.VALIDATION_CAN_NOT_OVERWRITE_ROOM_FOLDER),
                new TestArguments(409,      0, DracoonApiCode.VALIDATION_NODE_ALREADY_EXISTS),
                new TestArguments(504, -90027, DracoonApiCode.SERVER_S3_COMMUNICATION_FAILED),
                new TestArguments(504,      0, DracoonApiCode.SERVER_UNKNOWN_ERROR));
    }

    @ParameterizedTest
    @MethodSource("createTestParseS3UploadStatusErrorRawArguments")
    void testParseS3UploadStatusErrorRaw(int code, Integer errorCode, DracoonApiCode result) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);
        assertEquals(result, mErrorParser.parseS3UploadStatusError(errorResponse));
    }

    // --- Helper methods ---

    private static Stream<Arguments> createArguments(Stream<TestArguments> arguments) {
        return arguments
                .distinct()
                .sorted()
                .map(ta -> Arguments.of(ta.code, ta.errorCode, ta.result));
    }

    private static Stream<Arguments> createArguments(Stream<TestArguments> arguments,
            TestArguments... newArguments) {
        return Stream.concat(Stream.of(newArguments), arguments)
                .distinct()
                .sorted()
                .map(ta -> Arguments.of(ta.code, ta.errorCode, ta.result));
    }


    private DracoonApiCode executeParseMethod(int code, Integer errorCode,
            Function<Response, DracoonApiCode> function) {
        Response response;
        if (code < 400) {
            response = createResponse(code);
        } else if (errorCode == null) {
            response = createErrorResponse(code);
        } else {
            response = createErrorResponse(code, errorCode);
        }
        return function.apply(response);
    }

    private static Response createResponse(int code) {
        ResponseBody responseBody = createEmptyJsonResponseBody();
        return Response.error(responseBody, createOkHttpResponse(code, responseBody));
    }

    private static Response createErrorResponse(int code) {
        return Response.error(code, createEmptyJsonResponseBody());
    }

    private static Response createErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);

        Gson gson = sGsonBuilder.create();
        String json = gson.toJson(errorResponse);

        return Response.error(code, createJsonResponseBody(json));
    }

    private DracoonApiCode executeOkHttpParseMethod(int code, Integer errorCode,
            Function<okhttp3.Response, DracoonApiCode> function) {
        okhttp3.Response response;
        if (errorCode == null) {
            response = createOkHttpErrorResponse(code);
        } else {
            response = createOkHttpErrorResponse(code, errorCode);
        }
        return function.apply(response);
    }

    private static okhttp3.Response createOkHttpErrorResponse(int code) {
        return createOkHttpResponse(code, createEmptyJsonResponseBody());
    }

    private static okhttp3.Response createOkHttpErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = createApiErrorResponse(code, errorCode);

        Gson gson = sGsonBuilder.create();
        String json = gson.toJson(errorResponse);

        return createOkHttpResponse(code, createJsonResponseBody(json));
    }

    private static okhttp3.Response createOkHttpResponse(int code, ResponseBody body) {
        return new okhttp3.Response.Builder()
                .protocol(Protocol.HTTP_1_0)
                .request(new Request.Builder().get().url("http://localhost").build())
                .code(code)
                .message("")
                .body(body)
                .build();
    }

    private static ResponseBody createEmptyJsonResponseBody() {
        return createJsonResponseBody("");
    }

    private static ResponseBody createJsonResponseBody(String json) {
        return ResponseBody.create(json, MediaType.parse("application/json"));
    }

    private static ApiErrorResponse createApiErrorResponse(int code, Integer errorCode) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.code = code;
        errorResponse.errorCode = errorCode;
        return errorResponse;
    }

}
