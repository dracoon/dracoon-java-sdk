package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomValidator {

    public static void validateCreateRequest(CreateRoomRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Room creation request cannot be null.");
        }
        if (request.getParentId() != null && request.getParentId() < 0L) {
            throw new IllegalArgumentException("Room parent ID cannot be negative.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be null or empty.");
        }
    }

    public static void validateUpdateRequest(UpdateRoomRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Room update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("Room ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new IllegalArgumentException("Room ID cannot be negative or 0.");
        }
    }

}
