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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.CryptoException;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
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
import com.dracoon.sdk.internal.model.ApiCreateNodeCommentRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiFileIdFileKey;
import com.dracoon.sdk.internal.model.ApiFileKey;
import com.dracoon.sdk.internal.model.ApiMissingFileKeys;
import com.dracoon.sdk.internal.model.ApiMoveNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeComment;
import com.dracoon.sdk.internal.model.ApiNodeCommentList;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiSetFileKeysRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateNodeCommentRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUserIdFileId;
import com.dracoon.sdk.internal.model.ApiUserIdFileIdFileKey;
import com.dracoon.sdk.internal.model.ApiUserIdUserPublicKey;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.internal.validator.BaseValidator;
import com.dracoon.sdk.internal.validator.FileValidator;
import com.dracoon.sdk.internal.validator.FolderValidator;
import com.dracoon.sdk.internal.validator.NodeValidator;
import com.dracoon.sdk.internal.validator.RoomValidator;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateNodeCommentRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileDownloadStream;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeComment;
import com.dracoon.sdk.model.NodeCommentList;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateNodeCommentRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import retrofit2.Call;
import retrofit2.Response;

class DracoonNodesImpl extends DracoonRequestHandler implements DracoonClient.Nodes {

    private static final String LOG_TAG = DracoonNodesImpl.class.getSimpleName();

    private static final String MEDIA_URL_TEMPLATE = "%s/mediaserver/image/%s/%dx%d";

    private final Map<String, UploadThread> mUploads = new HashMap<>();
    private final Map<String, DownloadThread> mDownloads = new HashMap<>();

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

        String filter = filters != null ? filters.toString() : null;
        Call<ApiNodeList> call = mService.getNodes(parentNodeId, 0, filter, null, offset, limit);
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

        Call<ApiNode> call = mService.getNode(nodeId);
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

        ApiCreateRoomRequest apiRequest = RoomMapper.toApiCreateRoomRequest(request);
        Call<ApiNode> call = mService.createRoom(apiRequest);
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

        ApiUpdateRoomRequest apiRequest = RoomMapper.toApiUpdateRoomRequest(request);
        Call<ApiNode> call = mService.updateRoom(request.getId(), apiRequest);
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

        ApiCreateFolderRequest apiRequest = FolderMapper.toApiCreateFolderRequest(request);
        Call<ApiNode> call = mService.createFolder(apiRequest);
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

        ApiUpdateFolderRequest apiRequest = FolderMapper.toApiUpdateFolderRequest(request);
        Call<ApiNode> call = mService.updateFolder(request.getId(), apiRequest);
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

        ApiUpdateFileRequest apiRequest = FileMapper.toApiUpdateFileRequest(request);
        Call<ApiNode> call = mService.updateFile(request.getId(), apiRequest);
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

        ApiDeleteNodesRequest apiRequest = NodeMapper.toApiDeleteNodesRequest(request);
        Call<Void> call = mService.deleteNodes(apiRequest);
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
    public void deleteNode(long nodeId) throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodeId(nodeId);

        Call<Void> call = mService.deleteNode(nodeId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesDeleteError(response);
            String errorText = String.format("Deletion of node '%d' failed with '%s'!", nodeId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @Override
    public Node copyNodes(CopyNodesRequest request) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateCopyRequest(request);

        ApiCopyNodesRequest apiRequest = NodeMapper.toApiCopyNodesRequest(request);
        Call<ApiNode> call = mService.copyNodes(request.getTargetNodeId(), apiRequest);
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

        ApiMoveNodesRequest apiRequest = NodeMapper.toApiMoveNodesRequest(request);
        Call<ApiNode> call = mService.moveNodes(request.getTargetNodeId(), apiRequest);
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
        UserPublicKey userPublicKey = getUploadUserPublicKey(request.getParentId());
        PlainFileKey plainFileKey = createUploadFileKey(userPublicKey);

        UploadThread uploadThread = new UploadThread(mClient, id, request, length, userPublicKey,
                plainFileKey, is);
        uploadThread.addCallback(callback);

        Node node;
        try {
            node = uploadThread.runSync();
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
            throws DracoonCryptoException, DracoonNetIOException, DracoonApiException {
        UserPublicKey userPublicKey = getUploadUserPublicKey(request.getParentId());
        PlainFileKey plainFileKey = createUploadFileKey(userPublicKey);

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

        UploadThread uploadThread = new UploadThread(mClient, id, request, length, userPublicKey,
                plainFileKey, is);
        uploadThread.addCallback(callback);
        uploadThread.addCallback(internalCallback);

        mUploads.put(id, uploadThread);

        uploadThread.start();
    }

    @Override
    public void cancelUploadFileAsync(String id) {
        UploadThread uploadThread = mUploads.get(id);
        if (uploadThread == null) {
            return;
        }

        if (uploadThread.isAlive()) {
            uploadThread.interrupt();
        }
        mUploads.remove(id);
    }

    @Override
    public FileUploadStream createFileUploadStream(String id, FileUploadRequest request, long length,
            FileUploadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient.assertApiVersionSupported();

        FileValidator.validateUploadRequest(request);

        UserPublicKey userPublicKey = getUploadUserPublicKey(request.getParentId());
        PlainFileKey plainFileKey = createUploadFileKey(userPublicKey);

        UploadStream uploadStream = new UploadStream(mClient, id, request, length, userPublicKey,
                plainFileKey);

        if (callback != null) {
            uploadStream.addCallback(callback);
        }

        uploadStream.start();

        return uploadStream;
    }

    private UserPublicKey getUploadUserPublicKey(long parentNodeId) throws DracoonNetIOException,
            DracoonApiException {
        boolean isEncryptedUpload = isNodeEncrypted(parentNodeId);
        if (!isEncryptedUpload) {
            return null;
        }

        UserKeyPair userKeyPair = mClient.getAccountImpl().getPreferredUserKeyPair();
        return userKeyPair.getUserPublicKey();
    }

    private PlainFileKey createUploadFileKey(UserPublicKey userPublicKey)
            throws DracoonCryptoException {
        if (userPublicKey == null) {
            return null;
        }
        PlainFileKey.Version version = DracoonClientImpl.determinePlainFileKeyVersion(
                userPublicKey.getVersion());
        return Crypto.generateFileKey(version);
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
        PlainFileKey plainFileKey = getDownloadFileKey(nodeId);

        DownloadThread downloadThread = new DownloadThread(mClient, id, nodeId, plainFileKey, os);
        if (callback != null) {
            downloadThread.addCallback(callback);
        }

        try {
            downloadThread.runSync();
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
        PlainFileKey plainFileKey = getDownloadFileKey(nodeId);

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

        DownloadThread downloadThread = new DownloadThread(mClient, id, nodeId, plainFileKey, os);
        downloadThread.addCallback(stoppedCallback);
        if (callback != null) {
            downloadThread.addCallback(callback);
        }

        mDownloads.put(id, downloadThread);

        downloadThread.start();
    }

    @Override
    public void cancelDownloadFileAsync(String id) {
        DownloadThread downloadThread = mDownloads.get(id);
        if (downloadThread == null) {
            return;
        }

        if (downloadThread.isAlive()) {
            downloadThread.interrupt();
        }
        mDownloads.remove(id);
    }

    @Override
    public FileDownloadStream createFileDownloadStream(String id, long nodeId,
            FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient.assertApiVersionSupported();

        PlainFileKey plainFileKey = getDownloadFileKey(nodeId);

        DownloadStream downloadStream = new DownloadStream(mClient, id, nodeId, plainFileKey);

        if (callback != null) {
            downloadStream.addCallback(callback);
        }

        downloadStream.start();

        return downloadStream;
    }

    private PlainFileKey getDownloadFileKey(long nodeId) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        boolean isEncryptedDownload = isNodeEncrypted(nodeId);
        if (!isEncryptedDownload) {
            return null;
        }

        String userPrivateKeyPassword = mClient.getEncryptionPasswordOrAbort();

        EncryptedFileKey encFileKey = getFileKey(nodeId);

        UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.determineUserKeyPairVersion(
                encFileKey.getVersion());
        UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair(
                userKeyPairVersion);

        return decryptFileKey(nodeId, encFileKey, userKeyPair.getUserPrivateKey(),
                userPrivateKeyPassword);
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

        String filter = filters != null ? filters.toString() : null;
        Call<ApiNodeList> call = mService.searchNodes(searchString, parentNodeId, -1, filter, null,
                offset, limit);
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
    public boolean generateMissingFileKeys(int limit) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        return generateMissingFileKeysInternally(null, limit);
    }

    @Override
    public boolean generateMissingFileKeys(long nodeId, int limit) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        return generateMissingFileKeysInternally(nodeId, limit);
    }

    private boolean generateMissingFileKeysInternally(Long nodeId, Integer limit)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();

        List<UserKeyPair> userKeyPairs = mClient.getAccountImpl().getAndCheckUserKeyPairs();
        Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys = convertUserPrivateKeys(
                userKeyPairs);
        String userPrivateKeyPassword = mClient.getEncryptionPasswordOrAbort();

        boolean isFinished = false;
        Long batchOffset = 0L;
        Long batchLimit = 10L;
        while (!isFinished) {
            isFinished = generateMissingFileKeysBatch(userPrivateKeys,
                    userPrivateKeyPassword, nodeId, batchOffset, batchLimit);
            batchOffset = batchOffset + batchLimit;
            if (limit != null && batchOffset > limit) {
                break;
            }
        }
        return isFinished;
    }

    private static Map<UserKeyPair.Version, UserPrivateKey> convertUserPrivateKeys(
            List<UserKeyPair> userKeyPairs) {
        Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys = new HashMap<>();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            UserPrivateKey userPrivateKey = userKeyPair.getUserPrivateKey();
            userPrivateKeys.put(userPrivateKey.getVersion(), userPrivateKey);
        }
        return userPrivateKeys;
    }

    private boolean generateMissingFileKeysBatch(Map<UserKeyPair.Version,
            UserPrivateKey> userPrivateKeys, String userPrivateKeyPassword, Long nodeId,
            Long offset, Long limit) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        ApiMissingFileKeys apiMissingFileKeys = getMissingFileKeysBatch(nodeId, offset, limit);
        if (apiMissingFileKeys.items.isEmpty()) {
            return true;
        }

        List<ApiUserIdFileId> apiUserIdFileIds = apiMissingFileKeys.items;
        Map<Long, List<UserPublicKey>> usersPublicKeys = convertUserPublicKeys(
                apiMissingFileKeys.users);
        Map<Long, List<EncryptedFileKey>> encFilesKeys = convertFileKeys(apiMissingFileKeys.files);
        Map<Long, PlainFileKey> plainFileKeys = decryptFileKeys(encFilesKeys, userPrivateKeys,
                userPrivateKeyPassword);

        List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys = new ArrayList<>();

        for (ApiUserIdFileId apiUserIdFileId : apiUserIdFileIds) {
            List<UserPublicKey> userPublicKeys = usersPublicKeys.get(apiUserIdFileId.userId);
            PlainFileKey plainFileKey = plainFileKeys.get(apiUserIdFileId.fileId);
            if (userPublicKeys == null || plainFileKey == null) {
                continue;
            }

            for (UserPublicKey userPublicKey : userPublicKeys) {
                EncryptedFileKey encFileKey = encryptFileKey(apiUserIdFileId.fileId, plainFileKey,
                        userPublicKey);

                ApiFileKey apiFileKey = FileMapper.toApiFileKey(encFileKey);

                ApiUserIdFileIdFileKey apiUserIdFileIdFileKey = new ApiUserIdFileIdFileKey();
                apiUserIdFileIdFileKey.userId = apiUserIdFileId.userId;
                apiUserIdFileIdFileKey.fileId = apiUserIdFileId.fileId;
                apiUserIdFileIdFileKey.fileKey = apiFileKey;

                apiUserIdFileIdFileKeys.add(apiUserIdFileIdFileKey);
            }
        }

        setFileKeysBatch(apiUserIdFileIdFileKeys);

        return false;
    }

    private ApiMissingFileKeys getMissingFileKeysBatch(Long nodeId, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        Call<ApiMissingFileKeys> call = mService.getMissingFileKeys(nodeId, offset, limit);
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

    private static Map<Long, List<UserPublicKey>> convertUserPublicKeys(
            List<ApiUserIdUserPublicKey> apiUserIdUserPublicKeys) {
        Map<Long, List<UserPublicKey>> usersPublicKeys = new HashMap<>();
        for (ApiUserIdUserPublicKey apiUserIdUserPublicKey : apiUserIdUserPublicKeys) {
            try {
                UserPublicKey userPublicKey = UserMapper.fromApiUserPublicKey(
                        apiUserIdUserPublicKey.publicKeyContainer);
                List<UserPublicKey> userPublicKeys = usersPublicKeys.get(apiUserIdUserPublicKey.id);
                if (userPublicKeys != null) {
                    userPublicKeys.add(userPublicKey);
                } else {
                    usersPublicKeys.put(apiUserIdUserPublicKey.id, Collections.singletonList(
                            userPublicKey));
                }
            } catch (UnknownVersionException e) {
                // Not supported public keys are ignored
            }
        }
        return usersPublicKeys;
    }

    private static Map<Long, List<EncryptedFileKey>> convertFileKeys(
            List<ApiFileIdFileKey> apiFileIdFileKeys) {
        Map<Long, List<EncryptedFileKey>> encFilesKeys = new HashMap<>();
        for (ApiFileIdFileKey apiFileIdFileKey : apiFileIdFileKeys) {
            try {
                EncryptedFileKey encFileKey = FileMapper.fromApiFileKey(
                        apiFileIdFileKey.fileKeyContainer);
                List<EncryptedFileKey> encFileKeys = encFilesKeys.get(apiFileIdFileKey.id);
                if (encFileKeys != null) {
                    encFileKeys.add(encFileKey);
                } else {
                    encFilesKeys.put(apiFileIdFileKey.id, Collections.singletonList(encFileKey));
                }
            } catch (UnknownVersionException e) {
                // Not supported public keys are ignored
            }
        }
        return encFilesKeys;
    }

    private Map<Long, PlainFileKey> decryptFileKeys(Map<Long, List<EncryptedFileKey>> encFilesKeys,
            Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys, String userPrivateKeyPassword)
            throws DracoonCryptoException {
        Map<Long, PlainFileKey> plainFileKeys = new HashMap<>();
        for (Map.Entry<Long, List<EncryptedFileKey>> encFileKeys : encFilesKeys.entrySet()) {
            for (EncryptedFileKey encFileKey : encFileKeys.getValue()) {
                UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.determineUserKeyPairVersion(
                        encFileKey.getVersion());

                UserPrivateKey userPrivateKey = userPrivateKeys.get(userKeyPairVersion);
                if (userPrivateKey != null) {
                    PlainFileKey plainFileKey = decryptFileKey(null, encFileKey, userPrivateKey,
                            userPrivateKeyPassword);
                    plainFileKeys.put(encFileKeys.getKey(), plainFileKey);
                    break;
                }
            }
        }
        return plainFileKeys;
    }

    private void setFileKeysBatch(List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys)
            throws DracoonNetIOException, DracoonApiException {
        ApiSetFileKeysRequest request = new ApiSetFileKeysRequest();
        request.items = apiUserIdFileIdFileKeys;
        Call<Void> call = mService.setFileKeys(request);
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

    public EncryptedFileKey getFileKey(long nodeId) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();

        Call<ApiFileKey> call = mService.getFileKey(nodeId);
        Response<ApiFileKey> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeyQueryError(response);
            String errorText = String.format("Query of file key for node '%d' failed with " +
                    "'%s'!", nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiFileKey data = response.body();

        try {
            return FileMapper.fromApiFileKey(data);
        } catch (UnknownVersionException e) {
            String errorText = String.format("Query of file key for node '%d' failed! File key " +
                    "version is unknown!", nodeId);
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public PlainFileKey decryptFileKey(Long nodeId, EncryptedFileKey encFileKeyFileKey,
            UserPrivateKey userPrivateKey, String userPrivateKeyPassword)
            throws DracoonCryptoException {
        try {
            return Crypto.decryptFileKey(encFileKeyFileKey, userPrivateKey, userPrivateKeyPassword);
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

        Call<Void> call = mService.markFavorite(nodeId);
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

        Call<Void> call = mService.unmarkFavorite(nodeId);
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

    // --- Comment methods ---

    @Override
    public NodeCommentList getNodeComments(long nodeId) throws DracoonNetIOException,
            DracoonApiException {
        return getNodeCommentsInternally(nodeId, null, null);
    }

    @Override
    public NodeCommentList getNodeComments(long nodeId, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getNodeCommentsInternally(nodeId, offset, limit);
    }

    private NodeCommentList getNodeCommentsInternally(long nodeId, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateNodeId(nodeId);
        NodeValidator.validateRange(offset, limit, true);

        Call<ApiNodeCommentList> call = mService.getNodeComments(nodeId, offset, limit);
        Response<ApiNodeCommentList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodeCommentsGetError(response);
            String errorText = String.format("Query of node comments for node '%d' failed with '%s'!",
                    nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNodeCommentList data = response.body();
        return NodeMapper.fromApiNodeCommentList(data);
    }

    @Override
    public NodeComment createNodeComment(CreateNodeCommentRequest request)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateCreateCommentRequest(request);

        ApiCreateNodeCommentRequest apiRequest = NodeMapper.toApiCreateNodeCommentRequest(request);
        Call<ApiNodeComment> call = mService.createNodeComment(request.getNodeId(), apiRequest);
        Response<ApiNodeComment> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodeCommentCreateError(response);
            String errorText = String.format("Creation of comment on node '%d' failed with '%s'!",
                    request.getNodeId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNodeComment data = response.body();

        return NodeMapper.fromApiNodeComment(data);
    }

    @Override
    public NodeComment updateNodeComment(UpdateNodeCommentRequest request)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        NodeValidator.validateUpdateCommentRequest(request);

        ApiUpdateNodeCommentRequest apiRequest = NodeMapper.toApiUpdateNodeCommentRequest(request);
        Call<ApiNodeComment> call = mService.updateNodeComment(request.getId(), apiRequest);
        Response<ApiNodeComment> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodeCommentUpdateError(response);
            String errorText = String.format("Update of comment '%d' failed with '%s'!",
                    request.getId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNodeComment data = response.body();

        return NodeMapper.fromApiNodeComment(data);
    }

    @Override
    public void deleteNodeComment(long commentId)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        BaseValidator.validateCommentId(commentId);

        Call<Void> call = mService.deleteNodeComment(commentId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodeCommentDeleteError(response);
            String errorText = String.format("Deletion of comment '%d' failed with '%s'!", commentId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
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
