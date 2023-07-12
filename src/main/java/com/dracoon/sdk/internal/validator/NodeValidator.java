package com.dracoon.sdk.internal.validator;

import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateNodeCommentRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.GetFilesVirusScanInfoRequest;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.UpdateNodeCommentRequest;

public class NodeValidator extends BaseValidator {

    private NodeValidator() {
        super();
    }

    public static void validateParentNodeId(long id) {
        if (id != 0L) {
            BaseValidator.validateParentNodeId(id);
        }
    }

    public static void validateNodeId(long id) {
        BaseValidator.validateNodeId(id);
    }

    public static void validateNodePath(String path) {
        ValidatorUtils.validateFilePath("Node path", path);
    }

    public static void validateDeleteRequest(DeleteNodesRequest request) {
        ValidatorUtils.validateNotNull("Nodes delete request", request);
        validateNodeIds(request.getIds());
    }

    public static void validateCopyRequest(CopyNodesRequest request) {
        ValidatorUtils.validateNotNull("Nodes copy request", request);
        validateNodeId(request.getTargetNodeId());
        for (CopyNodesRequest.SourceNode sourceNode : request.getSourceNodes()) {
            validateNodeId(sourceNode.getId());
            if (sourceNode.getName() != null) {
                validateNodeName(sourceNode.getName());
            }
        }
    }

    public static void validateMoveRequest(MoveNodesRequest request) {
        ValidatorUtils.validateNotNull("Nodes move request", request);
        validateNodeId(request.getTargetNodeId());
        for (MoveNodesRequest.SourceNode sourceNode : request.getSourceNodes()) {
            validateNodeId(sourceNode.getId());
            if (sourceNode.getName() != null) {
                validateNodeName(sourceNode.getName());
            }
        }
    }

    public static void validateSearchRequest(long id, String searchString) {
        if (id != 0L) {
            BaseValidator.validateParentNodeId(id);
        }
        ValidatorUtils.validateString("Search string", searchString, false);
    }

    public static void validateCreateCommentRequest(CreateNodeCommentRequest request) {
        ValidatorUtils.validateNotNull("Comment create request", request);
        validateNodeId(request.getNodeId());
        validateText(request.getText());
    }

    public static void validateUpdateCommentRequest(UpdateNodeCommentRequest request) {
        ValidatorUtils.validateNotNull("Comment update request", request);
        validateCommentId(request.getId());
        validateText(request.getText());
    }

    public static void validateGetVirusScanInfoRequest(GetFilesVirusScanInfoRequest request) {
        ValidatorUtils.validateNotNull("Virus scan info get request", request);
        validateNodeIds(request.getIds());
    }

    public static void validateMediaUrlRequest(String mediaToken, int width, int height) {
        ValidatorUtils.validateString("Media token", mediaToken, false);
        ValidatorUtils.validatePositiveNumber("Width", width, false);
        ValidatorUtils.validatePositiveNumber("Height", height, false);
    }

}
