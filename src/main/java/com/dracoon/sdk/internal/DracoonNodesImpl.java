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
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.mapper.FolderMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.mapper.RoomMapper;
import com.dracoon.sdk.internal.model.ApiCopyNodesRequest;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiMoveNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.validator.FileValidator;
import com.dracoon.sdk.internal.validator.FolderValidator;
import com.dracoon.sdk.internal.validator.NodeValidator;
import com.dracoon.sdk.internal.validator.RoomValidator;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.MoveNodesRequest;
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
import java.util.Objects;

class DracoonNodesImpl extends DracoonRequestHandler implements DracoonClient.Nodes {

    private static final String LOG_TAG = DracoonNodesImpl.class.getSimpleName();

    private Map<String, FileUpload> mUploads = new HashMap<>();
    private Map<String, FileDownload> mDownloads = new HashMap<>();

    DracoonNodesImpl(DracoonClientImpl client) {
        super(client);
    }

    // --- Query methods ---

    @Override
    public NodeList getNodes(long parentNodeId) throws DracoonNetIOException,
            DracoonApiException {
        return getNodesInternally(parentNodeId, null, null);
    }

    @Override
    public NodeList getNodes(long parentNodeId, int offset, int limit)
            throws DracoonNetIOException, DracoonApiException {
        return getNodesInternally(parentNodeId, offset, limit);
    }

    private NodeList getNodesInternally(long parentNodeId, Integer offset, Integer limit)
            throws DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        NodeValidator.validateGetChildRequest(parentNodeId);

        String accessToken = mClient.getAccessToken();
        Call<ApiNodeList> call = mService.getNodes(accessToken, parentNodeId, 0, null, null,
                offset, limit);
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
    public Node getNode(long nodeId) throws DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        NodeValidator.validateGetRequest(nodeId);

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

    @Override
    public Node getNode(String nodePath) throws DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        String[] nodePathParts = nodePath.split("/", -1);
        long parentNodeId = 0L;

        Node wantedNode = null;

        for (int i = 1; i < nodePathParts.length; i++) {
            if (nodePathParts[i].isEmpty()) {
                break;
            }

            NodeList nodes = getNodes(parentNodeId);

            Node node = null;
            for (int j = 0; j < nodes.getItems().size(); j++) {
                if (Objects.equals(nodes.getItems().get(j).getName(), nodePathParts[i])) {
                    node = nodes.getItems().get(j);
                    break;
                }
            }

            if (node == null) {
                break;
            }

            if (i == nodePathParts.length - 1) {
                wantedNode = node;
            }

            parentNodeId = node.getId();
        }

        if (wantedNode == null) {
            DracoonApiCode errorCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            String errorText = String.format("Query of node '%s' failed with '%s'!", nodePath,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return wantedNode;
    }

    private boolean isNodeEncrypted(long nodeId) throws DracoonNetIOException, DracoonApiException {
        Node node = getNode(nodeId);
        return node.isEncrypted();
    }

    // --- Room creation and update methods ---

    @Override
    public Node createRoom(CreateRoomRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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
    public Node updateRoom(UpdateRoomRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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

    // --- Folder creation and update methods ---

    @Override
    public Node createFolder(CreateFolderRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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
    public Node updateFolder(UpdateFolderRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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
    public Node updateFile(UpdateFileRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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
    public void deleteNodes(DeleteNodesRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

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

    @Override
    public Node copyNodes(CopyNodesRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

        NodeValidator.validateCopyRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiCopyNodesRequest apiRequest = NodeMapper.toApiCopyNodesRequest(request);
        Call<ApiNode> call = mService.copyNodes(accessToken, request.getTargetNodeId(), apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesCopyError(response);
            String errorText = String.format("Copy to node '%d' failed with '%s'!",
                    request.getTargetNodeId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            // TODO: Add conflict nodes to exception
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    @Override
    public Node moveNodes(MoveNodesRequest request) throws DracoonNetIOException,
            DracoonApiException {
        assertServerApiVersion();

        NodeValidator.validateMoveRequest(request);

        String accessToken = mClient.getAccessToken();
        ApiMoveNodesRequest apiRequest = NodeMapper.toApiMoveNodesRequest(request);
        Call<ApiNode> call = mService.moveNodes(accessToken, request.getTargetNodeId(), apiRequest);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesMoveError(response);
            String errorText = String.format("Move to node '%d' failed with '%s'!",
                    request.getTargetNodeId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            // TODO: Add conflict nodes to exception
            throw new DracoonApiException(errorCode);
        }

        ApiNode data = response.body();

        return NodeMapper.fromApiNode(data);
    }

    // --- File upload methods ---

    @Override
    public Node uploadFile(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        FileValidator.validateUploadRequest(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            userPublicKey = getUserKeyPair().getUserPublicKey();
        }

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
            FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        FileValidator.validateUploadRequest(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            userPublicKey = getUserKeyPair().getUserPublicKey();
        }

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
    public void cancelUploadFileAsync(String id) {
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
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException,
            DracoonFileIOException {
        assertServerApiVersion();

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
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonFileIOException, DracoonCryptoException {
        assertServerApiVersion();

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
    public void cancelDownloadFileAsync(String id) {
        FileDownload download = mDownloads.get(id);
        if (download == null) {
            return;
        }

        if (download.isAlive()) {
            download.interrupt();
        }
        mDownloads.remove(id);
    }

    // --- Search methods ---

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString)
            throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, null, null);
    }

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString, int offset, int limit)
            throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, offset, limit);
    }

    private NodeList searchNodesInternally(long parentNodeId, String searchString, Integer offset,
            Integer limit) throws DracoonNetIOException, DracoonApiException {
        assertServerApiVersion();

        NodeValidator.validateSearchRequest(parentNodeId, searchString);

        String accessToken = mClient.getAccessToken();
        Call<ApiNodeList> call = mService.searchNodes(accessToken, searchString, parentNodeId, -1,
                null, null, offset, limit);
        Response<ApiNodeList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("Node search '%s' in node '%d' failed with '%s'!",
                    searchString, parentNodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNodeList data = response.body();

        return NodeMapper.fromApiNodeList(data);
    }

    // --- Helper methods ---

    private UserKeyPair getUserKeyPair() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        UserKeyPair userKeyPair = mClient.getAccountImpl().getUserKeyPair();

        boolean isValid = mClient.getAccountImpl().checkUserKeyPairPassword(userKeyPair);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }

        return userKeyPair;
    }

    private InputStream getFileInputStream(File file) throws DracoonFileIOException {
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

    private OutputStream getFileOutputStream(File file) throws DracoonFileIOException {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new DracoonFileIOException("File cannot be opened.", e);
        }
    }

}
