package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonFileNotFoundException;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.mapper.FolderMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.mapper.RoomMapper;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.validator.FileValidator;
import com.dracoon.sdk.internal.validator.FolderValidator;
import com.dracoon.sdk.internal.validator.NodeValidator;
import com.dracoon.sdk.internal.validator.RoomValidator;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
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

class DracoonNodesImpl extends DracoonRequestHandler implements DracoonClient.Nodes {

    private static final String LOG_TAG = DracoonNodesImpl.class.getSimpleName();

    private Map<String, FileUpload> mUploads = new HashMap<>();
    private Map<String, FileDownload> mDownloads = new HashMap<>();

    DracoonNodesImpl(DracoonClientImpl client) {
        super(client);
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
        Response<ApiNodeList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("Query of child nodes of node '%d' failed with '%s'!",
                    parentNodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNodeList data = response.body();

        return NodeMapper.fromApiNodeList(data);
    }

    @Override
    public Node getNode(long nodeId) throws DracoonException {
        String accessToken = mClient.getAccessToken();
        Call<ApiNode> call = mService.getNode(accessToken, nodeId);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("Query of node '%d' failed with '%s'!", nodeId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    private boolean isNodeEncrypted(long nodeId) throws DracoonException {
        Node node = getNode(nodeId);
        return node.isEncrypted();
    }

    // --- Room creation and update methods ---

    @Override
    public Node createRoom(CreateRoomRequest request) throws DracoonException {
        RoomValidator.validateCreateRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiCreateRoomRequest apiRequest = RoomMapper.toApiCreateRoomRequest(request);
        Call<ApiNode> call = mService.createRoom(accessToken, apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseRoomCreateError(response);
            String errorText = String.format("Creation of room '%s' failed with '%s'!",
                    request.getName(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    @Override
    public Node updateRoom(UpdateRoomRequest request) throws DracoonException {
        RoomValidator.validateUpdateRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiUpdateRoomRequest apiRequest = RoomMapper.toApiUpdateRoomRequest(request);
        Call<ApiNode> call = mService.updateRoom(accessToken, request.getId(), apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseRoomUpdateError(response);
            String errorText = String.format("Update of room '%d' failed with '%s'!",
                    request.getId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    // --- Room creation and update methods ---

    @Override
    public Node createFolder(CreateFolderRequest request) throws DracoonException {
        FolderValidator.validateCreateRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiCreateFolderRequest apiRequest = FolderMapper.toApiCreateFolderRequest(request);
        Call<ApiNode> call = mService.createFolder(accessToken, apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFolderCreateError(response);
            String errorText = String.format("Creation of folder '%s' failed with '%s'!",
                    request.getName(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    @Override
    public Node updateFolder(UpdateFolderRequest request) throws DracoonException {
        FolderValidator.validateUpdateRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiUpdateFolderRequest apiRequest = FolderMapper.toApiUpdateFolderRequest(request);
        Call<ApiNode> call = mService.updateFolder(accessToken, request.getId(), apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFolderUpdateError(response);
            String errorText = String.format("Update of folder '%d' failed with '%s'!",
                    request.getId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    // --- File update methods ---

    @Override
    public Node updateFile(UpdateFileRequest request) throws DracoonException {
        FileValidator.validateUpdateRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiUpdateFileRequest apiRequest = FileMapper.toApiUpdateFileRequest(request);
        Call<ApiNode> call = mService.updateFile(accessToken, request.getId(), apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileUpdateError(response);
            String errorText = String.format("Update of file '%d' failed with '%s'!",
                    request.getId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    // --- Node copy, move and deletion methods ---

    @Override
    public void deleteNodes(DeleteNodesRequest request) throws DracoonException {
        NodeValidator.validateDeleteRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiDeleteNodesRequest apiRequest = NodeMapper.toApiDeleteNodesRequest(request);
        Call<Void> call = mService.deleteNodes(accessToken, apiRequest);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesDeleteError(response);
            String errorText = String.format("Deletion of nodes %s failed with '%s'!",
                    request.getIds(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    // --- File upload methods ---

    @Override
    public Node uploadFile(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonException {
        FileValidator.validateUploadRequest(id, request, file);

        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            userPublicKey = getUserKeyPair().getUserPublicKey();
        }

        InputStream is = getFileInputStream(file);
        long length = file.length();

        FileUpload upload;
        if (isEncryptedUpload) {
            upload = new EncFileUpload(mClient, id, request, is, length, userPublicKey);
        } else {
            upload = new FileUpload(mClient, id, request, is, length);
        }

        upload.addCallback(callback);

        return upload.runSync();
    }

    @Override
    public void startUploadFileAsync(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonException {
        FileValidator.validateUploadRequest(id, request, file);

        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            userPublicKey = getUserKeyPair().getUserPublicKey();
        }

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

        FileUpload upload;
        if (isEncryptedUpload) {
            upload = new EncFileUpload(mClient, id, request, is, length, userPublicKey);
        } else {
            upload = new FileUpload(mClient, id, request, is, length);
        }

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
        boolean isEncryptedDownload = isNodeEncrypted(nodeId);
        UserPrivateKey userPrivateKey = null;
        if (isEncryptedDownload) {
            userPrivateKey = getUserKeyPair().getUserPrivateKey();
        }

        OutputStream os = getFileOutputStream(file);

        FileDownload download;
        if (isEncryptedDownload) {
            download = new EncFileDownload(mClient, id, nodeId, os, userPrivateKey);
        } else {
            download = new FileDownload(mClient, id, nodeId, os);
        }

        download.addCallback(callback);

        download.runSync();
    }

    @Override
    public void startDownloadFileAsync(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonException {
        boolean isEncryptedDownload = isNodeEncrypted(nodeId);
        UserPrivateKey userPrivateKey = null;
        if (isEncryptedDownload) {
            userPrivateKey = getUserKeyPair().getUserPrivateKey();
        }

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

        FileDownload download;
        if (isEncryptedDownload) {
            download = new EncFileDownload(mClient, id, nodeId, os, userPrivateKey);
        } else {
            download = new FileDownload(mClient, id, nodeId, os);
        }

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

    private UserKeyPair getUserKeyPair() throws DracoonException {
        UserKeyPair userKeyPair = mClient.getAccountImpl().getUserKeyPair();

        boolean isValid = mClient.getAccountImpl().checkUserKeyPairPassword(userKeyPair);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }

        return userKeyPair;
    }

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
