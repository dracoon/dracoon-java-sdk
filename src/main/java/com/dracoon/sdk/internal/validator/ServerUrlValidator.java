package com.dracoon.sdk.internal.validator;

import java.net.URL;

public class ServerUrlValidator {

    public static void validateServerURL(URL serverUrl) {
        if (serverUrl == null) {
            throw new IllegalArgumentException("Server URL cannot be null.");
        }
        String protocol = serverUrl.getProtocol();
        if (protocol == null || !(protocol.equals("http") || protocol.equals("https"))) {
            throw new IllegalArgumentException("Server URL can only have protocol http or https.");
        }
        String user = serverUrl.getUserInfo();
        if (user != null) {
            throw new IllegalArgumentException("Server URL cannot have user.");
        }
        String path = serverUrl.getPath();
        if (path != null && !path.isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot have path.");
        }
        String query = serverUrl.getQuery();
        if (query != null) {
            throw new IllegalArgumentException("Server URL cannot have query.");
        }
    }

}
