package com.dracoon.sdk.internal.validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.util.TextUtils;

public class ValidatorUtils {

    private static final char[] INVALID_FILE_NAME_CHARS = {'<', '>', ':', '"', '|', '?', '*',
            '/', '\\'};
    private static final char[] INVALID_FILE_PATH_CHARS = {'<', '>', ':', '"', '|', '?', '*', '\\'};

    // --- Null validation methods ---

    public static void validateNotNull(String name, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null.");
        }
    }

    // --- ID validation methods ---

    public static void validateId(String name, Long id) {
        validateNotNull(name + " ID", id);
        if (id <= 0L) {
            throw new IllegalArgumentException(name + " ID cannot be negative or 0.");
        }
    }

    public static void validateIds(String name, List<Long> ids) {
        validateNotNull(name + " IDs", ids);
        if (ids.isEmpty()) {
            throw new IllegalArgumentException(name + " IDs cannot be empty.");
        }
        for (Long id : ids) {
            validateId(name, id);
        }
    }

    // --- Number validation methods ---

    public static void validatePositiveNumber(String name, Integer number, boolean nullable) {
        if (nullable && number == null) {
            return;
        }
        validateNotNull(name, number);
        if (number <= 0) {
            throw new IllegalArgumentException(name + " cannot be negative or 0.");
        }
    }

    public static void validatePositiveNumber(String name, Long number, boolean nullable) {
        if (nullable && number == null) {
            return;
        }
        validateNotNull(name, number);
        if (number <= 0) {
            throw new IllegalArgumentException(name + " cannot be negative or 0.");
        }
    }

    public static void validateNotNegative(String name, Long number, boolean nullable) {
        if (nullable && number == null) {
            return;
        }
        validateNotNull(name, number);
        if (number < 0) {
            throw new IllegalArgumentException(name + " cannot be negative.");
        }
    }

    // --- String validation methods ---

    public static void validateString(String name, String string, boolean nullable) {
        if (nullable && string == null) {
            return;
        }
        validateNotNull(name, string);
        if (string.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty.");
        }
    }

    // --- Byte array validation methods ---

    public static void validateByteArray(String name, byte[] bytes, boolean nullable, long min,
            long max) {
        if (nullable && bytes == null) {
            return;
        }
        validateNotNull(name, bytes);
        if (bytes.length < min) {
            throw new IllegalArgumentException(name + " cannot be smaller then " + min + " bytes.");
        }
        if (bytes.length > max) {
            throw new IllegalArgumentException(name + " cannot be larger then " + max + " bytes.");
        }
    }

    // --- Other validation methods ---

    public static void validateServerURL(URL serverUrl) {
        validateNotNull("Server URL", serverUrl);
        String protocol = serverUrl.getProtocol();
        if (protocol == null || !(protocol.equals("http") || protocol.equals("https"))) {
            throw new IllegalArgumentException("Server URL can only have protocol http or https.");
        }
        String user = serverUrl.getUserInfo();
        if (user != null) {
            throw new IllegalArgumentException("Server URL cannot have user.");
        }
        String path = serverUrl.getPath();
        if (path != null && !path.isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot have path.");
        }
        String query = serverUrl.getQuery();
        if (query != null) {
            throw new IllegalArgumentException("Server URL cannot have query.");
        }
    }

    public static void validateFileName(String name, String string) {
        validateNotNull(name, string);

        List<String> invalidCharacters = new ArrayList<>();
        for (char invalidChar : INVALID_FILE_NAME_CHARS) {
            if (string.indexOf(invalidChar) > -1) {
                invalidCharacters.add("'" + invalidChar + "'");
            }
        }
        if (!invalidCharacters.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot contain " + TextUtils.join(
                    invalidCharacters) + ".");
        }
    }

    public static void validateFilePath(String name, String string) {
        validateNotNull(name, string);

        List<String> invalidCharacters = new ArrayList<>();
        for (char invalidChar : INVALID_FILE_PATH_CHARS) {
            if (string.indexOf(invalidChar) > -1) {
                invalidCharacters.add("'" + invalidChar + "'");
            }
        }
        if (!invalidCharacters.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot contain " + TextUtils.join(
                    invalidCharacters) + ".");
        }

        if (!string.startsWith("/")) {
            throw new IllegalArgumentException(name + " must start with '/'.");
        }
        if (string.endsWith("/")) {
            throw new IllegalArgumentException(name + " cannot end with '/'.");
        }
    }

}
