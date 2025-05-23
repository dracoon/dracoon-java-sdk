package com.dracoon.sdk.internal.service;

import java.net.URL;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;
import okhttp3.OkHttpClient;

public interface ServiceDependencies {

    Log getLog();
    DracoonHttpConfig getHttpConfig();
    OkHttpClient getHttpClient();
    HttpHelper getHttpHelper();
    URL getServerUrl();
    DracoonApi getDracoonApi();
    DracoonErrorParser getDracoonErrorParser();
    EncryptionPasswordHolder getEncryptionPasswordHolder();
    CryptoWrapper getCryptoWrapper();
    ThreadHelper getThreadHelper();
    FileStreamHelper getFileStreamHelper();

}
