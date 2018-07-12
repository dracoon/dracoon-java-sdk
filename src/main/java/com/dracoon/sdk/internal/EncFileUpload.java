package com.dracoon.sdk.internal;

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
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.mapper.NodeMapper;
import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.ResolutionStrategy;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;

public class EncFileUpload extends FileUpload {

    private static final String LOG_TAG = EncFileUpload.class.getSimpleName();

    private final UserPublicKey mUserPublicKey;

    public EncFileUpload(DracoonClientImpl client, String id, FileUploadRequest request,
            InputStream srcStream, long srcLength, UserPublicKey userPublicKey) {
        super(client, id, request, srcStream, srcLength);

        mUserPublicKey = userPublicKey;
    }

    protected Node upload() throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException, InterruptedException {
        notifyStarted(mId);

        String uploadId = createUpload(mRequest.getParentId(), mRequest.getName(),
                mRequest.getClassification().getValue(), mRequest.getNotes(),
                mRequest.getExpirationDate());

        PlainFileKey plainFileKey = mClient.getNodesImpl().createFileKey(mUserPublicKey.getVersion());

        uploadFile(uploadId, mRequest.getName(), mSrcStream, mSrcLength, plainFileKey);

        EncryptedFileKey encryptedFileKey = mClient.getNodesImpl().encryptFileKey(null, plainFileKey,
                mUserPublicKey);

        ApiNode apiNode = completeUpload(uploadId, mRequest.getName(),
                mRequest.getResolutionStrategy(), encryptedFileKey);

        Node node = NodeMapper.fromApiNode(apiNode);

        notifyFinished(mId, node);

        return node;
    }

    private void uploadFile(String uploadId, String fileName, InputStream is, long length,
            PlainFileKey plainFileKey) throws DracoonFileIOException, DracoonCryptoException,
            DracoonNetIOException, DracoonApiException, InterruptedException {
        FileEncryptionCipher cipher;
        try {
            cipher = Crypto.createFileEncryptionCipher(plainFileKey);
        } catch (CryptoException e) {
            String errorText = String.format("Encryption failed at upload '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }

        byte[] buffer = new byte[mChunkSize];
        long offset = 0;
        int count;

        try {
            while ((count = is.read(buffer)) != -1) {
                byte[] plainBytes = createByteArray(buffer, count);

                PlainDataContainer plainData = new PlainDataContainer(plainBytes);
                EncryptedDataContainer encData = cipher.processBytes(plainData);

                byte[] encBytes = encData.getContent();
                uploadFileChunk(uploadId, fileName, encBytes, offset, encBytes.length, length);

                offset = offset + encBytes.length;
            }

            EncryptedDataContainer encData = cipher.doFinal();

            byte[] encBytes = encData.getContent();
            uploadFileChunk(uploadId, fileName, encBytes, offset, encBytes.length, length);

            String encTag = CryptoUtils.byteArrayToString(encData.getTag());
            plainFileKey.setTag(encTag);
        } catch (IllegalArgumentException | IllegalStateException | CryptoSystemException e) {
            String errorText = String.format("Encryption failed at upload '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        } catch (IOException e) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = String.format("File read failed at upload '%s'!", mId);
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        }
    }

    private static byte[] createByteArray(byte[] bytes, int len) {
        byte[] b = new byte[len];
        System.arraycopy(bytes, 0, b, 0, len);
        return b;
    }

    private ApiNode completeUpload(String uploadId, String fileName,
            ResolutionStrategy resolutionStrategy, EncryptedFileKey encryptedFileKey)
            throws DracoonNetIOException, DracoonApiException, InterruptedException {
        String auth = mClient.buildAuthString();

        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = fileName;
        request.resolutionStrategy = resolutionStrategy.getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<ApiNode> call = mRestService.completeFileUpload(auth, uploadId, request);
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

}
