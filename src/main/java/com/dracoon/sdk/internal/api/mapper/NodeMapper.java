package com.dracoon.sdk.internal.api.mapper;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.api.model.ApiCopyNode;
import com.dracoon.sdk.internal.api.model.ApiCopyNodesRequest;
import com.dracoon.sdk.internal.api.model.ApiMoveNode;
import com.dracoon.sdk.internal.api.model.ApiMoveNodesRequest;
import com.dracoon.sdk.internal.api.model.ApiNode;
import com.dracoon.sdk.internal.api.model.ApiNodeComment;
import com.dracoon.sdk.internal.api.model.ApiNodeList;
import com.dracoon.sdk.internal.api.model.ApiNodeVirusProtectionInfo;
import com.dracoon.sdk.internal.api.model.ApiUpdateNodeCommentRequest;
import com.dracoon.sdk.internal.api.model.ApiVirusProtectionInfo;
import com.dracoon.sdk.internal.api.model.ApiCreateNodeCommentRequest;
import com.dracoon.sdk.internal.api.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.api.model.ApiNodeCommentList;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateNodeCommentRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileVirusScanInfo;
import com.dracoon.sdk.model.FileVirusScanInfoList;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeComment;
import com.dracoon.sdk.model.NodeCommentList;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.NodeType;
import com.dracoon.sdk.model.UpdateNodeCommentRequest;
import com.dracoon.sdk.model.VirusScanInfo;

public class NodeMapper extends BaseMapper {

    private NodeMapper() {
        super();
    }

    public static NodeList fromApiNodeList(ApiNodeList apiNodeList) {
        if (apiNodeList == null) {
            return null;
        }

        NodeList nodeList = new NodeList();
        nodeList.setOffset(apiNodeList.range.offset);
        nodeList.setLimit(apiNodeList.range.limit);
        nodeList.setTotal(apiNodeList.range.total);
        ArrayList<Node> items = new ArrayList<>();
        for (ApiNode apiNode : apiNodeList.items) {
            items.add(NodeMapper.fromApiNode(apiNode));
        }
        nodeList.setItems(items);
        return nodeList;
    }

    public static Node fromApiNode(ApiNode apiNode) {
        if (apiNode == null) {
            return null;
        }

        Node node = new Node();

        node.setId(apiNode.id);
        if (apiNode.type != null) {
            node.setType(NodeType.getByValue(apiNode.type));
        }
        node.setParentId(apiNode.parentId);
        node.setParentPath(apiNode.parentPath);
        node.setName(apiNode.name);
        node.setExtension(apiNode.fileType);

        node.setMediaType(apiNode.mediaType);
        node.setSize(apiNode.size);
        node.setQuota(apiNode.quota);
        if (apiNode.classification != null) {
            node.setClassification(Classification.getByValue(apiNode.classification));
        }
        node.setNotes(apiNode.notes);
        node.setHash(apiNode.hash);
        node.setExpireAt(apiNode.expireAt);

        node.setCreatedAt(apiNode.createdAt);
        node.setCreatedBy(UserMapper.fromApiUserInfo(apiNode.createdBy));
        node.setUpdatedAt(apiNode.updatedAt);
        node.setUpdatedBy(UserMapper.fromApiUserInfo(apiNode.updatedBy));

        node.setOriginalCreatedAt(apiNode.timestampCreation);
        node.setOriginalModifiedAt(apiNode.timestampModification);

        node.setHasInheritPermissions(toBoolean(apiNode.inheritPermissions));
        node.setPermissions(NodePermissionsMapper.fromApiNodePermissions(apiNode.permissions));

        node.setIsFavorite(toBoolean(apiNode.isFavorite));
        node.setIsEncrypted(toBoolean(apiNode.isEncrypted));
        node.setCntChildRooms(apiNode.cntRooms);
        node.setCntChildFolders(apiNode.cntFolders);
        node.setCntChildFiles(apiNode.cntFiles);
        node.setCntDeletedVersions(apiNode.cntDeletedVersions);
        node.setRecycleBinRetentionPeriod(apiNode.recycleBinRetentionPeriod);
        node.setCntComments(apiNode.cntComments);
        node.setCntDownloadShares(apiNode.cntDownloadShares);
        node.setCntUploadShares(apiNode.cntUploadShares);
        node.setBranchVersion(apiNode.branchVersion);

        node.setMediaToken(apiNode.mediaToken);
        if (apiNode.virusProtectionInfo != null) {
            node.setVirusScanInfo(fromApiVirusScanProtectionInfo(apiNode.virusProtectionInfo));
        }

        return node;
    }

    public static ApiDeleteNodesRequest toApiDeleteNodesRequest(DeleteNodesRequest request) {
        ApiDeleteNodesRequest apiRequest = new ApiDeleteNodesRequest();
        if (request.getIds() != null) {
            apiRequest.nodeIds = request.getIds();
        }
        return apiRequest;
    }

    public static ApiCopyNodesRequest toApiCopyNodesRequest(CopyNodesRequest request) {
        ApiCopyNodesRequest apiRequest = new ApiCopyNodesRequest();
        apiRequest.nodeIds = new ArrayList<>();
        apiRequest.items = new ArrayList<>();
        for (CopyNodesRequest.SourceNode sourceNode : request.getSourceNodes()) {
            apiRequest.nodeIds.add(sourceNode.getId());
            ApiCopyNode copyNode = new ApiCopyNode();
            copyNode.id = sourceNode.getId();
            copyNode.name = sourceNode.getName();
            apiRequest.items.add(copyNode);
        }
        apiRequest.resolutionStrategy = request.getResolutionStrategy().getValue();
        return apiRequest;
    }

    public static ApiMoveNodesRequest toApiMoveNodesRequest(MoveNodesRequest request) {
        ApiMoveNodesRequest apiRequest = new ApiMoveNodesRequest();
        apiRequest.nodeIds = new ArrayList<>();
        apiRequest.items = new ArrayList<>();
        for (MoveNodesRequest.SourceNode sourceNode : request.getSourceNodes()) {
            apiRequest.nodeIds.add(sourceNode.getId());
            ApiMoveNode moveNode = new ApiMoveNode();
            moveNode.id = sourceNode.getId();
            moveNode.name = sourceNode.getName();
            apiRequest.items.add(moveNode);
        }
        apiRequest.resolutionStrategy = request.getResolutionStrategy().getValue();
        return apiRequest;
    }

    public static NodeCommentList fromApiNodeCommentList(ApiNodeCommentList apiNodeCommentList) {
        if (apiNodeCommentList == null) {
            return null;
        }

        NodeCommentList nodeCommentList = new NodeCommentList();
        nodeCommentList.setOffset(apiNodeCommentList.range.offset);
        nodeCommentList.setLimit(apiNodeCommentList.range.limit);
        nodeCommentList.setTotal(apiNodeCommentList.range.total);
        ArrayList<NodeComment> items = new ArrayList<>();
        for (ApiNodeComment apiNodeComment : apiNodeCommentList.items) {
            items.add(NodeMapper.fromApiNodeComment(apiNodeComment));
        }
        nodeCommentList.setItems(items);
        return nodeCommentList;
    }

    public static NodeComment fromApiNodeComment(ApiNodeComment apiNodeComment) {
        if (apiNodeComment == null) {
            return null;
        }

        NodeComment nodeComment = new NodeComment();

        nodeComment.setId(apiNodeComment.id);
        nodeComment.setText(apiNodeComment.text);

        nodeComment.setCreatedAt(apiNodeComment.createdAt);
        nodeComment.setCreatedBy(UserMapper.fromApiUserInfo(apiNodeComment.createdBy));
        nodeComment.setUpdatedAt(apiNodeComment.updatedAt);
        nodeComment.setUpdatedBy(UserMapper.fromApiUserInfo(apiNodeComment.updatedBy));

        nodeComment.setWasChanged(toBoolean(apiNodeComment.isChanged));
        nodeComment.setWasDeleted(toBoolean(apiNodeComment.isDeleted));

        return nodeComment;
    }

    public static ApiCreateNodeCommentRequest toApiCreateNodeCommentRequest(
            CreateNodeCommentRequest request) {
        ApiCreateNodeCommentRequest apiRequest = new ApiCreateNodeCommentRequest();
        apiRequest.text = request.getText();
        return apiRequest;
    }

    public static ApiUpdateNodeCommentRequest toApiUpdateNodeCommentRequest(
            UpdateNodeCommentRequest request) {
        ApiUpdateNodeCommentRequest apiRequest = new ApiUpdateNodeCommentRequest();
        apiRequest.text = request.getText();
        return apiRequest;
    }

    private static VirusScanInfo fromApiVirusScanProtectionInfo(
            ApiVirusProtectionInfo apiVirusProtectionInfo) {
        if (apiVirusProtectionInfo == null) {
            return null;
        }

        VirusScanInfo virusScanInfo = new VirusScanInfo();
        if (apiVirusProtectionInfo.verdict != null) {
            virusScanInfo.setVerdict(VirusScanInfo.Verdict.getByValue(
                    apiVirusProtectionInfo.verdict.toLowerCase()));
        }
        virusScanInfo.setLastScannedAt(apiVirusProtectionInfo.lastCheckedAt);
        return virusScanInfo;
    }

    public static FileVirusScanInfoList fromApiNodeVirusProtectionInfos(
            List<ApiNodeVirusProtectionInfo> apiNodeVirusProtectionInfos) {
        if (apiNodeVirusProtectionInfos == null) {
            return null;
        }

        FileVirusScanInfoList fileVirusScanInfoList = new FileVirusScanInfoList();
        ArrayList<FileVirusScanInfo> items = new ArrayList<>();
        for (ApiNodeVirusProtectionInfo apiNodeVirusProtectionInfo : apiNodeVirusProtectionInfos) {
            items.add(NodeMapper.fromApiNodeVirusProtectionInfo(apiNodeVirusProtectionInfo));
        }
        fileVirusScanInfoList.setItems(items);
        return fileVirusScanInfoList;
    }

    private static FileVirusScanInfo fromApiNodeVirusProtectionInfo(
            ApiNodeVirusProtectionInfo apiNodeVirusProtectionInfo) {
        if (apiNodeVirusProtectionInfo == null) {
            return null;
        }

        FileVirusScanInfo fileVirusScanInfo = new FileVirusScanInfo();
        fileVirusScanInfo.setNodeId(apiNodeVirusProtectionInfo.nodeId);
        VirusScanInfo virusScanInfo = new VirusScanInfo();
        if (apiNodeVirusProtectionInfo.verdict != null) {
            virusScanInfo.setVerdict(VirusScanInfo.Verdict.getByValue(
                    apiNodeVirusProtectionInfo.verdict.toLowerCase()));
        }
        virusScanInfo.setLastScannedAt(apiNodeVirusProtectionInfo.lastCheckedAt);
        fileVirusScanInfo.setVirusScanInfo(virusScanInfo);
        return fileVirusScanInfo;
    }

}
