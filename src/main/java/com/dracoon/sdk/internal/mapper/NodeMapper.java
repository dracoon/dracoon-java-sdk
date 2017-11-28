package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.util.DateUtils;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.NodeType;

import java.util.ArrayList;

public class NodeMapper {

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

        node.setFileType(apiNode.fileType);
        node.setMediaType(apiNode.mediaType);
        node.setSize(apiNode.size);
        node.setQuota(apiNode.quota);
        if (apiNode.classification != null) {
            node.setClassification(Classification.getByValue(apiNode.classification));
        }
        node.setNotes(apiNode.notes);
        node.setHash(apiNode.hash);
        node.setExpireAt(DateUtils.parseDate(apiNode.expireAt));

        if (apiNode.createdAt != null) {
            node.setCreatedAt(DateUtils.parseDate(apiNode.createdAt));
        }
        node.setCreatedBy(UserInfoMapper.fromApiUserInfo(apiNode.createdBy));
        if (apiNode.updatedAt != null) {
            node.setUpdatedAt(DateUtils.parseDate(apiNode.updatedAt));
        }
        node.setUpdatedBy(UserInfoMapper.fromApiUserInfo(apiNode.updatedBy));

        node.setHasInheritPermissions(apiNode.inheritPermissions);
        node.setPermissions(NodePermissionsMapper.fromApiNodePermissions(apiNode.permissions));

        node.setIsFavorite(apiNode.isFavorite);
        node.setIsEncrypted(apiNode.isEncrypted);
        node.setCntChildren(apiNode.cntChildren);
        node.setCntDeletedVersions(apiNode.cntDeletedVersions);
        node.setHasRecycleBin(apiNode.hasRecycleBin);
        node.setRecycleBinRetentionPeriod(apiNode.recycleBinRetentionPeriod);
        node.setCntDownloadShares(apiNode.cntDownloadShares);
        node.setCntUploadShares(apiNode.cntUploadShares);
        node.setBranchVersion(apiNode.branchVersion);

        return node;
    }

    public static ApiDeleteNodesRequest toApiDeleteNodesRequest(DeleteNodesRequest request) {
        ApiDeleteNodesRequest apiRequest = new ApiDeleteNodesRequest();
        if (request.getIds() != null) {
            apiRequest.nodeIds = request.getIds().toArray(new Long[0]);
        }
        return apiRequest;
    }

}
