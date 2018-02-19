package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomValidator extends BaseValidator {

    public static void validateCreateRequest(CreateRoomRequest request) {
        ValidatorUtils.validateNotNull("Room creation request", request);
        if (request.getParentId() != null) {
            validateParentNodeId(request.getParentId());
        }
        validateName(request.getName());
        validateQuota(request.getQuota());
        validatePeriod(request.getRecycleBinRetentionPeriod());
        if ((request.getAdminUserIds() == null || request.getAdminUserIds().isEmpty()) &&
                (request.getAdminGroupIds() == null || request.getAdminGroupIds().isEmpty())) {
            throw new IllegalArgumentException("Room must have an admin user or admin group.");
        }
        if (request.getAdminUserIds() != null) {
            validateUserIds(request.getAdminUserIds());
        }
        if (request.getAdminGroupIds() != null) {
            validateGroupIds(request.getAdminGroupIds());
        }
    }

    public static void validateUpdateRequest(UpdateRoomRequest request) {
        ValidatorUtils.validateNotNull("Room update request", request);
        if (request.getName() != null) {
            validateName(request.getName());
        }
        validateRoomId(request.getId());
        validateQuota(request.getQuota());
    }

}
