package com.dracoon.sdk.model;

import com.dracoon.sdk.error.DracoonException;

public interface UploadCallback {

    void onStarted(String id);
    void onRunning(String id, long bytesSend, long bytesTotal);
    void onFinished(String id, Node node);
    void onCanceled(String id);
    void onFailed(String id, DracoonException e);

}
