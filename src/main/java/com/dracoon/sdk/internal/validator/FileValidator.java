package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;

public class FileValidator {

    public static void validateUpdateRequest(UpdateFileRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("File update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new DracoonInvalidArgException("File ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new DracoonInvalidArgException("File ID cannot be negative or 0.");
        }
        if (request.getName() != null && request.getName().isEmpty()) {
            throw new DracoonInvalidArgException("File name cannot be empty.");
        }
    }

    public static void validateUploadRequest(String id, FileUploadRequest request, File file)
            throws DracoonException {
        if (id == null || id.isEmpty()) {
            throw new DracoonInvalidArgException("Upload ID cannot be null.");
        }
        if (request == null) {
            throw new DracoonInvalidArgException("Upload request cannot be null.");
        }
        if (request.getParentId() == null) {
            throw new DracoonInvalidArgException("Upload parent ID cannot be null.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new DracoonInvalidArgException("Upload file name cannot be null or empty.");
        }
        if (file == null) {
            throw new DracoonInvalidArgException("Upload file cannot be null.");
        }
    }

}
