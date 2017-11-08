package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonFileNotFoundException;
import com.dracoon.sdk.error.DracoonInvalidArgException;
import com.dracoon.sdk.internal.mapper.NodeListMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.model.UploadCallback;
import com.dracoon.sdk.model.UploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class DracoonNodesImpl implements DracoonClient.Nodes {

    private DracoonClientImpl mClient;
    private DracoonService mService;

    private Map<String, Upload> mUploads = new HashMap<>();

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
            DracoonApiCode code = DracoonErrorParser.parseNodesQueryError(response);
            throw new DracoonApiException(code);
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
            DracoonApiCode code = DracoonErrorParser.parseNodesQueryError(response);
            throw new DracoonApiException(code);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApi(data);
    }

    @Override
    public Node upload(String id, UploadRequest request, File file, UploadCallback callback)
            throws DracoonException {
        validateUpload(id, request, file);

        InputStream is = getUploadStream(file);

        Upload upload = new Upload(mClient, id, request, is);
        upload.addCallback(callback);

        return upload.runWithResult();
    }

    @Override
    public void startUploadAsync(String id, UploadRequest request, File file,
            UploadCallback callback) throws DracoonException {
        validateUpload(id, request, file);

        InputStream is = getUploadStream(file);

        UploadCallback stoppedCallback = new UploadCallback() {
            @Override
            public void onStarted(String id) {

            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {

            }

            @Override
            public void onFinished(String id, Node node) {
                mUploads.remove(id);
            }

            @Override
            public void onCanceled(String id) {
                mUploads.remove(id);
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                mUploads.remove(id);
            }
        };

        Upload upload = new Upload(mClient, id, request, is);
        upload.addCallback(callback);
        upload.addCallback(stoppedCallback);

        mUploads.put(id, upload);

        upload.start();
    }

    private void validateUpload(String id, UploadRequest request, File file)
            throws DracoonException {
        if (id == null || id.isEmpty()) {
            throw new DracoonInvalidArgException("Upload ID cannot be null.");
        }
        if (request == null) {
            throw new DracoonInvalidArgException("Upload request cannot be null");
        }
        if (file == null) {
            throw new DracoonInvalidArgException("Upload file cannot be null");
        }
    }

    private InputStream getUploadStream(File file) throws DracoonException {
        if (!file.exists()) {
            throw new DracoonFileNotFoundException("File not found.");
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DracoonFileIOException("File cannot be opened.", e);
        }
    }

    @Override
    public void cancelUploadAsync(String id) throws DracoonException {
        Upload upload = mUploads.get(id);
        if (upload == null) {
            return;
        }

        if (upload.isAlive()) {
            upload.interrupt();
        }
        mUploads.remove(id);
    }

}
