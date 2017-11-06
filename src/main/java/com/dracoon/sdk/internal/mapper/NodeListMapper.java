package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;

import java.util.ArrayList;

public class NodeListMapper {

    public static NodeList fromApi(ApiNodeList apiNodeList) {
        NodeList nodeList = new NodeList();
        nodeList.setOffset(apiNodeList.range.offset);
        nodeList.setLimit(apiNodeList.range.limit);
        nodeList.setTotal(apiNodeList.range.total);
        ArrayList<Node> items = new ArrayList<>();
        for (ApiNode apiNode : apiNodeList.items) {
            items.add(NodeMapper.fromApi(apiNode));
        }
        nodeList.setItems(items);
        return nodeList;
    }

}
