package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderValidator {

    public static void validateCreateRequest(CreateFolderRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("Folder creation request cannot be null.");
        }
        if (request.getParentId() == null) {
            throw new DracoonInvalidArgException("Folder parent ID cannot be null.");
        }
        if (request.getParentId() < 0L) {
            throw new DracoonInvalidArgException("Folder parent ID cannot be negative.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new DracoonInvalidArgException("Folder name cannot be null or empty.");
        }
    }

    public static void validateUpdateRequest(UpdateFolderRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("Folder update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new DracoonInvalidArgException("Folder ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new DracoonInvalidArgException("Folder ID cannot be negative or 0.");
        }
    }

}
