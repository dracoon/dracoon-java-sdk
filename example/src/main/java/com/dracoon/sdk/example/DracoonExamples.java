package com.dracoon.sdk.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.filter.FavoriteStatusFilter;
import com.dracoon.sdk.filter.GetDownloadSharesFilter;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.GetUploadSharesFilter;
import com.dracoon.sdk.filter.NodeIdFilter;
import com.dracoon.sdk.filter.NodeNameFilter;
import com.dracoon.sdk.filter.NodeTypeFilter;
import com.dracoon.sdk.filter.SearchNodesFilters;
import com.dracoon.sdk.filter.ShareNameFilter;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateNodeCommentRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.DownloadShareList;
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
import com.dracoon.sdk.model.NodeType;
import com.dracoon.sdk.model.PasswordPolicies;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateNodeCommentRequest;
import com.dracoon.sdk.model.UpdateRoomConfigRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import com.dracoon.sdk.model.UploadShare;
import com.dracoon.sdk.model.UploadShareList;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserInfo;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;

/**
 * This class shows the usage of the Dracoon SDK.<br>
 * <br>
 * (The example uses the Access Token Mode for authorization. For a example of the more complex
 * Authorization Code Mode see {@link OAuthExamples}.)<br>
 * <br>
 * Notice: For the sake of simplicity error handling is ignored.
 */
@SuppressWarnings("unused")
public class DracoonExamples {

    private static final String SERVER_URL = "https://dracoon.team";
    private static final String ACCESS_TOKEN = "access-token";

    private static final UserKeyPairAlgorithm.Version ENCRYPTION_VERSION =
            UserKeyPairAlgorithm.Version.RSA2048;
    private static final String ENCRYPTION_PASSWORD = "encryption-password";

    public static void main(String[] args) throws Exception {
        DracoonAuth auth = new DracoonAuth(ACCESS_TOKEN);

        DracoonClient client = new DracoonClient.Builder(new URL(SERVER_URL))
                .log(new Logger(Logger.DEBUG))
                .auth(auth)
                .encryptionPassword(ENCRYPTION_PASSWORD)
                .build();

        //getServerData(client);
        //getServerSettings(client);
        //getServerDefaults(client);
        //getServerPasswordPolicies(client);
        //getServerAvailableUserKeyPairAlgorithms(client);

        //checkAuth(client);

        //getUserAccount(client);
        //getCustomerAccount(client);

        //getUserKeyPairAlgorithmVersions(client);
        //setUserKeyPair(client, ENCRYPTION_VERSION);
        //deleteUserKeyPair(client, ENCRYPTION_VERSION);
        //checkUserKeyPairPassword(client, ENCRYPTION_VERSION);
        //checkUserKeyPairPassword(client, ENCRYPTION_VERSION, ENCRYPTION_PASSWORD);

        //setUserAvatar(client);
        //deleteUserAvatar(client);

        //listNodes(client);
        //listNodesPaged(client);
        //getNode(client);
        //getNodeNotFound(client);

        //getNodeByPath(client);
        //getNodeByPathNotFound(client);

        //getNodesWithFilter(client);

        //createRoom(client);
        //updateRoom(client);
        //updateRoomConfig(client);
        //createFolder(client);
        //updateFolder(client);
        //updateFile(client);
        //updateFileInvalidName(client);
        //deleteNodes(client);
        //deleteNode(client);
        //copyNodes(client);
        //moveNodes(client);

        //uploadFile(client);
        //downloadFile(client);
        //uploadFileWithStream(client);
        //downloadFileWithStream(client);

        //searchNodes(client);
        //searchNodesPaged(client);

        //searchNodesWithFilter(client);

        //markFavorite(client);
        //unmarkFavorite(client);
        //getFavorites(client);
        //getFavoritesPaged(client);
        //getNodeComments(client);
        //getNodeCommentsPaged(client);
        //createNodeComment(client);
        //updateNodeComment(client);
        //deleteNodeComment(client);

        //buildMediaUrl(client);

        //createDownloadShare(client);
        //createDownloadShareEncrypted(client);
        //getDownloadShares(client);
        //getDownloadShareQrCode(client);
        //deleteDownloadShare(client);
        //createUploadShare(client);
        //getUploadShares(client);
        //getUploadShareQrCode(client);
        //deleteUploadShare(client);

        //generateMissingFileKeys(client);
        //generateMissingFileKeysForOneNode(client);

        //getUserAvatar(client);
    }

    private static void getServerData(DracoonClient client) throws DracoonException {
        String serverVersion = client.server().getVersion();
        System.out.println("Server version: " + serverVersion);

        Date serverDate = client.server().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("Server date: " + df.format(serverDate));
    }

    private static void getServerSettings(DracoonClient client) throws DracoonException {
        ServerGeneralSettings settings = client.server().settings().getGeneralSettings();
        System.out.println("Server general settings:");
        System.out.printf("isSharePasswordSmsEnabled: %s\n", settings.isSharePasswordSmsEnabled());
        System.out.printf("isCryptoEnabled: %s\n", settings.isCryptoEnabled());
        System.out.printf("isMediaServerEnabled: %s\n", settings.isMediaServerEnabled());
        System.out.printf("isWeakPasswordEnabled: %s\n", settings.isWeakPasswordEnabled());
    }

    private static void getServerDefaults(DracoonClient client) throws DracoonException {
        ServerDefaults defaults = client.server().settings().getDefaults();
        System.out.println("Server defaults:");
        System.out.printf("downloadShareExpirationPeriod: %s\n",
                defaults.getDownloadShareExpirationPeriod());
        System.out.printf("uploadShareExpirationPeriod: %s\n",
                defaults.getUploadShareExpirationPeriod());
        System.out.printf("fileExpirationPeriod: %s\n",
                defaults.getFileExpirationPeriod());
    }

    private static void getServerPasswordPolicies(DracoonClient client) throws DracoonException {
        PasswordPolicies encryptionPasswordPolicies = client.server().policies()
                .getEncryptionPasswordPolicies();
        System.out.println("Encryption password policies:");
        System.out.printf("minLength: %d\n", encryptionPasswordPolicies.getMinLength());
        System.out.printf("characterTypes: %s\n", encryptionPasswordPolicies.getCharacterTypes()
                .toArray());
        System.out.println("rejectUserInfo: " + encryptionPasswordPolicies.getRejectUserInfo());
        System.out.println("rejectKeyboardPatterns: " + encryptionPasswordPolicies
                .getRejectKeyboardPatterns());
        System.out.println("rejectDictionaryWords: " + encryptionPasswordPolicies
                .getRejectDictionaryWords());

        PasswordPolicies sharesPasswordPolicies = client.server().policies()
                .getSharesPasswordPolicies();
        System.out.println("Share password policies:");
        System.out.printf("minLength: %d\n", sharesPasswordPolicies.getMinLength());
        System.out.printf("characterTypes: %s\n", sharesPasswordPolicies.getCharacterTypes()
                .toArray());
        System.out.println("rejectUserInfo: " + sharesPasswordPolicies.getRejectUserInfo());
        System.out.println("rejectKeyboardPatterns: " + sharesPasswordPolicies
                .getRejectKeyboardPatterns());
        System.out.println("rejectDictionaryWords: " + sharesPasswordPolicies
                .getRejectDictionaryWords());
    }

    private static void getServerAvailableUserKeyPairAlgorithms(DracoonClient client)
            throws DracoonException {
        List<UserKeyPairAlgorithm> algorithms = client.server().settings()
                .getAvailableUserKeyPairAlgorithms();
        System.out.println("Available user key pair algorithms:");
        algorithms.forEach(a -> System.out.printf("- %s (%s)", a.getVersion().getValue(),
                a.getState().getValue()));
    }

    private static void checkAuth(DracoonClient client) throws DracoonException {
        try {
            client.checkAuthValid();
            System.out.println("Authorization is still valid.");
        } catch (DracoonApiException e) {
            if (e.getCode().isAuthError()) {
                System.out.println("Authorization is no longer valid.");
            } else {
                throw e;
            }
        }
    }

    private static void getUserAccount(DracoonClient client) throws DracoonException {
        UserAccount userAccount = client.account().getUserAccount();
        System.out.println("User: id=" + userAccount.getId() + ", " +
                "gender=" + userAccount.getGender() + ", " +
                "first name=" + userAccount.getFirstName() + ", " +
                "last name=" + userAccount.getLastName() + ", " +
                "email=" + userAccount.getEmail());
        System.out.println("User roles: " + userAccount.getUserRoles().toString());
    }

    private static void getCustomerAccount(DracoonClient client) throws DracoonException {
        CustomerAccount customerAccount = client.account().getCustomerAccount();
        System.out.println("Customer: id=" + customerAccount.getId() + ", " +
                "name=" + customerAccount.getName() + ", " +
                "user accounts=" + customerAccount.getUserAccountsUsed() + "/" +
                customerAccount.getUserAccountsLimit() + ", " +
                "space=" + customerAccount.getSpaceUsed() + "/" +
                customerAccount.getSpaceLimit());
    }

    private static void getUserKeyPairAlgorithmVersions(DracoonClient client) throws DracoonException {
        List<UserKeyPairAlgorithm.Version> version = client.account().getUserKeyPairAlgorithmVersions();
        System.out.println("User has key pairs for following crypto versions:");
        version.forEach(v -> System.out.printf("- %s", v.getValue()));
    }

    private static void setUserKeyPair(DracoonClient client, UserKeyPairAlgorithm.Version version)
            throws DracoonException {
        client.account().setUserKeyPair(version);
    }

    private static void deleteUserKeyPair(DracoonClient client, UserKeyPairAlgorithm.Version version)
            throws DracoonException {
        client.account().deleteUserKeyPair(version);
    }

    private static void checkUserKeyPairPassword(DracoonClient client,
            UserKeyPairAlgorithm.Version version) throws DracoonException {
        boolean isPasswordValid = client.account().checkUserKeyPairPassword(version);
        System.out.println("Valid encryption password: " + isPasswordValid);
    }

    private static void checkUserKeyPairPassword(DracoonClient client,
            UserKeyPairAlgorithm.Version version, String password) throws DracoonException {
        boolean isPasswordValid = client.account().checkUserKeyPairPassword(version, password);
        System.out.println("Valid encryption password: " + isPasswordValid);
    }

    private static void setUserAvatar(DracoonClient client) throws DracoonException, IOException {
        File f = new File("C:\\temp\\avatar.png");
        byte[] avatarImage = readBytes(f);

        client.account().setUserAvatar(avatarImage);
    }

    private static void deleteUserAvatar(DracoonClient client) throws DracoonException {
        client.account().deleteUserAvatar();
    }

    private static void listNodes(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;

        NodeList nodeList = client.nodes().getNodes(parentNodeId);
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void listNodesPaged(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;

        long page = 1;
        long pageSize = 4;

        long offset = 0;
        long total;

        do {
            NodeList nodeList = client.nodes().getNodes(parentNodeId, offset, pageSize);
            total = nodeList.getTotal();

            System.out.printf("Nodes page %d:\n", page);
            for (Node node : nodeList.getItems()) {
                System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
            }

            page++;
            offset = offset + pageSize;
        } while (offset < total);
    }

    private static void getNode(DracoonClient client) throws DracoonException {
        Node node = client.nodes().getNode(1L);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void getNodeNotFound(DracoonClient client) throws DracoonException {
        try {
            client.nodes().getNode(123456789L);
        } catch (DracoonApiException e) {
            System.err.println("Failed to query node: " + e.getCode().getText());
        }
    }

    private static void getNodeByPath(DracoonClient client) throws DracoonException {
        Node node = client.nodes().getNode("/room/folder/file.txt");
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void getNodeByPathNotFound(DracoonClient client) throws DracoonException {
        try {
            client.nodes().getNode("/not-existing");
        } catch (DracoonApiException e) {
            System.err.println("Failed to query node: " + e.getCode().getText());
        }
    }

    private static void getNodesWithFilter(DracoonClient client) throws DracoonException {
        GetNodesFilters filters = new GetNodesFilters();
        filters.addNodeTypeFilter(new NodeTypeFilter.Builder()
                .eq(NodeType.ROOM).or().eq(NodeType.FOLDER).build());
        filters.addNodeNameFilter(new NodeNameFilter.Builder()
                .cn("Test").build());

        NodeList nodeList = client.nodes().getNodes(0L, filters);
        System.out.println("Nodes:");
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void createRoom(DracoonClient client) throws DracoonException {
        List<Long> adminIds = new ArrayList<>();
        adminIds.add(1L);

        CreateRoomRequest request = new CreateRoomRequest.Builder("Test-Room")
                .notes("This is a test room.")
                .adminUserIds(adminIds)
                .build();
        Node node = client.nodes().createRoom(request);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void updateRoom(DracoonClient client) throws DracoonException {
        UpdateRoomRequest request = new UpdateRoomRequest.Builder(1L)
                .name("Test-Room-123")
                .notes("This note has been changed.")
                .build();
        Node node = client.nodes().updateRoom(request);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void updateRoomConfig(DracoonClient client) throws DracoonException {
        UpdateRoomConfigRequest request = new UpdateRoomConfigRequest.Builder(1L)
                .classification(Classification.CONFIDENTIAL)
                .build();
        Node node = client.nodes().updateRoomConfig(request);
        System.out.println("id=" + node.getId() + ", classification=" + node.getClassification());
    }

    private static void createFolder(DracoonClient client) throws DracoonException {
        CreateFolderRequest request = new CreateFolderRequest.Builder(1L, "Test-Folder")
                .notes("This is a test folder.")
                .build();
        Node node = client.nodes().createFolder(request);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void updateFolder(DracoonClient client) throws DracoonException {
        UpdateFolderRequest request = new UpdateFolderRequest.Builder(1L)
                .name("Test-Folder-123")
                .notes("This note has been changed.")
                .build();
        Node node = client.nodes().updateFolder(request);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void updateFile(DracoonClient client) throws DracoonException {
        UpdateFileRequest request = new UpdateFileRequest.Builder(1L)
                .name("Test-File-123.txt")
                .classification(Classification.CONFIDENTIAL)
                .notes("This note has been changed.")
                .build();
        Node node = client.nodes().updateFile(request);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void updateFileInvalidName(DracoonClient client) throws DracoonException {
        UpdateFileRequest request = new UpdateFileRequest.Builder(1L)
                .name("<Invalid-Name>.txt")
                .build();
        Node node = client.nodes().updateFile(request);
    }

    private static void deleteNodes(DracoonClient client) throws DracoonException {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        DeleteNodesRequest request = new DeleteNodesRequest.Builder(ids)
                .build();
        client.nodes().deleteNodes(request);
    }

    private static void deleteNode(DracoonClient client) throws DracoonException {
        client.nodes().deleteNode(1L);
    }

    private static void copyNodes(DracoonClient client) throws DracoonException {
        CopyNodesRequest request = new CopyNodesRequest.Builder(1L)
                .addSourceNode(2L)
                .addSourceNode(3L)
                .addSourceNode(4L)
                .build();
        Node node = client.nodes().copyNodes(request);
        System.out.println("id=" + node.getId() + ", size=" + node.getSize());
    }

    private static void moveNodes(DracoonClient client) throws DracoonException {
        MoveNodesRequest request = new MoveNodesRequest.Builder(1L)
                .addSourceNode(2L)
                .addSourceNode(3L)
                .addSourceNode(4L)
                .build();
        Node node = client.nodes().moveNodes(request);
        System.out.println("id=" + node.getId() + ", size=" + node.getSize());
    }

    private static void uploadFile(DracoonClient client) throws DracoonException {
        FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt")
                .build();

        File file = new File("C:\\temp\\test.txt");

        FileUploadCallback callback = new FileUploadCallback() {
            @Override
            public void onStarted(String id) {
                System.out.println(String.format("Upload %s started.", id));
            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {
                System.out.println(String.format("Upload %s running. (%d/%d)", id, bytesSend,
                        bytesTotal));
            }

            @Override
            public void onFinished(String id, Node node) {
                System.out.println(String.format("Upload %s finished.", id));
            }

            @Override
            public void onCanceled(String id) {
                System.out.println(String.format("Upload %s canceled.", id));
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                System.out.println(String.format("Upload %s failed.", id));
            }
        };

        Node node = client.nodes().uploadFile("1", request, file, callback);

        System.out.println(String.format("Node uploaded: id=%d, path=%s%s", node.getId(),
                node.getParentPath(), node.getName()));
    }

    private static void uploadFileWithStream(DracoonClient client) throws DracoonException,
            IOException {
        File file = new File("C:\\temp\\test.txt");

        FileUploadRequest request = new FileUploadRequest.Builder(1L, "file.txt")
                .build();

        FileInputStream is = new FileInputStream(file);
        FileUploadStream us = client.nodes().createFileUploadStream("1", request, file.length(),
                null);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            us.write(buffer, 0, bytesRead);
        }
        us.complete();

        us.close();
        is.close();
    }

    private static void downloadFile(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        File file = new File("C:\\temp\\test.txt");

        FileDownloadCallback callback = new FileDownloadCallback() {
            @Override
            public void onStarted(String id) {
                System.out.println(String.format("Download %s started.", id));
            }

            @Override
            public void onRunning(String id, long bytesSend, long bytesTotal) {
                System.out.println(String.format("Download %s running. (%d/%d)", id, bytesSend,
                        bytesTotal));
            }

            @Override
            public void onFinished(String id) {
                System.out.println(String.format("Download %s finished.", id));
            }

            @Override
            public void onCanceled(String id) {
                System.out.println(String.format("Download %s canceled.", id));
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                System.out.println(String.format("Download %s failed.", id));
            }
        };

        client.nodes().downloadFile("1", nodeId, file, callback);

        System.out.println(String.format("Node downloaded: id=%d", nodeId));
    }

    private static void downloadFileWithStream(DracoonClient client) throws DracoonException,
            IOException {
        long nodeId = 1L;

        File file = new File("C:\\temp\\test.txt");

        FileDownloadStream ds = client.nodes().createFileDownloadStream("1", nodeId, null);
        FileOutputStream os = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = ds.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        os.close();
        ds.close();
    }

    private static void searchNodes(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;
        String searchString = "test*";

        NodeList nodeList = client.nodes().searchNodes(parentNodeId, searchString);
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void searchNodesPaged(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;
        String searchString = "test*";

        long page = 1;
        long pageSize = 4;

        long offset = 0;
        long total;

        do {
            NodeList nodeList = client.nodes().searchNodes(parentNodeId, searchString, offset,
                    pageSize);
            total = nodeList.getTotal();

            System.out.printf("Nodes search page %d:\n", page);
            for (Node node : nodeList.getItems()) {
                System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
            }

            page++;
            offset = offset + pageSize;
        } while (offset < total);
    }

    private static void searchNodesWithFilter(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;
        String searchString = "*";
        SearchNodesFilters filters = new SearchNodesFilters();
        filters.addFavoriteStatusFilter(new FavoriteStatusFilter.Builder()
                .eq(true).build());

        NodeList nodeList = client.nodes().searchNodes(parentNodeId, searchString, filters);
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void markFavorite(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        client.nodes().markFavorite(nodeId);
    }

    private static void unmarkFavorite(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        client.nodes().unmarkFavorite(nodeId);
    }

    private static void getFavorites(DracoonClient client) throws DracoonException {
        NodeList nodeList = client.nodes().getFavorites();
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void getFavoritesPaged(DracoonClient client) throws DracoonException {
        long page = 1;
        long pageSize = 4;

        long offset = 0;
        long total;

        do {
            NodeList nodeList = client.nodes().getFavorites(offset, pageSize);
            total = nodeList.getTotal();

            System.out.printf("Favorites page %d:\n", page);
            for (Node node : nodeList.getItems()) {
                System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
            }

            page++;
            offset = offset + pageSize;
        } while (offset < total);
    }

    private static void getNodeComments(DracoonClient client) throws DracoonException {
        long nodeId = 1L;
        NodeCommentList nodeCommentList = client.nodes().getNodeComments(nodeId);
        for (NodeComment nodeComment : nodeCommentList.getItems()) {
            System.out.println(nodeComment.getId() + ": " + nodeComment.getText());
        }
    }

    private static void getNodeCommentsPaged(DracoonClient client) throws DracoonException {
        long nodeId = 1L;
        long page = 1;
        long pageSize = 3;

        long offset = 0;
        long total;

        do {
            NodeCommentList nodeCommentList = client.nodes().getNodeComments(nodeId, offset,
                    pageSize);
            total = nodeCommentList.getTotal();

            System.out.printf("Node comments page %d:\n", page);
            for (NodeComment nodeComment : nodeCommentList.getItems()) {
                System.out.println(nodeComment.getId() + ": " + nodeComment.getText());
            }

            page++;
            offset = offset + pageSize;
        } while (offset < total);
    }

    private static void createNodeComment(DracoonClient client) throws DracoonException {
        long nodeId = 1L;
        CreateNodeCommentRequest request = new CreateNodeCommentRequest.Builder(nodeId,
                "This is a comment!")
                .build();
        NodeComment nodeComment = client.nodes().createNodeComment(request);
        System.out.println(nodeComment.getId() + ": " + nodeComment.getText());
    }

    private static void updateNodeComment(DracoonClient client) throws DracoonException {
        long commentId = 1L;
        UpdateNodeCommentRequest request = new UpdateNodeCommentRequest.Builder(commentId,
                "This is a changed comment!")
                .build();
        NodeComment nodeComment = client.nodes().updateNodeComment(request);
        System.out.println(nodeComment.getId() + ": " + nodeComment.getText());
    }

    private static void deleteNodeComment(DracoonClient client) throws DracoonException {
        long commentId = 1L;
        client.nodes().deleteNodeComment(commentId);
    }

    private static void buildMediaUrl(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        ServerGeneralSettings settings = client.server().settings().getGeneralSettings();
        if (!settings.isMediaServerEnabled()) {
            System.err.println("Media server is not available!");
            return;
        }

        Node node = client.nodes().getNode(nodeId);
        URL mediaUrl = client.nodes().buildMediaUrl(node.getMediaToken(), 640, 480);

        System.out.println(String.format("Media token URL for node '%d': %s", nodeId, mediaUrl));
    }

    private static void createDownloadShare(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        CreateDownloadShareRequest request = new CreateDownloadShareRequest.Builder(nodeId)
                .name("Test Download Share")
                .notes("This is a note.")
                .expirationDate(new Date(1861916400000L))
                .showCreatorName(true)
                .notifyCreator(true)
                .accessPassword("Lni+36D8fq")
                .build();

        DownloadShare dlShare = client.shares().createDownloadShare(request);
        System.out.println(String.format("Download share: id=%d, access_key=%s", dlShare.getId(),
                dlShare.getAccessKey()));
    }

    private static void createDownloadShareEncrypted(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        CreateDownloadShareRequest request = new CreateDownloadShareRequest.Builder(nodeId)
                .name("Test Download Share Encrypted")
                .notes("This is a note.")
                .expirationDate(new Date(1861916400000L))
                .showCreatorName(true)
                .notifyCreator(true)
                .encryptionPassword("Lni+36D8fq")
                .build();

        DownloadShare dlShare = client.shares().createDownloadShare(request);
        System.out.println(String.format("Download share: id=%d, access_key=%s", dlShare.getId(),
                dlShare.getAccessKey()));
    }

    private static void getDownloadShares(DracoonClient client) throws DracoonException {
        long targetNodeId = 1L;

        GetDownloadSharesFilter filters = new GetDownloadSharesFilter();
        filters.addNodeIdFilter(new NodeIdFilter.Builder().eq(targetNodeId).build());

        DownloadShareList sharesList = client.shares().getDownloadShares(filters);
        System.out.println("Download shares:");
        for (DownloadShare share : sharesList.getItems()) {
            System.out.println(share.getId() + ": " + share.getName());
        }
    }

    private static void getDownloadShareQrCode(DracoonClient client) throws DracoonException,
            IOException {
        long shareId = 1L;

        byte[] qrCodeImage = client.shares().getDownloadShareQrCode(shareId);

        File f = new File("C:\\temp\\qrcode.png");
        writeBytes(f, qrCodeImage);
    }

    private static void deleteDownloadShare(DracoonClient client) throws DracoonException {
        long shareId = 1L;

        client.shares().deleteDownloadShare(shareId);
    }

    private static void createUploadShare(DracoonClient client) throws DracoonException {
        long targetNodeId = 1L;

        CreateUploadShareRequest request = new CreateUploadShareRequest.Builder(targetNodeId)
                .name("Test Upload Share")
                .notes("This is a note.")
                .expirationDate(new Date(1861916400000L))
                .filesExpirationPeriod(10)
                .maxUploads(5)
                .maxQuota(10240L)
                .showUploadedFiles(true)
                .showCreatorName(true)
                .notifyCreator(true)
                .accessPassword("Lni+36D8fq")
                .build();

        UploadShare ulShare = client.shares().createUploadShare(request);
        System.out.println(String.format("Upload share: id=%d, access_key=%s", ulShare.getId(),
                ulShare.getAccessKey()));
    }

    private static void getUploadShares(DracoonClient client) throws DracoonException {
        GetUploadSharesFilter filters = new GetUploadSharesFilter();
        filters.addNameFilter(new ShareNameFilter.Builder().cn("Test").build());

        UploadShareList sharesList = client.shares().getUploadShares(filters);
        System.out.println("Upload shares:");
        for (UploadShare share : sharesList.getItems()) {
            System.out.println(share.getId() + ": " + share.getName());
        }
    }

    private static void getUploadShareQrCode(DracoonClient client) throws DracoonException,
            IOException {
        long shareId = 1L;

        byte[] qrCodeImage = client.shares().getUploadShareQrCode(shareId);

        File f = new File("C:\\temp\\qrcode.png");
        writeBytes(f, qrCodeImage);
    }

    private static void deleteUploadShare(DracoonClient client) throws DracoonException {
        long shareId = 1L;

        client.shares().deleteUploadShare(shareId);
    }

    private static void generateMissingFileKeys(DracoonClient client) throws DracoonException {
        boolean finished = client.nodes().generateMissingFileKeys(10);
        System.out.println("All missing file keys have been created: " + finished);
    }

    private static void generateMissingFileKeysForOneNode(DracoonClient client)
            throws DracoonException {
        long nodeId = 1L;

        boolean finished = client.nodes().generateMissingFileKeys(nodeId, 10);
        System.out.println("All missing file keys have been created: " + finished);
    }

    private static void getUserAvatar(DracoonClient client) throws DracoonException, IOException {
        Node node = client.nodes().getNode(1L);
        UserInfo userInfo = node.getCreatedBy();

        System.out.println("User info: id=" + userInfo.getId() + ", avatarUuid=" +
                userInfo.getAvatarUuid());

        byte[] avatarImage = client.users().getUserAvatar(userInfo.getId(), userInfo.getAvatarUuid());

        File f = new File("C:\\temp\\avatar.png");
        writeBytes(f, avatarImage);
    }

    // --- Helper methods ---

    private static byte[] readBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            int c;
            byte[] b = new byte[1024];
            while ((c = is.read(b)) != -1) {
                os.write(b, 0, c);
            }
        } finally {
            is.close();
        }
        return os.toByteArray();
    }

    private static void writeBytes(File file, byte[] bytes) throws IOException {
        OutputStream os = new FileOutputStream(file);
        os.write(bytes);
        os.close();
    }

}
