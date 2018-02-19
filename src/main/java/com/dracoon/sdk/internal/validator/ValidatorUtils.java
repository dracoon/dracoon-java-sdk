package com.dracoon.sdk.internal.validator;

import java.net.URL;
import java.util.List;

public class ValidatorUtils {

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
        ids.forEach(id -> validateId(name, id));
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

}
