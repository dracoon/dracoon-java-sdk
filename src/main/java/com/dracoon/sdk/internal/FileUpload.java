package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.ResolutionStrategy;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;

public class FileUpload extends Thread {

    private static final String LOG_TAG = FileUpload.class.getSimpleName();

    protected static final int JUNK_SIZE = 2 * 1024 * 1024;

    private static final int BLOCK_SIZE = 2 * 1024;
    private static final int PROGRESS_UPDATE_INTERVAL = 100;

    private static class FileRequestBody extends RequestBody {

        interface Callback {
            void onProgress(long send);
        }

        private final byte[] mData;
        private final int mLength;

        private Callback mCallback;

        FileRequestBody(byte[] data, int length) {
            mData = data;
            mLength = length;
        }

        void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public long contentLength() throws IOException {
            return mLength;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            int offset = 0;
            int remaining;
            while ((remaining = mLength - offset) > 0) {
                int count = remaining >= BLOCK_SIZE ? BLOCK_SIZE : remaining;
                sink.write(mData, offset, count);

                offset = offset + count;

                if (mCallback != null) {
                    mCallback.onProgress(offset);
                }
            }
        }

    }

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonService mRestService;
    protected final DracoonHttpHelper mHttpHelper;
    protected final DracoonErrorParser mErrorParser;

    protected final String mId;
    protected final FileUploadRequest mRequest;
    protected final InputStream mSrcStream;
    protected final long mSrcLength;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileUploadCallback> mCallbacks = new ArrayList<>();

    public FileUpload(DracoonClientImpl client, String id, FileUploadRequest request,
            InputStream srcStream, long srcLength) {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpHelper = client.getDracoonHttpHelper();
        mErrorParser = client.getDracoonErrorParser();

        mId = id;
        mRequest = request;
        mSrcStream = srcStream;
        mSrcLength = srcLength;
    }

    public void addCallback(FileUploadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(FileUploadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public void run() {
        try {
            upload();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonFileIOException | DracoonCryptoException | DracoonNetIOException |
                DracoonApiException e) {
            notifyFailed(mId, e);
        }
    }

    public Node runSync() throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        try {
            return upload();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
            return null;
        } catch (DracoonFileIOException | DracoonCryptoException | DracoonNetIOException |
                DracoonApiException e) {
            notifyFailed(mId, e);
            throw e;
        }
    }

    protected Node upload() throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException, InterruptedException {
        notifyStarted(mId);

        String uploadId = createUpload(mRequest.getParentId(), mRequest.getName(),
                mRequest.getClassification().getValue(), mRequest.getNotes(),
                mRequest.getExpirationDate());

        uploadFile(uploadId, mRequest.getName(), mSrcStream, mSrcLength);

        ApiNode apiNode = completeUpload(uploadId, mRequest.getName(),
                mRequest.getResolutionStrategy());

        Node node = NodeMapper.fromApiNode(apiNode);

        notifyFinished(mId, node);

        return node;
    }

    protected String createUpload(long parentNodeId, String name, int classification, String notes,
            Date expirationDate) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        String authToken = mClient.getAccessToken();

        ApiCreateFileUploadRequest request = new ApiCreateFileUploadRequest();
        request.parentId = parentNodeId;
        request.name = name;
        request.classification = classification;
        request.notes = notes;
        if (expirationDate != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = expirationDate.getTime() != 0L;
            apiExpiration.expireAt = expirationDate;
            request.expiration = apiExpiration;
        }

        Call<ApiFileUpload> call = mRestService.createFileUpload(authToken, request);
        Response<ApiFileUpload> response = mHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileUploadCreateError(response);
            String errorText = String.format("Creation of upload '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().uploadId;
    }

    private void uploadFile(String uploadId, String fileName, InputStream is, long length)
            throws DracoonFileIOException, DracoonNetIOException, DracoonApiException,
            InterruptedException {
        byte[] buffer = new byte[JUNK_SIZE];
        long offset = 0;
        int count;

        try {
            while ((count = is.read(buffer)) != -1) {
                uploadFileChunk(uploadId, fileName, buffer, offset, count, length);
                offset = offset + count;
            }
        } catch (IOException e) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = String.format("File read failed at upload '%s'!", mId);
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        }
    }

    protected void uploadFileChunk(String uploadId, String fileName, byte[] data, long offset,
            int count, long length) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        if (count <= 0) {
            return;
        }

        String authToken = mClient.getAccessToken();

        FileRequestBody requestBody = new FileRequestBody(data, count);
        requestBody.setCallback(send -> {
            if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                    && !isInterrupted()) {
                notifyRunning(mId, offset + send, length);
                mProgressUpdateTime = System.currentTimeMillis();
            }
        });
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestBody);

        String contentRange = "bytes " + offset + "-" + (offset + count) + "/*";

        Call<Void> call = mRestService.uploadFile(authToken, uploadId, contentRange, body);
        Response<Void> response = mHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileUploadError(response);
            String errorText = String.format("Upload of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private ApiNode completeUpload(String uploadId, String fileName,
            ResolutionStrategy resolutionStrategy) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        String authToken = mClient.getAccessToken();

        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = fileName;
        request.resolutionStrategy = resolutionStrategy.getValue();

        Call<ApiNode> call = mRestService.completeFileUpload(authToken, uploadId, request);
        Response<ApiNode> response = mHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileUploadCompleteError(response);
            String errorText = String.format("Completion of upload '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    // --- Callback helper methods ---

    protected void notifyStarted(String id) {
        mCallbacks.forEach(callback -> callback.onStarted(id));
    }

    protected void notifyRunning(String id, long bytesSend, long bytesTotal) {
        mCallbacks.forEach(callback -> callback.onRunning(id, bytesSend, bytesTotal));
    }

    protected void notifyFinished(String id, Node node) {
        mCallbacks.forEach(callback -> callback.onFinished(id, node));
    }

    protected void notifyCanceled(String id) {
        mCallbacks.forEach(callback -> callback.onCanceled(id));
    }

    protected void notifyFailed(String id, DracoonException e) {
        mCallbacks.forEach(callback -> callback.onFailed(id, e));
    }

}
