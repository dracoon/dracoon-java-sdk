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
        ValidatorUtils.validateNotNull("Nodes delete request", request);
        validateNodeIds(request.getIds());
    }

    public static void validateCopyRequest(CopyNodesRequest request) {
        ValidatorUtils.validateNotNull("Nodes copy request", request);
        validateNodeId(request.getTargetNodeId());
        validateNodeIds(request.getSourceNodeIds());
    }

    public static void validateMoveRequest(MoveNodesRequest request) {
        ValidatorUtils.validateNotNull("Nodes move request", request);
        validateNodeId(request.getTargetNodeId());
        validateNodeIds(request.getSourceNodeIds());
    }

    public static void validateSearchRequest(long id, String searchString) {
        if (id != 0L) {
            BaseValidator.validateParentNodeId(id);
        }
        ValidatorUtils.validateString("Search string", searchString, false);
    }

}
