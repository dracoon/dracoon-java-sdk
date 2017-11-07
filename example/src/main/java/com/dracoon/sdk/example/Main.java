package com.dracoon.sdk.example;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonClientBuilder;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {
        String serverUrl = "https://dracoon.team";
        String accessToken = "";

        DracoonClient client = new DracoonClientBuilder(serverUrl)
                .accessToken(accessToken)
                .build();

        //getServerData(client);

        //listRootNodes(client);
        //getNode(client);
        getInvalidNode(client);
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

}
