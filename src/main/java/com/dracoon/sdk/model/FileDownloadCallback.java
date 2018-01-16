package com.dracoon.sdk.model;

import com.dracoon.sdk.error.DracoonException;

/**
 * A listener for receiving file download progress events.
 */
public interface FileDownloadCallback {

    /**
     * This method gets called when a download was started.
     *
     * @param id The ID of the download.
     */
    void onStarted(String id);

    /**
     * This method gets called at every progress update (every 100ms).
     *
     * @param id         The ID of the download.
     * @param bytesRead  The number of bytes which have been read.
     * @param bytesTotal The total number of bytes.
     */
    void onRunning(String id, long bytesRead, long bytesTotal);

    /**
     * This method gets called when a download was finished.
     *
     * @param id The ID of the download.
     */
    void onFinished(String id);

    /**
     * This method gets called when a download was canceled.
     *
     * @param id The ID of the download.
     */
    void onCanceled(String id);

    /**
     * This method gets called when a download failed.
     *
     * @param id The ID of the download.
     * @param e  The cause of the error.
     */
    void onFailed(String id, DracoonException e);

}
