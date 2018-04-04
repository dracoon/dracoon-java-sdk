package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;
import java.io.InputStream;

public class FileValidator extends BaseValidator {

    public static void validateUploadRequest(String id, FileUploadRequest request, File file) {
        validateUploadRequest(id, request);
        ValidatorUtils.validateNotNull("Upload file", file);
    }

    public static void validateUploadRequest(String id, FileUploadRequest request, InputStream is) {
        validateUploadRequest(id, request);
        ValidatorUtils.validateNotNull("Upload stream", is);
    }

    private static void validateUploadRequest(String id, FileUploadRequest request) {
        ValidatorUtils.validateString("Upload ID", id, false);
        ValidatorUtils.validateNotNull("Upload request", request);
        validateParentNodeId(request.getParentId());
        validateName(request.getName());
    }

    public static void validateUpdateRequest(UpdateFileRequest request) {
        ValidatorUtils.validateNotNull("File update request", request);
        validateFileId(request.getId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
    }

}
