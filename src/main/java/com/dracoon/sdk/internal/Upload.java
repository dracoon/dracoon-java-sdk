package com.dracoon.sdk.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.model.UploadCallback;
import com.dracoon.sdk.model.UploadRequest;
import com.dracoon.sdk.model.Node;

public class Upload extends Thread {

    private final DracoonClientImpl mClient;
    private final DracoonService mService;

    private final String mId;
    private final UploadRequest mRequest;
    private final InputStream mSrcStream;

    private final List<UploadCallback> mCallbacks = new ArrayList<>();

    public Upload(DracoonClientImpl client, String id, UploadRequest request,
                  InputStream srcStream) {
        mClient = client;
        mService = client.getDracoonService();

        mId = id;
        mRequest = request;
        mSrcStream = srcStream;
    }

    public void addCallback(UploadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(UploadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public void run() {

    }

    public Node runWithResult() {
        return null;
    }

    private Node upload() {
        return null;
    }

    private String createUpload(long parentNodeId, String fileName) {
        return null;
    }

    private void uploadFile(String uploadId, InputStream is) {

    }

    private ApiNode completeUpload(String uploadId, String fileName) {
        return null;
    }

}
