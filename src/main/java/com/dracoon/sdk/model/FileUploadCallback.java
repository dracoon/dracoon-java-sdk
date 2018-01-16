package com.dracoon.sdk.model;

import com.dracoon.sdk.error.DracoonException;

/**
 * A listener for receiving file upload progress events.
 */
public interface FileUploadCallback {

    /**
     * This method gets called when a upload was started.
     *
     * @param id The ID of the upload.
     */
    void onStarted(String id);

    /**
     * This method gets called at every progress update (every 100ms).
     *
     * @param id         The ID of the upload.
     * @param bytesSend  The number of bytes which have been send.
     * @param bytesTotal The total number of bytes.
     */
    void onRunning(String id, long bytesSend, long bytesTotal);

    /**
     * This method gets called when a upload was finished.
     *
     * @param id The ID of the upload.
     */
    void onFinished(String id, Node node);

    /**
     * This method gets called when a upload was canceled.
     *
     * @param id The ID of the upload.
     */
    void onCanceled(String id);

    /**
     * This method gets called when a upload failed.
     *
     * @param id The ID of the upload.
     * @param e  The cause of the error.
     */
    void onFailed(String id, DracoonException e);

}
