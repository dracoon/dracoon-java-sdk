package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.CryptoException;
import com.dracoon.sdk.crypto.error.CryptoSystemException;
import com.dracoon.sdk.crypto.CryptoUtils;
import com.dracoon.sdk.crypto.FileEncryptionCipher;
import com.dracoon.sdk.crypto.model.EncryptedDataContainer;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainDataContainer;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCompleteS3FileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiGetS3FileUploadUrlsRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiS3FileUploadPart;
import com.dracoon.sdk.internal.model.ApiS3FileUploadStatus;
import com.dracoon.sdk.internal.model.ApiS3FileUploadUrlList;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import com.dracoon.sdk.model.Node;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;

public class UploadStream extends FileUploadStream {

    private static final String LOG_TAG = UploadStream.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;
    private static final long PROGRESS_UPDATE_INTERVAL = 100;

    private static final int S3_DEFAULT_CHUNK_SIZE = 5 * DracoonConstants.MIB;
    private static final String S3_ETAG_HEADER = "ETag";
    private static final long S3_MIN_COMPLETE_WAIT_TIME = 500;
    private static final long S3_MAX_COMPLETE_WAIT_TIME = 5 * DracoonConstants.SECOND;

    private static final String S3_UPLOAD_STATUS_TRANSFER = "transfer";
    private static final String S3_UPLOAD_STATUS_FINISHING = "finishing";
    private static final String S3_UPLOAD_STATUS_DONE = "done";
    private static final String S3_UPLOAD_STATUS_ERROR = "error";

    private static class FileRequestBody extends RequestBody {

        interface Callback {
            void onProgress(long send);
        }

        private final byte[] mData;
        private final int mLength;

        private FileRequestBody.Callback mCallback;

        FileRequestBody(byte[] data, int length) {
            mData = data;
            mLength = length;
        }

        void setCallback(FileRequestBody.Callback callback) {
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

    private final DracoonClientImpl mClient;
    private final Log mLog;
    private final DracoonService mService;
    private final OkHttpClient mHttpClient;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    private final String mId;
    private final FileUploadRequest mFileUploadRequest;
    private final UserPublicKey mUserPublicKey;
    private final PlainFileKey mFileKey;

    private FileEncryptionCipher mEncryptionCipher;

    private String mUploadId;
    private long mUploadOffset = 0L;
    private final long mUploadLength;

    private final Buffer mUploadBuffer = new Buffer();

    private int mChunkSize;
    private int mChunkNum = 0;

    private boolean mIsS3Upload = false;
    private final List<ApiS3FileUploadPart> mS3UploadParts = new ArrayList<>();

    private boolean mIsCompleted = false;
    private boolean mIsClosed = false;

    private Thread mThread;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileUploadCallback> mCallbacks = new ArrayList<>();

    UploadStream(DracoonClientImpl client, String id, FileUploadRequest request, long length,
            UserPublicKey userPublicKey, PlainFileKey fileKey) {
        mClient = client;
        mLog = client.getLog();
        mService = client.getDracoonService();
        mHttpClient = client.getHttpClient();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();

        mId = id;
        mFileUploadRequest = request;
        mUploadLength = length;
        mUserPublicKey = userPublicKey;
        mFileKey = fileKey;

        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
    }

    void start() throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mThread = Thread.currentThread();

        try {
            notifyStarted(mId);

            if (isEncryptedUpload()) {
                mEncryptionCipher = createEncryptionCipher();
            }

            mIsS3Upload = checkIsS3Upload();

            if (mIsS3Upload) {
                mChunkSize = mChunkSize > S3_DEFAULT_CHUNK_SIZE ? mChunkSize : S3_DEFAULT_CHUNK_SIZE;
            }

            mUploadId = createUpload();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw e;
        }
    }

    private boolean isEncryptedUpload() {
        return mFileKey != null;
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

    // --- Stream methods ---

    @Override
    public void write(int b) throws IOException {
        byte[] ba = new byte[1];
        ba[0] = (byte) b;
        write(ba);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        assertNotCompleted();
        assertNotClosed();

        // If start offset and/or maximum number of bytes is invalid: Throw error
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }

        // If no bytes should be read: Abort
        if (len == 0) {
            return;
        }

        // Write to buffer
        mUploadBuffer.write(b, off, len);
        // Try to upload data
        try {
            uploadData(true);
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw new IOException("Could not write to upload stream.", e);
        }
    }

    @Override
    public Node complete() throws IOException {
        assertNotCompleted();
        assertNotClosed();

        try {
            uploadData(false);
        } catch (InterruptedException e) {
            notifyCanceled(mId);
            return null;
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw new IOException("Could not write to upload stream.", e);
        }

        EncryptedFileKey encryptedFileKey = null;
        try {
            if (isEncryptedUpload()) {
                encryptedFileKey = mClient.getNodesImpl().encryptFileKey(null, mFileKey,
                        mUserPublicKey);
            }
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw new IOException("Could not complete upload.", e);
        }

        Node node;
        try {
            node = completeUpload(encryptedFileKey);
        } catch (InterruptedException e) {
            notifyCanceled(mId);
            return null;
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw new IOException("Could not close upload stream.", e);
        }

        notifyFinished(mId, node);

        mIsCompleted = true;

        return node;
    }

    @Override
    public void close() throws IOException {
        assertNotClosed();
        mIsClosed = true;
    }

    // --- Helper methods ---

    private FileEncryptionCipher createEncryptionCipher() throws DracoonCryptoException {
        try {
            return Crypto.createFileEncryptionCipher(mFileKey);
        } catch (CryptoException e) {
            String errorText = String.format("Encryption failed at upload of '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    private boolean checkIsS3Upload() throws DracoonNetIOException, DracoonApiException {
        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_S3_DIRECT_UPLOAD)) {
            return false;
        }

        Call<ApiServerGeneralSettings> call = mService.getServerGeneralSettings();
        Response<ApiServerGeneralSettings> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server general settings failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerGeneralSettings settings = response.body();

        return settings.useS3Storage != null && settings.useS3Storage;
    }

    private String createUpload() throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        Integer classification = mFileUploadRequest.getClassification() != null ?
                mFileUploadRequest.getClassification().getValue() : null;

        ApiCreateFileUploadRequest request = new ApiCreateFileUploadRequest();
        request.parentId = mFileUploadRequest.getParentId();
        request.name = mFileUploadRequest.getName();
        request.classification = classification;
        request.notes = mFileUploadRequest.getNotes();
        if (mFileUploadRequest.getExpirationDate() != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = mFileUploadRequest.getExpirationDate().getTime() != 0L;
            apiExpiration.expireAt = mFileUploadRequest.getExpirationDate();
            request.expiration = apiExpiration;
        }
        request.timestampCreation = mFileUploadRequest.getOriginalCreationDate();
        request.timestampModification = mFileUploadRequest.getOriginalModificationDate();
        if (mIsS3Upload) {
            request.directS3Upload = mIsS3Upload;
        }

        Call<ApiFileUpload> call = mService.createFileUpload(request);
        Response<ApiFileUpload> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadCreateError(response);
            String errorText = String.format("Creation of upload stream for '%s' failed with '%s'!",
                    mId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().uploadId;
    }

    private void uploadData(boolean more) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException, InterruptedException {
        // Upload till buffer is exhausted
        while ((more && mUploadBuffer.size() > mChunkSize) || (!more && mUploadBuffer.size() > 0)) {
            long remaining = mUploadBuffer.size();
            long size = remaining > mChunkSize ? mChunkSize : remaining;

            byte[] bytes;
            try {
                bytes = mUploadBuffer.readByteArray(size);
            } catch (IOException e) {
                String errorText = "Buffer read failed!";
                mLog.d(LOG_TAG, errorText);
                throw new DracoonFileIOException(errorText, e);
            }

            if (isEncryptedUpload()) {
                boolean isLast = !more && mUploadBuffer.size() == 0;
                bytes = encryptBytes(bytes, isLast);
            }

            int count = bytes.length;

            mLog.d(LOG_TAG, String.format("Loading: id='%s': chunk=%d: %d-%d", mId, mChunkNum,
                    mUploadOffset, mUploadOffset + count));

            uploadChunk(mUploadOffset, mChunkNum, bytes);

            mUploadOffset = mUploadOffset + bytes.length;
            mChunkNum++;
        }
    }

    private byte[] encryptBytes(byte[] bytes, boolean isLast) throws DracoonFileIOException,
            DracoonCryptoException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            PlainDataContainer plainData = new PlainDataContainer(bytes);
            EncryptedDataContainer encData = mEncryptionCipher.processBytes(plainData);
            os.write(encData.getContent());

            if (isLast) {
                encData = mEncryptionCipher.doFinal();
                os.write(encData.getContent());

                String encTag = CryptoUtils.byteArrayToString(encData.getTag());
                mFileKey.setTag(encTag);
            }
        } catch (IllegalArgumentException | IllegalStateException | CryptoSystemException e) {
            String errorText = String.format("Encryption failed at upload of '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        } catch (IOException e) {
            String errorText = "Buffer write failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        } finally {
            StreamUtils.closeStream(os);
        }

        return os.toByteArray();
    }

    private void uploadChunk(long uploadOffset, int chunkNum, byte[] bytes)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
        if (mIsS3Upload) {
            ApiS3FileUploadPart uploadPart = uploadS3Chunk(chunkNum, bytes);
            mS3UploadParts.add(uploadPart);
        } else {
            uploadStandardChunk(uploadOffset, bytes);
        }
    }

    private Node completeUpload(EncryptedFileKey encryptedFileKey) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        if (mIsS3Upload) {
            if (mS3UploadParts.isEmpty()) {
                ApiS3FileUploadPart uploadPart = uploadS3Chunk(0, new byte[0]);
                mS3UploadParts.add(uploadPart);
            }
            return completeS3Upload(mS3UploadParts, encryptedFileKey);
        } else {
            return completeStandardUpload(encryptedFileKey);
        }
    }

    private void uploadStandardChunk(long offset, byte[] chunk) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String fileName = mFileUploadRequest.getName();
        FileRequestBody fileChunk = createChunk(chunk);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, fileChunk);

        String contentRange = "bytes " + offset + "-" + (offset + chunk.length) + "/*";

        Call<Void> call = mService.uploadFile(mUploadId, contentRange, body);
        Response<Void> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadError(response);
            String errorText = String.format("Upload of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private Node completeStandardUpload(EncryptedFileKey encryptedFileKey)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = mFileUploadRequest.getName();
        request.resolutionStrategy = mFileUploadRequest.getResolutionStrategy().getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<ApiNode> call = mService.completeFileUpload(mUploadId, request);
        Response<ApiNode> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadCompleteError(response);
            String errorText = String.format("Completion of upload for '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return NodeMapper.fromApiNode(response.body());
    }

    private ApiS3FileUploadPart uploadS3Chunk(int chunkNum, byte[] chunk) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String uploadUrl = getS3UploadUrl(chunkNum, chunk.length);

        FileRequestBody fileChunk = createChunk(chunk);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(uploadUrl)
                .put(fileChunk)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseS3UploadError(response);
            String errorText = String.format("Upload of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        String eTag = response.headers().get(S3_ETAG_HEADER);
        eTag = eTag != null ? eTag.replace("\"", "") : "";

        ApiS3FileUploadPart part = new ApiS3FileUploadPart();
        part.partNumber = chunkNum + 1;
        part.partEtag = eTag;

        return part;
    }

    private String getS3UploadUrl(int chunkNum, long chunkSize) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        mLog.d(LOG_TAG, String.format("Requesting S3 upload URL: chunk=%d: size=%d: ", chunkNum,
                chunkSize));

        ApiGetS3FileUploadUrlsRequest request = new ApiGetS3FileUploadUrlsRequest();
        request.size = chunkSize;
        request.firstPartNumber = chunkNum + 1;
        request.lastPartNumber = chunkNum + 1;

        Call<ApiS3FileUploadUrlList> call = mService.getS3FileUploadUrls(mUploadId, request);
        Response<ApiS3FileUploadUrlList> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseS3UploadGetUrlsError(response);
            String errorText = String.format("Upload of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().urls.get(0).url;
    }

    private Node completeS3Upload(List<ApiS3FileUploadPart> uploadParts,
            EncryptedFileKey encryptedFileKey) throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        ApiCompleteS3FileUploadRequest request = new ApiCompleteS3FileUploadRequest();
        request.fileName = mFileUploadRequest.getName();
        request.parts = uploadParts;
        request.resolutionStrategy = mFileUploadRequest.getResolutionStrategy().getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<Void> call = mService.completeS3FileUpload(mUploadId, request);
        Response<Void> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseS3UploadCompleteError(response);
            String errorText = String.format("Completion of upload for '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        Node node = null;
        long completeWaitTime = S3_MIN_COMPLETE_WAIT_TIME;
        while (completeWaitTime < S3_MAX_COMPLETE_WAIT_TIME) {
            node = waitForCompleteS3Upload();
            if (node != null) {
                break;
            }
            Thread.sleep(completeWaitTime);
            completeWaitTime = completeWaitTime * 2;
        }

        return node;
    }

    private Node waitForCompleteS3Upload() throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        Call<ApiS3FileUploadStatus> call = mService.getS3FileUploadStatus(mUploadId);
        Response<ApiS3FileUploadStatus> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseS3UploadStatusError(response);
            String errorText = String.format("Completion of upload for '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiS3FileUploadStatus uploadStatus = response.body();

        switch (uploadStatus.status) {
            case S3_UPLOAD_STATUS_TRANSFER:
            case S3_UPLOAD_STATUS_FINISHING:
                return null;
            case S3_UPLOAD_STATUS_DONE:
                return NodeMapper.fromApiNode(uploadStatus.node);
            default:
                DracoonApiCode errorCode = mErrorParser.parseS3UploadStatusError(
                        uploadStatus.errorDetails);
                String errorText = String.format("Completion of upload for '%s' failed with '%s'!",
                        mId, errorCode.name());
                mLog.d(LOG_TAG, errorText);
                throw new DracoonApiException(errorCode);
        }
    }

    private FileRequestBody createChunk(byte[] chunk) {
        FileRequestBody requestBody = new FileRequestBody(chunk, chunk.length);
        requestBody.setCallback(send -> {
            if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                    && !mThread.isInterrupted()) {
                notifyRunning(mId, mUploadOffset + send, mUploadLength);
                mProgressUpdateTime = System.currentTimeMillis();
            }
        });
        return requestBody;
    }

    private void assertNotCompleted() throws IOException {
        if (mIsCompleted) {
            throw new IOException("Upload stream was already completed.");
        }
    }

    private void assertNotClosed() throws IOException {
        if (mIsClosed) {
            throw new IOException("Upload stream was already closed.");
        }
    }

    // --- Callback helper methods ---

    private void notifyStarted(String id) {
        for (FileUploadCallback callback : mCallbacks) {
            callback.onStarted(id);
        }
    }

    private void notifyRunning(String id, long bytesSend, long bytesTotal) {
        for (FileUploadCallback callback : mCallbacks) {
            callback.onRunning(id, bytesSend, bytesTotal);
        }
    }

    private void notifyFinished(String id, Node node) {
        for (FileUploadCallback callback : mCallbacks) {
            callback.onFinished(id, node);
        }
    }

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
