package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.FileUploadRequest;

import java.io.File;

public class UploadValidator {

    public static void validate(String id, FileUploadRequest request, File file)
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
