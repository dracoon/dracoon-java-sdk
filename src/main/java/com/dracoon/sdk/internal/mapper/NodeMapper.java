package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.model.Node;

public class NodeMapper {

    public static Node fromApi(ApiNode apiNode) {
        Node node = new Node();
        node.setId(apiNode.id);
        node.setType(apiNode.type);
        node.setParentId(apiNode.parentId);
        node.setParentPath(apiNode.parentPath);
        node.setName(apiNode.name);
        return node;
    }


}
