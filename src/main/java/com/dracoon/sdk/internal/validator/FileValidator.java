package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class FileValidator extends BaseValidator {

    public static void validateUploadRequest(String id, FileUploadRequest request, File file) {
        ValidatorUtils.validateString("Upload ID", id, false);
        validateUploadRequest(request);
        ValidatorUtils.validateNotNull("Upload file", file);
    }

    public static void validateUploadRequest(String id, FileUploadRequest request, InputStream is) {
        ValidatorUtils.validateString("Upload ID", id, false);
        validateUploadRequest(request);
        ValidatorUtils.validateNotNull("Upload stream", is);
    }

    public static void validateUploadRequest(FileUploadRequest request) {
        ValidatorUtils.validateNotNull("Upload request", request);
        validateParentNodeId(request.getParentId());
        validateFileName(request.getName());
    }

    public static void validateDownloadRequest(String id, File file) {
        validateDownloadRequest(id);
        ValidatorUtils.validateNotNull("Download file", file);
    }

    public static void validateDownloadRequest(String id, OutputStream os) {
        validateDownloadRequest(id);
        ValidatorUtils.validateNotNull("Download stream", os);
    }

    private static void validateDownloadRequest(String id) {
        ValidatorUtils.validateString("Download ID", id, false);
    }

    public static void validateUpdateRequest(UpdateFileRequest request) {
        ValidatorUtils.validateNotNull("File update request", request);
        validateFileId(request.getId());
        if (request.getName() != null) {
            validateFileName(request.getName());
        }
    }

}
