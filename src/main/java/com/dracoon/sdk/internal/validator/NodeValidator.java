package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.model.DeleteNodesRequest;

public class NodeValidator {

    public static void validateDeleteRequest(DeleteNodesRequest request) throws DracoonException {
        if (request == null) {
            throw new DracoonInvalidArgException("Nodes delete request cannot be null.");
        }
        if (request.getIds() == null) {
            throw new DracoonInvalidArgException("Node IDs cannot be null.");
        }
        if (request.getIds().contains(null)) {
            throw new DracoonInvalidArgException("Node ID cannot be null.");
        }
    }

}
