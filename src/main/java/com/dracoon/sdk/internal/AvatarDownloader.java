package com.dracoon.sdk.internal;

import java.io.IOException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.http.HttpHelper;
import okhttp3.OkHttpClient;

public class AvatarDownloader {

    private static final String LOG_TAG = AvatarDownloader.class.getSimpleName();

    private final Log mLog;
    private final OkHttpClient mHttpClient;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    public AvatarDownloader(DracoonClientImpl client) {
        mLog = client.getLog();
        mHttpClient = client.getHttpClient();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();
    }

    public byte[] downloadAvatar(String downloadUrl) throws DracoonNetIOException,
            DracoonApiException {

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(downloadUrl)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseAvatarDownloadError(response);
            String errorText = String.format("Download of avatar with URL '%s' failed with '%s'!",
                    downloadUrl, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        try {
            return response.body().bytes();
        } catch (IOException e) {
            String errorText = "Server communication failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        }
    }

}
