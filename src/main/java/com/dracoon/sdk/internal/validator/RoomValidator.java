package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomValidator {

    public static void validateCreateRequest(CreateRoomRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("Room creation request cannot be null.");
        }
        if (request.getParentId() != null && request.getParentId() < 0L) {
            throw new DracoonInvalidArgException("Room parent ID cannot be negative.");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new DracoonInvalidArgException("Room name cannot be null or empty.");
        }
    }

    public static void validateUpdateRequest(UpdateRoomRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("Room update request cannot be null.");
        }
        if (request.getId() == null) {
            throw new DracoonInvalidArgException("Room ID cannot be null.");
        }
        if (request.getId() <= 0L) {
            throw new DracoonInvalidArgException("Room ID cannot be negative or 0.");
        }
    }

}
