package com.dracoon.sdk.example;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {
        String serverUrl = "https://dracoon.team";
        String accessToken = "";

        DracoonClient client = new DracoonClient.Builder(serverUrl)
                .log(new Logger(Log.DEBUG))
                .accessToken(accessToken)
                .build();

        //getServerData(client);

        //listRootNodes(client);
        //getNode(client);
        //getInvalidNode(client);
        uploadFile(client);
    }

    private static void getServerData(DracoonClient client) throws DracoonException {
        String serverVersion = client.server().getVersion();
        System.out.println("Server version: " + serverVersion);

        Date serverDate = client.server().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("Server date: " + df.format(serverDate));
    }

    private static void listRootNodes(DracoonClient client) throws DracoonException {
        NodeList nodeList = client.nodes().getRootNodes();
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

    private static void getNode(DracoonClient client) throws DracoonException {
        Node node = client.nodes().getNode(1);
        System.out.println("id=" + node.getId() + ", name=" + node.getName());
    }

    private static void getInvalidNode(DracoonClient client) throws DracoonException {
        try {
            client.nodes().getNode(123456789);
        } catch (DracoonException e) {
            System.err.println("Error at retrieval of node: " + e.getMessage());
        }
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

}
