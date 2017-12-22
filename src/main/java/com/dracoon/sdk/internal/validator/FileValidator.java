package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.io.File;

public class FileValidator {

    public static void validateUpdateRequest(UpdateFileRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("File update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("File ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new IllegalArgumentException("File ID cannot be negative or 0.");
        }
        if (request.getName() != null && request.getName().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }
    }

    public static void validateUploadRequest(String id, FileUploadRequest request, File file) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Upload ID cannot be null.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Upload request cannot be null.");
        }
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("Upload parent ID cannot be null.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Upload file name cannot be null or empty.");
        }
        if (file == null) {
            throw new IllegalArgumentException("Upload file cannot be null.");
        }
    }

}
