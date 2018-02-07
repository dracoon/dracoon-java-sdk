package com.dracoon.sdk.example;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import com.dracoon.sdk.model.UserAccount;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String serverUrl = "https://dracoon.team";
        String accessToken = "";
        String encryptionPassword = "secret";

        DracoonClient client = new DracoonClient.Builder(serverUrl)
                .log(new Logger(Log.DEBUG))
                .accessToken(accessToken)
                .encryptionPassword(encryptionPassword)
                .httpRetryEnabled(true)
                .build();

        //getServerData(client);

        //getUserAccount(client);
        //getCustomerAccount(client);

        //setUserKeyPair(client);
        //checkUserKeyPair(client);
        //deleteUserKeyPair(client);

        listNodes(client);
        //listNodesPaged(client);
        //getNode(client);
        //getNodeNotFound(client);

        //getNodeByPath(client);
        //getNodeByPathNotFound(client);

        //createRoom(client);
        //updateRoom(client);
        //createFolder(client);
        //updateFolder(client);
        //updateFile(client);
        //deleteNodes(client);
        //copyNodes(client);
        //moveNodes(client);

        //uploadFile(client);
        //downloadFile(client);

        //searchNodes(client);
        //searchNodesPaged(client);

        //createDownloadShare(client);
        //createDownloadShareEncrypted(client);

        //generateMissingFileKeys(client);
        //generateMissingFileKeysForOneNode(client);
    }

    private static void getServerData(DracoonClient client) throws DracoonException {
        String serverVersion = client.server().getVersion();
        System.out.println("Server version: " + serverVersion);

        Date serverDate = client.server().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("Server date: " + df.format(serverDate));
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

    private static void setUserKeyPair(DracoonClient client) throws DracoonException {
        client.account().setUserKeyPair();
    }

    private static void checkUserKeyPair(DracoonClient client) throws DracoonException {
        boolean validPassword = client.account().checkUserKeyPairPassword();
        System.out.println("Valid encryption password: " + validPassword);
    }

    private static void deleteUserKeyPair(DracoonClient client) throws DracoonException {
        client.account().deleteUserKeyPair();
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

        NodeList nodeList1 = client.nodes().getNodes(parentNodeId, 0, 4);
        System.out.println("Nodes page 1:");
        for (Node node : nodeList1.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
        NodeList nodeList2 = client.nodes().getNodes(parentNodeId, 4, 4);
        System.out.println("Nodes page 2:");
        for (Node node : nodeList2.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void getNode(DracoonClient client) throws DracoonException {
        Node node = client.nodes().getNode(1);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void getNodeNotFound(DracoonClient client) throws DracoonException {
        try {
            client.nodes().getNode(123456789);
        } catch (DracoonException e) {
            System.err.println("Failed to query node: " + e.getMessage());
        }
    }

    private static void getNodeByPath(DracoonClient client) throws DracoonException {
        Node node = client.nodes().getNode("/room/folder/file.txt");
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void getNodeByPathNotFound(DracoonClient client) throws DracoonException {
        try {
            client.nodes().getNode("/not-existing");
        } catch (DracoonException e) {
            System.err.println("Failed to query node: " + e.getMessage());
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

    private static void deleteNodes(DracoonClient client) throws DracoonException {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        DeleteNodesRequest request = new DeleteNodesRequest.Builder(ids)
                .build();
        client.nodes().deleteNodes(request);
    }

    private static void copyNodes(DracoonClient client) throws DracoonException {
        CopyNodesRequest request = new CopyNodesRequest.Builder(1L)
                .addSourceNodeId(2L)
                .addSourceNodeId(3L)
                .addSourceNodeId(4L)
                .build();
        Node node = client.nodes().copyNodes(request);
        System.out.println("id=" + node.getId() + ", size=" + node.getSize());
    }

    private static void moveNodes(DracoonClient client) throws DracoonException {
        MoveNodesRequest request = new MoveNodesRequest.Builder(1L)
                .addSourceNodeId(2L)
                .addSourceNodeId(3L)
                .addSourceNodeId(4L)
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
                System.out.println(String.format("Upload %s failed.", id));
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                System.out.println(String.format("Upload %s canceled.", id));
            }
        };

        Node node = client.nodes().uploadFile("1", request, file, callback);

        System.out.println(String.format("Node uploaded: id=%d, path=%s%s", node.getId(),
                node.getParentPath(), node.getName()));
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
                System.out.println(String.format("Download %s failed.", id));
            }

            @Override
            public void onFailed(String id, DracoonException e) {
                System.out.println(String.format("Download %s canceled.", id));
            }
        };

        client.nodes().downloadFile("1", nodeId, file, callback);

        System.out.println(String.format("Node downloaded: id=%d", nodeId));
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

        NodeList nodeList1 = client.nodes().searchNodes(parentNodeId, searchString, 0, 0);
        System.out.println("Nodes search page 1:");
        for (Node node : nodeList1.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }

        NodeList nodeList2 = client.nodes().searchNodes(parentNodeId, searchString, 4, 4);
        System.out.println("Nodes search page 2:");
        for (Node node : nodeList2.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void createDownloadShare(DracoonClient client) throws DracoonException {
        long nodeId = 1L;

        CreateDownloadShareRequest request = new CreateDownloadShareRequest.Builder(nodeId)
                .name("Test Download Share")
                .notes("This is a note.")
                .expirationDate(new Date(1861916400000L))
                .showCreatorName(true)
                .notifyCreator(true)
                .accessPassword("secret")
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
                .encryptionPassword("secret")
                .build();

        DownloadShare dlShare = client.shares().createDownloadShare(request);
        System.out.println(String.format("Download share: id=%d, access_key=%s", dlShare.getId(),
                dlShare.getAccessKey()));
    }

    private static void generateMissingFileKeys(DracoonClient client) throws DracoonException {
        client.nodes().generateMissingFileKeys(10);
    }

    private static void generateMissingFileKeysForOneNode(DracoonClient client)
            throws DracoonException {
        long nodeId = 1L;

        client.nodes().generateMissingFileKeys(nodeId, 10);
    }

}
