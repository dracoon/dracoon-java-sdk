package com.dracoon.sdk.internal.model;

import java.util.List;

@SuppressWarnings("unused")
public class ApiMoveNodesRequest {
    public List<Long> nodeIds;
    public List<ApiMoveNode> items;
    public String resolutionStrategy;
}
