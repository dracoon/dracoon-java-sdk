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

    protected static void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
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

}
