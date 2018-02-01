package com.dracoon.sdk.internal.validator;

import java.util.List;

public class BaseValidator {

    protected static void validateNodeId(Long id) {
        validateId(id, "Node");
    }

    protected static void validateNodeIds(List<Long> ids) {
        validateIds(ids, "Node");
    }

    protected static void validateParentNodeId(Long id) {
        validateId(id, "Parent node");
    }

    protected static void validateRoomId(Long id) {
        validateId(id, "Room");
    }

    protected static void validateFolderId(Long id) {
        validateId(id, "Folder");
    }

    protected static void validateFileId(Long id) {
        validateId(id, "File");
    }

    protected static void validateUserId(Long id) {
        validateId(id, "User");
    }

    protected static void validateUserIds(List<Long> ids) {
        validateIds(ids, "User");
    }

    protected static void validateGroupId(Long id) {
        validateId(id, "Group");
    }

    protected static void validateGroupIds(List<Long> ids) {
        validateIds(ids, "Group");
    }

    protected static void validateAccessPassword(String password) {
        validateString(password, "Access password");
    }

    protected static void validateEncryptionPassword(String password) {
        validateString(password, "Encryption password");
    }

    protected static void validateMaxDownloads(Integer maxDownloads) {
        validatePositiveNumber(maxDownloads, "Maximum downloads");
    }

    protected static void validateName(String name) {
        validateString(name, "Name");
    }

    protected static void validateEmailAddress(String address) {
        validateString(address, "Email address");
    }

    protected static void validateEmailAddresses(List<String> addresses) {
        if (addresses == null) {
            throw new IllegalArgumentException("Email addresses cannot be null.");
        }
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("Email addresses cannot be empty.");
        }
        for (String address : addresses) {
            validateEmailAddress(address);
        }
    }

    protected static void validateEmailSubject(String subject) {
        validateString(subject, "Email subject");
    }

    protected static void validateEmailBody(String body) {
        validateString(body, "Email body");
    }

    protected static void validatePhoneNumber(String number) {
        validateString(number, "Phone number");
    }

    protected static void validatePhoneNumbers(List<String> numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Phone numbers cannot be null.");
        }
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("Phone numbers cannot be empty.");
        }
        for (String number : numbers) {
            validatePhoneNumber(number);
        }
    }

    protected static void validateQuota(Long quota) {
        if (quota == null) {
            return;
        }
        if (quota < 0) {
            throw new IllegalArgumentException("Quota cannot be negative.");
        }
    }

    protected static void validatePeriod(Integer period) {
        if (period == null) {
            return;
        }
        if (period < 0) {
            throw new IllegalArgumentException("Period cannot be negative.");
        }
    }

    private static void validateId(Long id, String type) {
        if (id == null) {
            throw new IllegalArgumentException(type + " ID cannot be null.");
        }
        if (id <= 0L) {
            throw new IllegalArgumentException(type + " ID cannot be negative or 0.");
        }
    }

    private static void validateIds(List<Long> ids, String type) {
        if (ids == null) {
            throw new IllegalArgumentException(type + " IDs cannot be null.");
        }
        if (ids.isEmpty()) {
            throw new IllegalArgumentException(type + " IDs cannot be empty.");
        }
        ids.forEach(id -> validateId(id, type));
    }

    private static void validateString(String string, String type) {
        if (string == null) {
            throw new IllegalArgumentException(type + " cannot be null.");
        }
        if (string.isEmpty()) {
            throw new IllegalArgumentException(type + " cannot be empty.");
        }
    }

    private static void validatePositiveNumber(Integer number, String type) {
        if (number == null) {
            throw new IllegalArgumentException(type + " cannot be null.");
        }
        if (number <= 0) {
            throw new IllegalArgumentException(type + " cannot be negative or 0.");
        }
    }

}
