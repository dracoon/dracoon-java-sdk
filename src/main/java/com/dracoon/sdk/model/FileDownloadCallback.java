package com.dracoon.sdk.model;

import com.dracoon.sdk.error.DracoonException;

public interface FileDownloadCallback {

    void onStarted(String id);
    void onRunning(String id, long bytesRead, long bytesTotal);
    void onFinished(String id);
    void onCanceled(String id);
    void onFailed(String id, DracoonException e);

}
