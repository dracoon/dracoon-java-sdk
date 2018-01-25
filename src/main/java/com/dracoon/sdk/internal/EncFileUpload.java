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
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
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

    protected Node upload() throws DracoonException, InterruptedException {
        notifyStarted(mId);

        String uploadId = createUpload(mRequest.getParentId(), mRequest.getName(),
                mRequest.getClassification().getValue(), mRequest.getNotes(),
                mRequest.getExpiration());

        PlainFileKey plainFileKey = createFileKey(mUserPublicKey);

        uploadFile(uploadId, mRequest.getName(), mSrcStream, mSrcLength, plainFileKey);

        EncryptedFileKey encryptedFileKey = encryptFileKey(plainFileKey, mUserPublicKey);

        ApiNode apiNode = completeUpload(uploadId, mRequest.getName(),
                mRequest.getResolutionStrategy(), encryptedFileKey);

        Node node = NodeMapper.fromApiNode(apiNode);

        notifyFinished(mId, node);

        return node;
    }

    private PlainFileKey createFileKey(UserPublicKey userPublicKey) throws DracoonException {
        try {
            return Crypto.generateFileKey(userPublicKey.getVersion());
        } catch (CryptoException e) {
            String errorText = String.format("Creation of file key for upload '%s' failed! %s",
                    mId, e.getMessage());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonCryptoException(e);
        }
    }

    private void uploadFile(String uploadId, String fileName, InputStream is, long length,
            PlainFileKey plainFileKey) throws DracoonException, InterruptedException {
        FileEncryptionCipher cipher;
        try {
            cipher = Crypto.createFileEncryptionCipher(plainFileKey);
        } catch (CryptoException e) {
            String errorText = String.format("Encryption failed at upload '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonCryptoException(e);
        }

        byte[] buffer = new byte[JUNK_SIZE];
        long offset = 0;
        int count;

        try {
            while ((count = is.read(buffer)) != -1) {
                byte[] pBytes = createByteArray(buffer, count);
                EncryptedDataContainer eData = cipher.processBytes(new PlainDataContainer(pBytes));

                byte[] eBytes = eData.getContent();
                uploadFileChunk(uploadId, fileName, eBytes, offset, eBytes.length, length);

                offset = offset + eBytes.length;
            }

            EncryptedDataContainer eData = cipher.doFinal();

            byte[] eBytes = eData.getContent();
            uploadFileChunk(uploadId, fileName, eBytes, offset, eBytes.length, length);

            String eTag = CryptoUtils.byteArrayToString(eData.getTag());
            plainFileKey.setTag(eTag);
        } catch (IllegalArgumentException | IllegalStateException | CryptoSystemException e) {
            String errorText = String.format("Encryption failed at upload '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonCryptoException(e);
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

    private EncryptedFileKey encryptFileKey(PlainFileKey plainFileKey, UserPublicKey userPublicKey)
            throws DracoonException {
        try {
            return Crypto.encryptFileKey(plainFileKey, userPublicKey);
        } catch (CryptoException e) {
            String errorText = String.format("Encryption of file key for upload '%s' failed! %s",
                    mId, e.getMessage());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonCryptoException(e);
        }
    }

    private ApiNode completeUpload(String uploadId, String fileName,
            ResolutionStrategy resolutionStrategy, EncryptedFileKey encryptedFileKey)
            throws DracoonException, InterruptedException {
        String authToken = mClient.getAccessToken();

        ApiCompleteFileUploadRequest request = new ApiCompleteFileUploadRequest();
        request.fileName = fileName;
        request.resolutionStrategy = resolutionStrategy.getValue();
        request.fileKey = FileMapper.toApiFileKey(encryptedFileKey);

        Call<ApiNode> call = mRestService.completeFileUpload(authToken, uploadId, request);
        Response<ApiNode> response = mHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseCompleteFileUploadError(response);
            String errorText = String.format("Completion of upload '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

}
