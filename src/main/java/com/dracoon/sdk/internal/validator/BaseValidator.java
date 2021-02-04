package com.dracoon.sdk.internal.validator;

import java.util.List;

public class BaseValidator {

    protected BaseValidator() {

    }

    protected static void validateNodeId(Long id) {
        ValidatorUtils.validateId("Node", id);
    }

    protected static void validateNodeIds(List<Long> ids) {
        ValidatorUtils.validateIds("Node", ids);
    }

    protected static void validateParentNodeId(Long id) {
        ValidatorUtils.validateId("Parent node", id);
    }

    protected static void validateNodeName(String name) {
        ValidatorUtils.validateFileName("Node name", name);
    }

    protected static void validateRoomId(Long id) {
        ValidatorUtils.validateId("Room", id);
    }

    protected static void validateRoomName(String name) {
        ValidatorUtils.validateFileName("Room name", name);
    }

    protected static void validateFolderId(Long id) {
        ValidatorUtils.validateId("Folder", id);
    }

    protected static void validateFolderName(String name) {
        ValidatorUtils.validateFileName("Folder name", name);
    }

    protected static void validateFileId(Long id) {
        ValidatorUtils.validateId("File", id);
    }

    protected static void validateFileName(String name) {
        ValidatorUtils.validateFileName("File name", name);
    }

    protected static void validateName(String name) {
        ValidatorUtils.validateString("Name", name, false);
    }

    protected static void validateText(String text) {
        ValidatorUtils.validateString("Text", text, false);
    }

    protected static void validateUserId(Long id) {
        ValidatorUtils.validateId("User", id);
    }

    protected static void validateUserIds(List<Long> ids) {
        ValidatorUtils.validateIds("User", ids);
    }

    protected static void validateGroupId(Long id) {
        ValidatorUtils.validateId("Group", id);
    }

    protected static void validateGroupIds(List<Long> ids) {
        ValidatorUtils.validateIds("Group", ids);
    }

    public static void validateShareId(Long shareId) {
        ValidatorUtils.validateId("Share ID", shareId);
    }

    public static void validateCommentId(Long commentId) {
        ValidatorUtils.validateId("Comment ID", commentId);
    }

    protected static void validateAccessPassword(String password) {
        ValidatorUtils.validateString("Access password", password, false);
    }

    protected static void validateEncryptionPassword(String password) {
        ValidatorUtils.validateString("Encryption password", password, false);
    }

    protected static void validateMaxDownloads(Integer maxDownloads) {
        ValidatorUtils.validatePositiveNumber("Maximum downloads", maxDownloads, false);
    }

    protected static void validateMaxUploads(Integer maxUploads) {
        ValidatorUtils.validatePositiveNumber("Maximum uploads", maxUploads, false);
    }

    protected static void validateEmailAddress(String address) {
        ValidatorUtils.validateString("Email address", address, false);
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
        ValidatorUtils.validateString("Email subject", subject, false);
    }

    protected static void validateEmailBody(String body) {
        ValidatorUtils.validateString("Email body", body, false);
    }

    protected static void validatePhoneNumber(String number) {
        ValidatorUtils.validateString("Phone number", number, false);
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
        ValidatorUtils.validatePositiveNumber("Quota", quota, true);
    }

    protected static void validatePeriod(Integer period) {
        ValidatorUtils.validatePositiveNumber("Period", period, true);
    }

    public static void validateRange(Long offset, Long limit, boolean nullable) {
        ValidatorUtils.validateNotNegative("offset", offset, nullable);
        ValidatorUtils.validatePositiveNumber("limit", limit, nullable);
    }

}
