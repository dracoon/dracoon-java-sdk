package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderValidator {

    public static void validateCreateRequest(CreateFolderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Folder creation request cannot be null.");
        }
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("Folder parent ID cannot be null.");
        }
        if (request.getParentId() < 0L) {
            throw new IllegalArgumentException("Folder parent ID cannot be negative.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Folder name cannot be null or empty.");
        }
    }

    public static void validateUpdateRequest(UpdateFolderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Folder update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("Folder ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new IllegalArgumentException("Folder ID cannot be negative or 0.");
        }
    }

}
