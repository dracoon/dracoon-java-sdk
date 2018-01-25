package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.MoveNodesRequest;

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

    public static void validateCopyRequest(CopyNodesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nodes copy request cannot be null.");
        }
        validateNodeId(request.getTargetNodeId());
        validateNodeIds(request.getSourceNodeIds());
    }

    public static void validateMoveRequest(MoveNodesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nodes move request cannot be null.");
        }
        validateNodeId(request.getTargetNodeId());
        validateNodeIds(request.getSourceNodeIds());
    }

}
