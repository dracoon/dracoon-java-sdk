package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.model.FileDownloadCallback;

public class DownloadThread extends Thread {

    private static final String LOG_TAG = DownloadThread.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;

    private final Log mLog;

    private final String mId;

    private final DownloadStream mDownloadStream;
    private final OutputStream mOutputStream;

    private Thread mThread;

    private final List<FileDownloadCallback> mCallbacks = new ArrayList<>();

    public DownloadThread(DracoonClientImpl client, String id, long nodeId, PlainFileKey fileKey,
            OutputStream outputStream) {
        mLog = client.getLog();

        mId = id;

        mDownloadStream = new DownloadStream(client, id, nodeId, fileKey);
        mOutputStream = outputStream;
    }

    public void addCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
            mDownloadStream.addCallback(callback);
        }
    }

    public void removeCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
            mDownloadStream.removeCallback(callback);
        }
    }

    @Override
    public void run() {
        mThread = this;

        try {
            download();
        } catch (DracoonException e) {
            // Nothing to do here
        }
    }

    public void runSync() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        mThread = Thread.currentThread();

        download();
    }

    private void download() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        try {
            mDownloadStream.start();

            byte[] buffer = new byte[BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = mDownloadStream.read(buffer)) != -1) {
                mOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                notifyCanceled(mId);
                return;
            }
            Throwable cause = e.getCause();
            if (cause instanceof DracoonException) {
                rethrow((DracoonException) cause);
            } else {
                String errorText = "File write failed!";
                mLog.d(LOG_TAG, errorText);
                DracoonFileIOException ex = new DracoonFileIOException(errorText, e);
                notifyFailed(mId, ex);
                throw ex;
            }
        }
    }

    // --- Helper methods ---

    private void rethrow(DracoonException e) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        if (e instanceof DracoonNetIOException) {
            throw (DracoonNetIOException) e;
        } else if (e instanceof DracoonApiException) {
            throw (DracoonApiException) e;
        } else if (e instanceof DracoonCryptoException) {
            throw (DracoonCryptoException) e;
        } else if (e instanceof DracoonFileIOException) {
            throw (DracoonFileIOException) e;
        }
    }

    // --- Callback helper methods ---

    private void notifyCanceled(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onCanceled(id);
        }
    }

    private void notifyFailed(String id, DracoonException e) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onFailed(id, e);
        }
    }

}
