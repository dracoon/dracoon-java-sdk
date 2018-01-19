package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.DeleteNodesRequest;

public class NodeValidator extends BaseValidator {

    public static void validateGetRequest(long id) {
        BaseValidator.validateNodeId(id);
    }

    public static void validateGetChildRequest(long id) {
        if (id != 0L) {
            BaseValidator.validateParentNodeId(id);
        }
    }

    public static void validateDeleteRequest(DeleteNodesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nodes delete request cannot be null.");
        }
        validateNodeIds(request.getIds());
    }

}
