package com.dracoon.sdk.example;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonClientBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {
        String serverUrl = "https://dracoon.team";
        String accessToken = "";

        DracoonClient client = new DracoonClientBuilder(serverUrl)
                .accessToken(accessToken)
                .build();

        String serverVersion = client.server().getVersion();
        Date serverDate = client.server().getTime();

        System.out.println("Server version: " + serverVersion);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("Server date: " + df.format(serverDate));
    }

}
