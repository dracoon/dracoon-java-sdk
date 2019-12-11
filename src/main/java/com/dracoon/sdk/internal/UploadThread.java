package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;

public class UploadThread extends Thread {

    private static final String LOG_TAG = DownloadThread.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;

    private final Log mLog;

    private final String mId;

    private final UploadStream mUploadStream;
    private final InputStream mInputStream;

    private Thread mThread;

    private final List<FileUploadCallback> mCallbacks = new ArrayList<>();

    public UploadThread(DracoonClientImpl client, String id, FileUploadRequest request, long length,
            UserPublicKey userPublicKey, PlainFileKey fileKey, InputStream inputStream) {
        mLog = client.getLog();

        mId = id;

        mUploadStream = new UploadStream(client, id, request, length, userPublicKey, fileKey);
        mInputStream = inputStream;
    }

    public void addCallback(FileUploadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
            mUploadStream.addCallback(callback);
        }
    }

    public void removeCallback(FileUploadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
            mUploadStream.removeCallback(callback);
        }
    }

    @Override
    public void run() {
        mThread = this;

        try {
            upload();
        } catch (DracoonException e) {
            // Nothing to do here
        }
    }

    public Node runSync() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        mThread = Thread.currentThread();

        return upload();
    }

    private Node upload() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
        Node node = null;

        try {
            mUploadStream.start();

            byte[] buffer = new byte[BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = mInputStream.read(buffer)) != -1) {
                mUploadStream.write(buffer, 0, bytesRead);
            }

            node = mUploadStream.complete();
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                notifyCanceled(mId);
                return null;
            }
            Throwable cause = e.getCause();
            if (cause instanceof DracoonException) {
                rethrow((DracoonException) cause);
            } else {
                String errorText = "File read failed!";
                mLog.d(LOG_TAG, errorText);
                DracoonFileIOException ex = new DracoonFileIOException(errorText, e);
                notifyFailed(mId, ex);
                throw ex;
            }
        }

        return node;
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
        for (FileUploadCallback callback : mCallbacks) {
            callback.onCanceled(id);
        }
    }

    private void notifyFailed(String id, DracoonException e) {
        for (FileUploadCallback callback : mCallbacks) {
            callback.onFailed(id, e);
        }
    }

}
