package com.dracoon.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

public class StreamUpload extends FileUploadStream {

    private static final String LOG_TAG = StreamUpload.class.getSimpleName();

    private final DracoonClientImpl mClient;
    private final Log mLog;
    private final DracoonService mRestService;
    private final HttpHelper mHttpHelper;
    private final int mChunkSize;
    private final DracoonErrorParser mErrorParser;

    private final FileUploadRequest mFileUploadRequest;
    private final UserPublicKey mUserPublicKey;
    private final PlainFileKey mFileKey;

    private FileEncryptionCipher mEncryptionCipher;

    private String mUploadId;
    private long mUploadOffset = 0L;

    private Buffer mUploadBuffer = new Buffer();

    private long mChunkNum = 0L;

    private boolean mIsCompleted = false;
    private boolean mIsClosed = false;

    StreamUpload(DracoonClientImpl client, FileUploadRequest request, UserPublicKey userPublicKey,
            PlainFileKey fileKey) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpHelper = client.getHttpHelper();
        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
        mErrorParser = client.getDracoonErrorParser();

        mFileUploadRequest = request;
        mUserPublicKey = userPublicKey;
        mFileKey = fileKey;

        init();
    }

    private void init() throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        if (isEncryptedUpload()) {
            mEncryptionCipher = createEncryptionCipher();
        }

        mUploadId = createUpload();
    }

    private boolean isEncryptedUpload() {
        return mFileKey != null;
    }

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
        } catch (DracoonException e) {
            throw new IOException("Could not write to upload stream.", e);
        }
    }

    @Override
    public void complete() throws IOException {
        assertNotCompleted();
        assertNotClosed();

        try {
            uploadData(false);
        } catch (DracoonException e) {
            throw new IOException("Could not write to upload stream.", e);
        }

        EncryptedFileKey encryptedFileKey = null;
        try {
            if (isEncryptedUpload()) {
                encryptedFileKey = mClient.getNodesImpl().encryptFileKey(null, mFileKey,
                        mUserPublicKey);
            }
        } catch (DracoonException e) {
            throw new IOException("Could not complete upload.", e);
        }

        try {
            completeUpload(encryptedFileKey);
        } catch (DracoonException e) {
            throw new IOException("Could not close upload stream.", e);
        }

        mIsCompleted = true;
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
            String errorText = String.format("Encryption failed at upload of file '%s'! %s",
                    mFileUploadRequest.getName(), e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    private String createUpload() throws DracoonNetIOException, DracoonApiException {
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
        Response<ApiFileUpload> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadCreateError(response);
            String errorText = String.format("Creation of upload stream for file '%s' failed " +
                    "with '%s'!", mFileUploadRequest.getName(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().uploadId;
    }

    private void uploadData(boolean more) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException {
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

            mLog.d(LOG_TAG, String.format("Loading: %d: %d-%d", mChunkNum, mUploadOffset,
                    mUploadOffset + count));

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
            String errorText = String.format("Encryption failed at upload of file '%s'! %s",
                    mFileUploadRequest.getName(), e.getMessage());
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

    private void uploadChunk(long offset, byte[] chunk)
            throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),
                chunk, 0, chunk.length);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",
                mFileUploadRequest.getName(), requestBody);

        String contentRange = "bytes " + offset + "-" + (offset + chunk.length) + "/*";

        Call<Void> call = mRestService.uploadFile(auth, mUploadId, contentRange, body);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadError(response);
            String errorText = String.format("Upload of file '%s' failed with '%s'!",
                    mFileUploadRequest.getName(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private void completeUpload(EncryptedFileKey encryptedFileKey) throws DracoonNetIOException,
            DracoonApiException {
        String auth = mClient.buildAuthString();

        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = mFileUploadRequest.getName();
        request.resolutionStrategy = mFileUploadRequest.getResolutionStrategy().getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<ApiNode> call = mRestService.completeFileUpload(auth, mUploadId, request);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadCompleteError(response);
            String errorText = String.format("Completion of upload for file '%s' failed with '%s'!",
                    mFileUploadRequest.getName(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
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

}
