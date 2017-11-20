package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonFileNotFoundException;
import com.dracoon.sdk.internal.mapper.NodeListMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.validator.UploadValidator;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class DracoonNodesImpl implements DracoonClient.Nodes {

    private DracoonClientImpl mClient;
    private DracoonService mService;

    private Map<String, FileUpload> mUploads = new HashMap<>();
    private Map<String, FileDownload> mDownloads = new HashMap<>();

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
        Response<ApiNodeList> response = DracoonHttpHelper.executeRequest(call);

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
        Response<ApiNode> response = DracoonHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode code = DracoonErrorParser.parseNodesQueryError(response);
            throw new DracoonApiException(code);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApi(data);
    }

    // --- File upload methods ---

    @Override
    public Node uploadFile(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonException {
        UploadValidator.validate(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        FileUpload upload = new FileUpload(mClient, id, request, is, length);
        upload.addCallback(callback);

        return upload.runSync();
    }

    @Override
    public void startUploadFileAsync(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonException {
        UploadValidator.validate(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        FileUploadCallback stoppedCallback = new FileUploadCallback() {
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

        FileUpload upload = new FileUpload(mClient, id, request, is, length);
        upload.addCallback(callback);
        upload.addCallback(stoppedCallback);

        mUploads.put(id, upload);

        upload.start();
    }

    @Override
    public void cancelUploadFileAsync(String id) throws DracoonException {
        FileUpload upload = mUploads.get(id);
        if (upload == null) {
            return;
        }

        if (upload.isAlive()) {
            upload.interrupt();
        }
        mUploads.remove(id);
    }

    // --- File download methods ---

    @Override
    public void downloadFile(String id, long nodeId, File file, FileDownloadCallback callback)
            throws DracoonException {
        OutputStream os = getFileOutputStream(file);

        FileDownload download = new FileDownload(mClient, id, nodeId, os);
        download.addCallback(callback);

        download.runSync();
    }

    @Override
    public void startDownloadFileAsync(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonException {
        OutputStream os = getFileOutputStream(file);

        FileDownloadCallback stoppedCallback = new FileDownloadCallback() {
            @Override
            public void onStarted(String id) {

            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {

            }

            @Override
            public void onFinished(String id) {
                mDownloads.remove(id);
            }

            @Override
            public void onCanceled(String id) {
                mDownloads.remove(id);
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                mDownloads.remove(id);
            }
        };

        FileDownload download = new FileDownload(mClient, id, nodeId, os);
        download.addCallback(callback);
        download.addCallback(stoppedCallback);

        mDownloads.put(id, download);

        download.start();
    }

    @Override
    public void cancelDownloadFileAsync(String id) throws DracoonException {
        FileDownload download = mDownloads.get(id);
        if (download == null) {
            return;
        }

        if (download.isAlive()) {
            download.interrupt();
        }
        mDownloads.remove(id);
    }

    // --- Helper methods ---

    private InputStream getFileInputStream(File file) throws DracoonException {
        if (!file.exists()) {
            throw new DracoonFileNotFoundException("File not found.");
        }

        if (!file.canRead()) {
            throw new DracoonFileNotFoundException("File not readable.");
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DracoonFileIOException("File cannot be opened.", e);
        }
    }

    private OutputStream getFileOutputStream(File file) throws DracoonException {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new DracoonFileIOException("File cannot be opened.", e);
        }
    }

}
