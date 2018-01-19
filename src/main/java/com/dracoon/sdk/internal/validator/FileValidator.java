package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;

public class FileValidator extends BaseValidator {

    public static void validateUploadRequest(String id, FileUploadRequest request, File file) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Upload ID cannot be null.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Upload request cannot be null.");
        }
        validateParentNodeId(request.getParentId());
        validateName(request.getName());
        if (file == null) {
            throw new IllegalArgumentException("Upload file cannot be null.");
        }
    }

    public static void validateUpdateRequest(UpdateFileRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("File update request cannot be null.");
        }
        validateFileId(request.getId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
    }

}
