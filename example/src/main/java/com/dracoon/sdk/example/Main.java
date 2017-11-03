package com.dracoon.sdk.example;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonClientBuilder;

public class Main {

    public static void main(String[] args) {
        String serverUrl = "https://dracoon.team";
        String accessToken = "";

        DracoonClient client = new DracoonClientBuilder(serverUrl)
                .accessToken(accessToken)
                .build();
    }

}
