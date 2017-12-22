package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.DeleteNodesRequest;

public class NodeValidator {

    public static void validateDeleteRequest(DeleteNodesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nodes delete request cannot be null.");
        }
        if (request.getIds() == null) {
            throw new IllegalArgumentException("Node IDs cannot be null.");
        }
        if (request.getIds().contains(null)) {
            throw new IllegalArgumentException("Node ID cannot be null.");
        }
    }

}
