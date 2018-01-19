package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderValidator extends BaseValidator {

    public static void validateCreateRequest(CreateFolderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Folder creation request cannot be null.");
        }
        validateParentNodeId(request.getParentId());
        validateName(request.getName());
    }

    public static void validateUpdateRequest(UpdateFolderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Folder update request cannot be null.");
        }
        validateFolderId(request.getId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
    }

}
