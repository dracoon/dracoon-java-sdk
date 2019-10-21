package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoException;
import com.dracoon.sdk.crypto.CryptoSystemException;
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
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import com.dracoon.sdk.model.Node;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;

public class UploadStream extends FileUploadStream {

    private static final String LOG_TAG = UploadStream.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;
    private static final int PROGRESS_UPDATE_INTERVAL = 100;

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
    private final DracoonService mRestService;
    private final HttpHelper mHttpHelper;
    private final int mChunkSize;
    private final DracoonErrorParser mErrorParser;

    private final String mId;
    private final FileUploadRequest mFileUploadRequest;
    private final UserPublicKey mUserPublicKey;
    private final PlainFileKey mFileKey;

    private FileEncryptionCipher mEncryptionCipher;

    private String mUploadId;
    private long mUploadOffset = 0L;
    private long mUploadLength;

    private Buffer mUploadBuffer = new Buffer();

    private long mChunkNum = 0L;

    private boolean mIsCompleted = false;
    private boolean mIsClosed = false;

    private Thread mThread;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileUploadCallback> mCallbacks = new ArrayList<>();

    UploadStream(DracoonClientImpl client, String id, FileUploadRequest request, long length,
            UserPublicKey userPublicKey, PlainFileKey fileKey) {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpHelper = client.getHttpHelper();
        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
        mErrorParser = client.getDracoonErrorParser();

        mId = id;
        mFileUploadRequest = request;
        mUploadLength = length;
        mUserPublicKey = userPublicKey;
        mFileKey = fileKey;
    }

    void start() throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mThread = Thread.currentThread();

        try {
            notifyStarted(mId);

            if (isEncryptedUpload()) {
                mEncryptionCipher = createEncryptionCipher();
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

    private String createUpload() throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        String auth = mClient.buildAuthString();

        Integer classification = mFileUploadRequest.getClassification() != null ?
                mFileUploadRequest.getClassification().getValue() : null;

        if (classification == null && !mClient.isApiVersionGreaterEqual(
                DracoonConstants.API_MIN_VERSION_DEFAULT_CLASSIFICATION)) {
            classification = Classification.PUBLIC.getValue();
        }

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

        Call<ApiFileUpload> call = mRestService.createFileUpload(auth, request);
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
            long count = remaining > mChunkSize ? mChunkSize : remaining;

            byte[] bytes;
            try {
                bytes = mUploadBuffer.readByteArray(count);
            } catch (IOException e) {
                String errorText = "Buffer read failed!";
                mLog.d(LOG_TAG, errorText);
                throw new DracoonFileIOException(errorText, e);
            }

            if (isEncryptedUpload()) {
                boolean isLast = !more && mUploadBuffer.size() == 0;
                bytes = encryptBytes(bytes, isLast);
            }

            uploadChunk(mUploadOffset, bytes);
            count = bytes.length;

            mLog.d(LOG_TAG, String.format("Loading: id='%s': chunk=%d: %d-%d", mId, mChunkNum,
                    mUploadOffset, mUploadOffset + count));

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

    private void uploadChunk(long offset, byte[] chunk) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String auth = mClient.buildAuthString();

        String fileName = mFileUploadRequest.getName();

        FileRequestBody requestBody = new FileRequestBody(chunk, chunk.length);
        requestBody.setCallback(new FileRequestBody.Callback() {
            @Override
            public void onProgress(long send) {
                if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                        && !mThread.isInterrupted()) {
                    notifyRunning(mId, mUploadOffset + send, mUploadLength);
                    mProgressUpdateTime = System.currentTimeMillis();
                }
            }
        });
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestBody);

        String contentRange = "bytes " + offset + "-" + (offset + chunk.length) + "/*";

        Call<Void> call = mRestService.uploadFile(auth, mUploadId, contentRange, body);
        Response<Void> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadError(response);
            String errorText = String.format("Upload of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private Node completeUpload(EncryptedFileKey encryptedFileKey) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String auth = mClient.buildAuthString();

        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = mFileUploadRequest.getName();
        request.resolutionStrategy = mFileUploadRequest.getResolutionStrategy().getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<ApiNode> call = mRestService.completeFileUpload(auth, mUploadId, request);
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
