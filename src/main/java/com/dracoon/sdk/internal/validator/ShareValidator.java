package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateDownloadShareRequest;

public class ShareValidator extends BaseValidator {

    public static void validateCreateRequest(CreateDownloadShareRequest request,
            boolean isEncrypted) {
        if (request == null) {
            throw new IllegalArgumentException("Download share creation request cannot be null.");
        }
        validateNodeId(request.getNodeId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
        if (request.getMaxDownloads() != null) {
            validateMaxDownloads(request.getMaxDownloads());
        }
        if (isEncrypted && request.getAccessPassword() != null) {
            throw new IllegalArgumentException("Download shares of a encrypted node cannot have " +
                    "a access password.");
        } else if (!isEncrypted && request.getAccessPassword() != null) {
            validateAccessPassword(request.getAccessPassword());
        }
        if (!isEncrypted && request.getEncryptionPassword() != null) {
            throw new IllegalArgumentException("Download shares of a not encrypted node cannot " +
                    "have a encryption password.");
        } else if (isEncrypted && request.getEncryptionPassword() == null) {
            throw new IllegalArgumentException("Download shares of a encrypted node must have a " +
                    "encryption password.");
        } else if (isEncrypted && request.getEncryptionPassword() != null) {
            validateEncryptionPassword(request.getEncryptionPassword());
        }
        if (request.sendEmail()) {
            validateEmailAddresses(request.getEmailRecipients());
            validateEmailSubject(request.getEmailSubject());
            validateEmailBody(request.getEmailBody());
        }
        if (request.sendSms()) {
            validatePhoneNumbers(request.getSmsRecipients());
        }
    }

}
