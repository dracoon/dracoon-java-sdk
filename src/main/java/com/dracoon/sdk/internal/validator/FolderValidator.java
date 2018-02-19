package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderValidator extends BaseValidator {

    public static void validateCreateRequest(CreateFolderRequest request) {
        ValidatorUtils.validateNotNull("Folder creation request", request);
        validateParentNodeId(request.getParentId());
        validateName(request.getName());
    }

    public static void validateUpdateRequest(UpdateFolderRequest request) {
        ValidatorUtils.validateNotNull("Folder update request", request);
        validateFolderId(request.getId());
        if (request.getName() != null) {
            validateName(request.getName());
        }
    }

}
