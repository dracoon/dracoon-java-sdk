package com.dracoon.sdk.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoException;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
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
import com.dracoon.sdk.filter.FavoriteStatusFilter;
import com.dracoon.sdk.filter.Filters;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.NodeParentPathFilter;
import com.dracoon.sdk.filter.SearchNodesFilters;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.mapper.FolderMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.mapper.RoomMapper;
import com.dracoon.sdk.internal.mapper.UserMapper;
import com.dracoon.sdk.internal.model.ApiCopyNodesRequest;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiFileIdFileKey;
import com.dracoon.sdk.internal.model.ApiFileKey;
import com.dracoon.sdk.internal.model.ApiMissingFileKeys;
import com.dracoon.sdk.internal.model.ApiMoveNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiSetFileKeysRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUserIdFileId;
import com.dracoon.sdk.internal.model.ApiUserIdFileIdFileKey;
import com.dracoon.sdk.internal.model.ApiUserIdUserPublicKey;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.internal.validator.FileValidator;
import com.dracoon.sdk.internal.validator.FolderValidator;
import com.dracoon.sdk.internal.validator.NodeValidator;
import com.dracoon.sdk.internal.validator.RoomValidator;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileDownloadStream;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import retrofit2.Call;
import retrofit2.Response;

class DracoonNodesImpl extends DracoonRequestHandler implements DracoonClient.Nodes {

    private static final String LOG_TAG = DracoonNodesImpl.class.getSimpleName();

    private static final String MEDIA_URL_TEMPLATE = "%s/mediaserver/image/%s/%dx%d";

    private Map<String, FileUpload> mUploads = new HashMap<>();
    private Map<String, FileDownload> mDownloads = new HashMap<>();

    DracoonNodesImpl(DracoonClientImpl client) {
        super(client);
    }

    // --- Query methods ---

    @Override
    public NodeList getNodes(long parentNodeId) throws DracoonNetIOException,
            DracoonApiException {
        return getNodesInternally(parentNodeId, null, null, null);
    }

    @Override
    public NodeList getNodes(long parentNodeId, GetNodesFilters filters)
            throws DracoonNetIOException, DracoonApiException {
        return getNodesInternally(parentNodeId, filters, null, null);
    }

    @Override
    public NodeList getNodes(long parentNodeId, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getNodesInternally(parentNodeId, null, offset, limit);
    }

    @Override
    public NodeList getNodes(long parentNodeId, GetNodesFilters filters, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getNodesInternally(parentNodeId, filters, offset, limit);
    }

    private NodeList getNodesInternally(long parentNodeId, Filters filters, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateParentNodeId(parentNodeId);
        NodeValidator.validateRange(offset, limit, true);

        String auth = mClient.buildAuthString();
        String filter = filters != null ? filters.toString() : null;
        Call<ApiNodeList> call = mService.getNodes(auth, parentNodeId, 0, filter, null,
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
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodeId(nodeId);

        String auth = mClient.buildAuthString();
        Call<ApiNode> call = mService.getNode(auth, nodeId);
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
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodePath(nodePath);

        int slashPos = nodePath.lastIndexOf('/');
        String path = nodePath.substring(0, slashPos + 1);
        String name = nodePath.substring(slashPos + 1, nodePath.length());

        NodeParentPathFilter pathFilter = new NodeParentPathFilter.Builder()
                .eq(path)
                .build();
        SearchNodesFilters filters = new SearchNodesFilters();
        filters.addNodeParentPathFilter(pathFilter);

        NodeList nodeList = searchNodes(0L, name, filters);

        if (nodeList.getItems().isEmpty()) {
            DracoonApiCode errorCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            String errorText = String.format("Query of node '%s' failed with '%s'!", nodePath,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return nodeList.getItems().get(0);
    }

    public boolean isNodeEncrypted(long nodeId) throws DracoonNetIOException, DracoonApiException {
        Node node = getNode(nodeId);
        return node.isEncrypted();
    }

    // --- Room creation and update methods ---

    @Override
    public Node createRoom(CreateRoomRequest request) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        RoomValidator.validateCreateRequest(request);

        String auth = mClient.buildAuthString();
        ApiCreateRoomRequest apiRequest = RoomMapper.toApiCreateRoomRequest(request);
        Call<ApiNode> call = mService.createRoom(auth, apiRequest);
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
        mClient.assertApiVersionSupported();

        RoomValidator.validateUpdateRequest(request);

        String auth = mClient.buildAuthString();
        ApiUpdateRoomRequest apiRequest = RoomMapper.toApiUpdateRoomRequest(request);
        Call<ApiNode> call = mService.updateRoom(auth, request.getId(), apiRequest);
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
        mClient.assertApiVersionSupported();

        FolderValidator.validateCreateRequest(request);

        String auth = mClient.buildAuthString();
        ApiCreateFolderRequest apiRequest = FolderMapper.toApiCreateFolderRequest(request);
        Call<ApiNode> call = mService.createFolder(auth, apiRequest);
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
        mClient.assertApiVersionSupported();

        FolderValidator.validateUpdateRequest(request);

        String auth = mClient.buildAuthString();
        ApiUpdateFolderRequest apiRequest = FolderMapper.toApiUpdateFolderRequest(request);
        Call<ApiNode> call = mService.updateFolder(auth, request.getId(), apiRequest);
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
        mClient.assertApiVersionSupported();

        FileValidator.validateUpdateRequest(request);

        String auth = mClient.buildAuthString();
        ApiUpdateFileRequest apiRequest = FileMapper.toApiUpdateFileRequest(request);
        Call<ApiNode> call = mService.updateFile(auth, request.getId(), apiRequest);
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
        mClient.assertApiVersionSupported();

        NodeValidator.validateDeleteRequest(request);

        String auth = mClient.buildAuthString();
        ApiDeleteNodesRequest apiRequest = NodeMapper.toApiDeleteNodesRequest(request);
        Call<Void> call = mService.deleteNodes(auth, apiRequest);
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
        mClient.assertApiVersionSupported();

        NodeValidator.validateCopyRequest(request);

        String auth = mClient.buildAuthString();
        ApiCopyNodesRequest apiRequest = NodeMapper.toApiCopyNodesRequest(request);
        Call<ApiNode> call = mService.copyNodes(auth, request.getTargetNodeId(), apiRequest);
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
        mClient.assertApiVersionSupported();

        NodeValidator.validateMoveRequest(request);

        String auth = mClient.buildAuthString();
        ApiMoveNodesRequest apiRequest = NodeMapper.toApiMoveNodesRequest(request);
        Call<ApiNode> call = mService.moveNodes(auth, request.getTargetNodeId(), apiRequest);
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
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        return uploadFileInternally(id, request, is, length, true, callback);
    }

    @Override
    public Node uploadFile(String id, FileUploadRequest request, InputStream is, long length,
            FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(id, request, is);

        return uploadFileInternally(id, request, is, length, false, callback);
    }

    private Node uploadFileInternally(String id, FileUploadRequest request, InputStream is,
            long length, boolean close, FileUploadCallback callback) throws DracoonFileIOException,
            DracoonCryptoException, DracoonNetIOException, DracoonApiException {
        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            userPublicKey = userKeyPair.getUserPublicKey();
        }

        FileUpload upload;
        if (isEncryptedUpload) {
            upload = new EncFileUpload(mClient, id, request, is, length, userPublicKey);
        } else {
            upload = new FileUpload(mClient, id, request, is, length);
        }

        upload.addCallback(callback);

        Node node;
        try {
            node = upload.runSync();
        } finally {
            closeStream(is, close);
        }

        return node;
    }

    @Override
    public void startUploadFileAsync(String id, FileUploadRequest request, File file,
            FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(id, request, file);

        InputStream is = getFileInputStream(file);
        long length = file.length();

        startUploadFileAsyncInternally(id, request, is, length, true, callback);
    }

    @Override
    public void startUploadFileAsync(String id, FileUploadRequest request, InputStream is,
            long length, FileUploadCallback callback) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(id, request, is);

        startUploadFileAsyncInternally(id, request, is, length, false, callback);
    }

    private void startUploadFileAsyncInternally(String id, FileUploadRequest request,
            final InputStream is, long length, final boolean close, FileUploadCallback callback)
            throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());
        UserPublicKey userPublicKey = null;
        if (isEncryptedUpload) {
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            userPublicKey = userKeyPair.getUserPublicKey();
        }

        FileUploadCallback internalCallback = new FileUploadCallback() {
            @Override
            public void onStarted(String id) {

            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {

            }

            @Override
            public void onFinished(String id, Node node) {
                closeStream(is, close);
                mUploads.remove(id);
            }

            @Override
            public void onCanceled(String id) {
                closeStream(is, close);
                mUploads.remove(id);
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                closeStream(is, close);
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
        upload.addCallback(internalCallback);

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

    @Override
    public FileUploadStream createFileUploadStream(String id, FileUploadRequest request, long length,
            FileUploadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(request);

        boolean isEncryptedUpload = isNodeEncrypted(request.getParentId());

        UserPublicKey userPublicKey = null;
        PlainFileKey plainFileKey = null;
        if (isEncryptedUpload) {
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            userPublicKey = userKeyPair.getUserPublicKey();
            plainFileKey = createFileKey(userPublicKey.getVersion());
        }

        StreamUpload streamUpload = new StreamUpload(mClient, id, request, length, userPublicKey,
                plainFileKey);

        if (callback != null) {
            streamUpload.addCallback(callback);
        }

        streamUpload.start();

        return streamUpload;
    }

    // --- File download methods ---

    @Override
    public void downloadFile(String id, long nodeId, File file, FileDownloadCallback callback)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException,
            DracoonFileIOException {
        mClient.assertApiVersionSupported();

        FileValidator.validateDownloadRequest(id, file);

        OutputStream os = getFileOutputStream(file);

        downloadFileInternally(id, nodeId, os, true, callback);
    }

    @Override
    public void downloadFile(String id, long nodeId, OutputStream os, FileDownloadCallback callback)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException,
            DracoonFileIOException {
        mClient.assertApiVersionSupported();

        FileValidator.validateDownloadRequest(id, os);

        downloadFileInternally(id, nodeId, os, false, callback);
    }

    private void downloadFileInternally(String id, long nodeId, OutputStream os, boolean close,
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        boolean isEncryptedDownload = isNodeEncrypted(nodeId);
        UserPrivateKey userPrivateKey = null;
        if (isEncryptedDownload) {
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            userPrivateKey = userKeyPair.getUserPrivateKey();
        }

        FileDownload download;
        if (isEncryptedDownload) {
            download = new EncFileDownload(mClient, id, nodeId, os, userPrivateKey);
        } else {
            download = new FileDownload(mClient, id, nodeId, os);
        }

        download.addCallback(callback);

        try {
            download.runSync();
        } finally {
            closeStream(os, close);
        }
    }

    @Override
    public void startDownloadFileAsync(String id, long nodeId, File file,
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        mClient.assertApiVersionSupported();

        FileValidator.validateDownloadRequest(id, file);

        OutputStream os = getFileOutputStream(file);

        startDownloadFileAsyncInternally(id, nodeId, os, true, callback);
    }

    @Override
    public void startDownloadFileAsync(String id, long nodeId, OutputStream os,
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient.assertApiVersionSupported();

        FileValidator.validateDownloadRequest(id, os);

        startDownloadFileAsyncInternally(id, nodeId, os, false, callback);
    }

    private void startDownloadFileAsyncInternally(String id, long nodeId, final OutputStream os,
            final boolean close, FileDownloadCallback callback) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        boolean isEncryptedDownload = isNodeEncrypted(nodeId);
        UserPrivateKey userPrivateKey = null;
        if (isEncryptedDownload) {
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            userPrivateKey = userKeyPair.getUserPrivateKey();
        }

        FileDownloadCallback stoppedCallback = new FileDownloadCallback() {
            @Override
            public void onStarted(String id) {

            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {

            }

            @Override
            public void onFinished(String id) {
                closeStream(os, close);
                mDownloads.remove(id);
            }

            @Override
            public void onCanceled(String id) {
                closeStream(os, close);
                mDownloads.remove(id);
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                closeStream(os, close);
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

    @Override
    public FileDownloadStream createFileDownloadStream(String id, long nodeId,
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient.assertApiVersionSupported();

        boolean isEncryptedDownload = isNodeEncrypted(nodeId);

        PlainFileKey plainFileKey = null;
        if (isEncryptedDownload) {
            String userPrivateKeyPassword = mClient.getEncryptionPassword();
            UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            UserPrivateKey userPrivateKey = userKeyPair.getUserPrivateKey();
            EncryptedFileKey encryptedFileKey = getFileKey(nodeId);
            plainFileKey = decryptFileKey(nodeId, encryptedFileKey, userPrivateKey,
                    userPrivateKeyPassword);
        }

        StreamDownload streamDownload = new StreamDownload(mClient, id, nodeId, plainFileKey);

        if (callback != null) {
            streamDownload.addCallback(callback);
        }

        streamDownload.start();

        return streamDownload;
    }

    // --- Search methods ---

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString)
            throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, null, null, null);
    }

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString, SearchNodesFilters filters)
            throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, filters, null, null);
    }

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, null, offset, limit);
    }

    @Override
    public NodeList searchNodes(long parentNodeId, String searchString, SearchNodesFilters filters,
            long offset, long limit) throws DracoonNetIOException, DracoonApiException {
        return searchNodesInternally(parentNodeId, searchString, filters, offset, limit);
    }

    private NodeList searchNodesInternally(long parentNodeId, String searchString,
            SearchNodesFilters filters, Long offset, Long limit) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateSearchRequest(parentNodeId, searchString);
        NodeValidator.validateRange(offset, limit, true);

        String auth = mClient.buildAuthString();
        String filter = filters != null ? filters.toString() : null;
        Call<ApiNodeList> call = mService.searchNodes(auth, searchString, parentNodeId, -1, filter,
                null, offset, limit);
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

    // --- File key generation methods ---

    @Override
    public void generateMissingFileKeys() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        generateMissingFileKeysInternally(null, null);
    }

    @Override
    public void generateMissingFileKeys(int limit) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        generateMissingFileKeysInternally(null, limit);
    }

    @Override
    public void generateMissingFileKeys(long nodeId) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        generateMissingFileKeysInternally(nodeId, null);
    }

    @Override
    public void generateMissingFileKeys(long nodeId, int limit) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        generateMissingFileKeysInternally(nodeId, limit);
    }

    private void generateMissingFileKeysInternally(Long nodeId, Integer limit)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();

        Long batchOffset = 0L;
        Long batchLimit = 10L;

        UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
        String userPrivateKeyPassword = mClient.getEncryptionPassword();

        boolean isFinished = false;
        while (!isFinished) {
            isFinished = generateMissingFileKeysBatch(nodeId, batchOffset, batchLimit,
                    userKeyPair.getUserPrivateKey(), userPrivateKeyPassword);
            batchOffset = batchOffset + batchLimit;
            if (limit != null && batchOffset > limit) {
                break;
            }
        }
    }

    private boolean generateMissingFileKeysBatch(Long nodeId, Long offset, Long limit,
            UserPrivateKey userPrivateKey, String userPrivateKeyPassword)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        ApiMissingFileKeys apiMissingFileKeys = getMissingFileKeysBatch(nodeId, offset, limit);
        if (apiMissingFileKeys.items.isEmpty()) {
            return true;
        }

        List<ApiUserIdFileId> apiUserIdFileIds = apiMissingFileKeys.items;
        Map<Long, UserPublicKey> userPublicKeys = convertUserPublicKeys(apiMissingFileKeys.users);
        Map<Long, EncryptedFileKey> encryptedFileKeys = convertFileKeys(apiMissingFileKeys.files);
        Map<Long, PlainFileKey> plainFileKeys = decryptFileKeys(encryptedFileKeys, userPrivateKey,
                userPrivateKeyPassword);

        List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys = new ArrayList<>();

        for (ApiUserIdFileId apiUserIdFileId : apiUserIdFileIds) {
            UserPublicKey userPublicKey = userPublicKeys.get(apiUserIdFileId.userId);
            PlainFileKey plainFileKey = plainFileKeys.get(apiUserIdFileId.fileId);

            EncryptedFileKey encryptedFileKey = encryptFileKey(apiUserIdFileId.fileId, plainFileKey,
                    userPublicKey);

            ApiFileKey apiFileKey = FileMapper.toApiFileKey(encryptedFileKey);

            ApiUserIdFileIdFileKey apiUserIdFileIdFileKey = new ApiUserIdFileIdFileKey();
            apiUserIdFileIdFileKey.userId = apiUserIdFileId.userId;
            apiUserIdFileIdFileKey.fileId = apiUserIdFileId.fileId;
            apiUserIdFileIdFileKey.fileKey = apiFileKey;

            apiUserIdFileIdFileKeys.add(apiUserIdFileIdFileKey);
        }

        setFileKeysBatch(apiUserIdFileIdFileKeys);

        return false;
    }

    private ApiMissingFileKeys getMissingFileKeysBatch(Long nodeId, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();
        Call<ApiMissingFileKeys> call = mService.getMissingFileKeys(auth, nodeId, offset,
                limit);
        Response<ApiMissingFileKeys> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseMissingFileKeysQueryError(response);
            String errorText = String.format("Query of missing file keys failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private Map<Long, UserPublicKey> convertUserPublicKeys(
            List<ApiUserIdUserPublicKey> apiUserIdUserPublicKeys) {
        Map<Long, UserPublicKey> userPublicKeys = new HashMap<>();
        for (ApiUserIdUserPublicKey apiUserIdUserPublicKey : apiUserIdUserPublicKeys) {
            UserPublicKey userPublicKey = UserMapper.fromApiUserPublicKey(
                    apiUserIdUserPublicKey.publicKeyContainer);
            userPublicKeys.put(apiUserIdUserPublicKey.id, userPublicKey);
        }
        return userPublicKeys;
    }

    private Map<Long, EncryptedFileKey> convertFileKeys(List<ApiFileIdFileKey> apiFileIdFileKeys) {
        Map<Long, EncryptedFileKey> encryptedFileKeys = new HashMap<>();
        for (ApiFileIdFileKey apiFileIdFileKey : apiFileIdFileKeys) {
            EncryptedFileKey encryptedFileKey = FileMapper.fromApiFileKey(
                    apiFileIdFileKey.fileKeyContainer);
            encryptedFileKeys.put(apiFileIdFileKey.id, encryptedFileKey);
        }
        return encryptedFileKeys;
    }

    private Map<Long, PlainFileKey> decryptFileKeys(Map<Long, EncryptedFileKey> encryptedFileKeys,
            UserPrivateKey userPrivateKey, String userPrivateKeyPassword)
            throws DracoonCryptoException {
        Map<Long, PlainFileKey> plainFileKeys = new HashMap<>();
        for (Map.Entry<Long, EncryptedFileKey> encryptedFileKey : encryptedFileKeys.entrySet()) {
            PlainFileKey plainFileKey = decryptFileKey(null, encryptedFileKey.getValue(),
                    userPrivateKey, userPrivateKeyPassword);
            plainFileKeys.put(encryptedFileKey.getKey(), plainFileKey);
        }
        return plainFileKeys;
    }

    private void setFileKeysBatch(List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys)
            throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();
        ApiSetFileKeysRequest request = new ApiSetFileKeysRequest();
        request.items = apiUserIdFileIdFileKeys;
        Call<Void> call = mService.setFileKeys(auth, request);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeysSetError(response);
            String errorText = String.format("Setting missing file keys failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    // --- File key methods ---

    public PlainFileKey createFileKey(String version) throws DracoonCryptoException {
        try {
            return Crypto.generateFileKey(version);
        } catch (CryptoException e) {
            String errorText = String.format("Creation of file key failed! %s", e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public EncryptedFileKey getFileKey(long nodeId) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<ApiFileKey> call = mService.getFileKey(auth, nodeId);
        Response<ApiFileKey> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeyQueryError(response);
            String errorText = String.format("Query of file key for node '%d' failed with " +
                    "'%s'!", nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiFileKey data = response.body();

        return FileMapper.fromApiFileKey(data);
    }

    public PlainFileKey decryptFileKey(Long nodeId, EncryptedFileKey encryptedFileKeyFileKey,
            UserPrivateKey userPrivateKey, String userPrivateKeyPassword)
            throws DracoonCryptoException {
        try {
            return Crypto.decryptFileKey(encryptedFileKeyFileKey, userPrivateKey,
                    userPrivateKeyPassword);
        } catch (CryptoException e) {
            String nodeErrorText = nodeId != null ? String.format("for node '%d' ", nodeId) : "";
            String errorText = String.format("Decryption of file key " + nodeErrorText +
                    "failed! %s", nodeId, e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public EncryptedFileKey encryptFileKey(Long nodeId, PlainFileKey plainFileKey,
            UserPublicKey userPublicKey) throws DracoonCryptoException {
        try {
            return Crypto.encryptFileKey(plainFileKey, userPublicKey);
        } catch (CryptoException e) {
            String nodeErrorText = nodeId != null ? String.format("for node '%d' ", nodeId) : "";
            String errorText = String.format("Encryption of file key " + nodeErrorText +
                    "failed! %s", nodeId, e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    // --- Favorite methods ---

    @Override
    public void markFavorite(long nodeId) throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodeId(nodeId);

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.markFavorite(auth, nodeId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFavoriteMarkError(response);
            String errorText = String.format("Mark node %s as favorite failed with '%s'!", nodeId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            if (!errorCode.isValidationError()) {
                throw new DracoonApiException(errorCode);
            }
        }
    }

    @Override
    public void unmarkFavorite(long nodeId) throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodeId(nodeId);

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.unmarkFavorite(auth, nodeId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            // Ignore errors with response code 400
            // (This check fixes a bad designed API. It can be removed after the API has been
            // reworked.)
            if (response.code() == HttpStatus.BAD_REQUEST.getNumber()) {
                return;
            }

            DracoonApiCode errorCode = mErrorParser.parseFavoriteMarkError(response);
            String errorText = String.format("Unmark node %s as favorite failed with '%s'!", nodeId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @Override
    public NodeList getFavorites() throws DracoonNetIOException, DracoonApiException {
        SearchNodesFilters filters = new SearchNodesFilters();
        filters.addFavoriteStatusFilter(new FavoriteStatusFilter.Builder()
                .eq(true).build());

        return searchNodes(0L, "*", filters);
    }

    @Override
    public NodeList getFavorites(long offset, long limit) throws DracoonNetIOException,
            DracoonApiException {
        SearchNodesFilters filters = new SearchNodesFilters();
        filters.addFavoriteStatusFilter(new FavoriteStatusFilter.Builder()
                .eq(true).build());

        return searchNodes(0L, "*", filters, offset, limit);
    }

    // --- Media methods ---

    @Override
    public URL buildMediaUrl(String mediaToken, int width, int height) {
        NodeValidator.validateMediaUrlRequest(mediaToken, width, height);

        String url = String.format(MEDIA_URL_TEMPLATE, mClient.getServerUrl(), mediaToken, width,
                height);
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    // --- Helper methods ---

    private static InputStream getFileInputStream(File file) throws DracoonFileIOException {
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

    private static OutputStream getFileOutputStream(File file) throws DracoonFileIOException {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new DracoonFileIOException("File cannot be opened.", e);
        }
    }

    private static void closeStream(InputStream is, boolean close) {
        if (close) {
            StreamUtils.closeStream(is);
        }
    }

    private static void closeStream(OutputStream os, boolean close) {
        if (close) {
            StreamUtils.closeStream(os);
        }
    }

}
