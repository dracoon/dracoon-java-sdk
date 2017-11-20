package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.model.FileDownloadCallback;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileDownload extends Thread {

    private static final String LOG_TAG = FileDownload.class.getSimpleName();

    private static final int JUNK_SIZE = 2 * 1024 * 1024;
    private static final int BLOCK_SIZE = 2 * 1024;
    private static final int PROGRESS_UPDATE_INTERVAL = 100;

    private final DracoonClientImpl mClient;
    private final DracoonService mRestService;
    private final OkHttpClient mHttpClient;

    private final String mId;
    private final long mNodeId;
    private final OutputStream mTrgStream;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileDownloadCallback> mCallbacks = new ArrayList<>();

    public FileDownload(DracoonClientImpl client, String id, long nodeId, OutputStream trgStream) {
        mClient = client;
        mRestService = client.getDracoonService();
        mHttpClient = new OkHttpClient();

        mId = id;
        mNodeId = nodeId;
        mTrgStream = trgStream;
    }

    public void addCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public void run() {
        try {
            download();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonException e) {
            notifyFailed(mId, e);
        }
    }

    public void runSync() throws DracoonException {
        try {
            download();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw e;
        }
    }

    private void download() throws DracoonException, InterruptedException {
        notifyStarted(mId);

        String downloadUrl = getDownloadUrl(mNodeId);

        downloadFile(downloadUrl, mTrgStream);

        notifyFinished(mId);
    }

    private String getDownloadUrl(long nodeId) throws DracoonException, InterruptedException {
        String authToken = mClient.getAccessToken();

        Call<ApiDownloadToken> call = mRestService.getDownloadToken(authToken, nodeId);
        Response<ApiDownloadToken> response = DracoonHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = DracoonErrorParser.parseDownloadTokenError(response);
            String errorText = String.format("Creation of file download '%s' for file '%d' " +
                    "failed with '%s'!", mId, nodeId, errorCode.name());
            Log.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadToken downloadToken = response.body();

        if (downloadToken.downloadUrl != null) {
            return downloadToken.downloadUrl;
        } else {
            return mClient.buildApiUrl("downloads", downloadToken.token);
        }
    }

    private void downloadFile(String downloadUrl, OutputStream outStream)
            throws DracoonException, InterruptedException {
        long offset = 0L;
        long length = getFileSize(downloadUrl);
        while (offset < length) {
            long remaining = length - offset;
            int count = remaining > JUNK_SIZE ? JUNK_SIZE : (int) remaining;
            byte[] data = downloadFileChunk(downloadUrl, offset, count, length);

            try {
                outStream.write(data);
            } catch (IOException e) {
                if (isInterrupted()) {
                    throw new InterruptedException();
                }
                String errorText = "File write failed!";
                Log.d(LOG_TAG, errorText);
                throw new DracoonFileIOException(errorText, e);
            }

            offset = offset + count;
        }
    }

    private long getFileSize(String downloadUrl) throws DracoonException, InterruptedException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(downloadUrl)
                .head()
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = DracoonHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = DracoonErrorParser.parseDownloadError(response);
            String errorText = String.format("File download '%s' for file '%d' failed with '%s'!",
                    mId, mNodeId, errorCode.name());
            Log.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().contentLength();
    }

    private byte[] downloadFileChunk(String downloadUrl, long offset, int count, long length)
            throws DracoonException, InterruptedException {
        String range = "bytes=" + offset + "-" + (offset + count);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(downloadUrl)
                .addHeader("Range", range)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = DracoonHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = DracoonErrorParser.parseDownloadError(response);
            String errorText = String.format("File download '%s' for file '%d' failed with '%s'!",
                    mId, mNodeId, errorCode.name());
            Log.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        BufferedInputStream is = new BufferedInputStream(response.body().byteStream());;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte buffer[] = new byte[BLOCK_SIZE];
        int bytesRead;
        int bytesReadTotal = 0;
        try {
            while ((bytesRead = is.read(buffer)) > -1) {
                os.write(buffer, 0, bytesRead);

                bytesReadTotal = bytesReadTotal + bytesRead;

                if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                        && !isInterrupted()) {
                    notifyRunning(mId, offset + bytesReadTotal, length);
                    mProgressUpdateTime = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            if (isInterrupted()) {
                throw new InterruptedException();
            } else {
                String errorText = "Server communication failed!";
                Log.d(LOG_TAG, errorText);
                throw new DracoonNetIOException(errorText, e);
            }
        }

        return os.toByteArray();
    }

    // --- Callback helper methods ---

    private void notifyStarted(String id) {
        mCallbacks.forEach(callback -> callback.onStarted(id));
    }

    private void notifyRunning(String id, long bytesRead, long bytesTotal) {
        mCallbacks.forEach(callback -> callback.onRunning(id, bytesRead, bytesTotal));
    }

    private void notifyFinished(String id) {
        mCallbacks.forEach(callback -> callback.onFinished(id));
    }

    private void notifyCanceled(String id) {
        mCallbacks.forEach(callback -> callback.onCanceled(id));
    }

    private void notifyFailed(String id, DracoonException e) {
        mCallbacks.forEach(callback -> callback.onFailed(id, e));
    }

}
