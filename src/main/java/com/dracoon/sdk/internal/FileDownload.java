package com.dracoon.sdk.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.model.FileDownloadCallback;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

public class FileDownload extends Thread {

    private static final String LOG_TAG = FileDownload.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;
    private static final int PROGRESS_UPDATE_INTERVAL = 100;

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonService mRestService;
    protected final OkHttpClient mHttpClient;
    protected final HttpHelper mHttpHelper;
    protected final int mChunkSize;
    protected final DracoonErrorParser mErrorParser;

    protected final String mId;
    protected final long mNodeId;
    protected final OutputStream mTrgStream;

    protected Thread mThread;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileDownloadCallback> mCallbacks = new ArrayList<>();

    public FileDownload(DracoonClientImpl client, String id, long nodeId, OutputStream trgStream) {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpClient = client.getHttpClient();
        mHttpHelper = client.getHttpHelper();
        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
        mErrorParser = client.getDracoonErrorParser();

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
            mThread = this;
            download();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonNetIOException | DracoonApiException | DracoonCryptoException |
                DracoonFileIOException e) {
            notifyFailed(mId, e);
        }
    }

    public void runSync() throws DracoonNetIOException, DracoonApiException, DracoonCryptoException,
            DracoonFileIOException {
        try {
            mThread = Thread.currentThread();
            download();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonNetIOException | DracoonApiException | DracoonCryptoException |
                DracoonFileIOException e) {
            notifyFailed(mId, e);
            throw e;
        }
    }

    protected void download() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException, InterruptedException {
        notifyStarted(mId);

        String downloadUrl = getDownloadUrl(mNodeId);

        downloadFile(downloadUrl, mTrgStream);

        notifyFinished(mId);
    }

    protected String getDownloadUrl(long nodeId) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        String auth = mClient.buildAuthString();

        Call<ApiDownloadToken> call = mRestService.getDownloadToken(auth, nodeId);
        Response<ApiDownloadToken> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadTokenGetError(response);
            String errorText = String.format("Creation of file download '%s' for file '%d' " +
                    "failed with '%s'!", mId, nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
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
            throws DracoonNetIOException, DracoonApiException, DracoonFileIOException,
            InterruptedException {
        long offset = 0L;
        long length = getFileSize(mNodeId);

        try {
            while (offset < length) {
                long remaining = length - offset;
                int count = remaining > mChunkSize ? mChunkSize : (int) remaining;
                byte[] data = downloadFileChunk(downloadUrl, offset, count, length);

                outStream.write(data);

                offset = offset + count;
            }
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = "File write failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        }
    }

    protected long getFileSize(long nodeId) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        String auth = mClient.buildAuthString();

        Call<ApiNode> call = mRestService.getNode(auth, nodeId);
        Response<ApiNode> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("File download '%s' for file '%d' failed with '%s'!",
                    mId, nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode node = response.body();

        return node.size;
    }

    protected byte[] downloadFileChunk(String downloadUrl, long offset, int count, long length)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
        String range = "bytes=" + offset + "-" + (offset + count - 1);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(downloadUrl)
                .addHeader("Range", range)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadError(response);
            String errorText = String.format("File download '%s' for file '%d' failed with '%s'!",
                    mId, mNodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        BufferedInputStream is = new BufferedInputStream(response.body().byteStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte buffer[] = new byte[BLOCK_SIZE];
        int bytesRead;
        int bytesReadTotal = 0;
        try {
            while ((bytesRead = is.read(buffer)) > -1) {
                os.write(buffer, 0, bytesRead);

                bytesReadTotal = bytesReadTotal + bytesRead;

                if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                        && !mThread.isInterrupted()) {
                    notifyRunning(mId, offset + bytesReadTotal, length);
                    mProgressUpdateTime = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                throw new InterruptedException();
            } else {
                String errorText = "Server communication failed!";
                mLog.d(LOG_TAG, errorText);
                throw new DracoonNetIOException(errorText, e);
            }
        } finally {
            StreamUtils.closeStream(os);
            StreamUtils.closeStream(is);
        }

        return os.toByteArray();
    }

    // --- Callback helper methods ---

    protected void notifyStarted(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onStarted(id);
        }
    }

    protected void notifyRunning(String id, long bytesRead, long bytesTotal) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onRunning(id, bytesRead, bytesTotal);
        }
    }

    protected void notifyFinished(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onFinished(id);
        }
    }

    protected void notifyCanceled(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onCanceled(id);
        }
    }

    protected void notifyFailed(String id, DracoonException e) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onFailed(id, e);
        }
    }

}
