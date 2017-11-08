package com.dracoon.sdk;

import com.dracoon.sdk.internal.DracoonClientImpl;

public class DracoonClientBuilder {

    private DracoonClientImpl mClient;

    public DracoonClientBuilder(String serverUrl) {
        mClient = new DracoonClientImpl(serverUrl);
    }

    public DracoonClientBuilder log(Log log) {
        mClient.setLog(log);
        return this;
    }

    public DracoonClientBuilder accessToken(String accessToken) {
        mClient.setAccessToken(accessToken);
        return this;
    }

    public DracoonClient build() {
        mClient.init();
        return mClient;
    }

}
