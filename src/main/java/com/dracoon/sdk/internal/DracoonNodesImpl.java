package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.mapper.NodeListMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import retrofit2.Call;
import retrofit2.Response;

class DracoonNodesImpl implements DracoonClient.Nodes {

    private DracoonClientImpl mClient;
    private DracoonService mService;

    DracoonNodesImpl(DracoonClientImpl client) {
        mClient = client;
        mService = client.getDracoonService();
    }

    @Override
    public NodeList getRootNodes() throws DracoonException {
        return getChildNodes(0L);
    }

    @Override
    public NodeList getChildNodes(long parentNodeId) throws DracoonException {
        String accessToken = mClient.getAccessToken();
        Call<ApiNodeList> call = mService.getChildNodes(accessToken, parentNodeId, 1, null, null,
                null);
        Response<ApiNodeList> response = DracoonServiceHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ApiNodeList data = response.body();

        return NodeListMapper.fromApi(data);
    }

    @Override
    public Node getNode(long nodeId) throws DracoonException {
        String accessToken = mClient.getAccessToken();
        Call<ApiNode> call = mService.getNode(accessToken, nodeId);
        Response<ApiNode> response = DracoonServiceHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ApiNode data = response.body();

        return NodeMapper.fromApi(data);
    }

}
