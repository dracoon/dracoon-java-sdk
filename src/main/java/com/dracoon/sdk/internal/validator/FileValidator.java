package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.UpdateFileRequest;

public class FileValidator {

    public static void validate(UpdateFileRequest request) throws DracoonException {
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

}
