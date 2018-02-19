package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;

public class FileValidator extends BaseValidator {

    public static void validateUploadRequest(String id, FileUploadRequest request, File file) {
        ValidatorUtils.validateString("Upload ID", id, false);
        ValidatorUtils.validateNotNull("Upload request", request);
        validateParentNodeId(request.getParentId());
        validateName(request.getName());
        ValidatorUtils.validateNotNull("Upload file", file);
    }

    public static void validateUpdateRequest(UpdateFileRequest request) {
        ValidatorUtils.validateNotNull("File update request", request);
        validateFileId(request.getId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
    }

}
