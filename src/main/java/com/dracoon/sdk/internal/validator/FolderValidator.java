package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderValidator extends BaseValidator {

    private FolderValidator() {
        super();
    }

    public static void validateCreateRequest(CreateFolderRequest request) {
        ValidatorUtils.validateNotNull("Folder creation request", request);
        validateParentNodeId(request.getParentId());
        validateFolderName(request.getName());
    }

    public static void validateUpdateRequest(UpdateFolderRequest request) {
        ValidatorUtils.validateNotNull("Folder update request", request);
        validateFolderId(request.getId());
        if (request.getName() != null) {
            validateFolderName(request.getName());
        }
    }

}
