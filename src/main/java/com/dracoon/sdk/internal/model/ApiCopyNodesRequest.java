package com.dracoon.sdk.internal.model;

import java.util.List;

@SuppressWarnings("unused")
public class ApiCopyNodesRequest {
    public List<Long> nodeIds;
    public List<ApiCopyNode> items;
    public String resolutionStrategy;
}
