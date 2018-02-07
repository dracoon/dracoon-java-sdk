package com.dracoon.sdk.internal;

import com.dracoon.sdk.crypto.BadFileException;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoException;
import com.dracoon.sdk.crypto.CryptoSystemException;
import com.dracoon.sdk.crypto.CryptoUtils;
import com.dracoon.sdk.crypto.FileDecryptionCipher;
import com.dracoon.sdk.crypto.model.EncryptedDataContainer;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainDataContainer;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.model.ApiFileKey;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.OutputStream;

public class EncFileDownload extends FileDownload {

    private static final String LOG_TAG = EncFileDownload.class.getSimpleName();

    private final UserPrivateKey mUserPrivateKey;

    public EncFileDownload(DracoonClientImpl client, String id, long nodeId, OutputStream trgStream,
            UserPrivateKey userPrivateKey) {
        super(client, id, nodeId, trgStream);

        mUserPrivateKey = userPrivateKey;
    }

    protected void download() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException, InterruptedException {
        notifyStarted(mId);

        EncryptedFileKey encryptedFileKey = getFileKey(mNodeId);

        String userPrivateKeyPassword = mClient.getEncryptionPassword();
        PlainFileKey plainFileKey = mClient.getNodesImpl().decryptFileKey(mNodeId, encryptedFileKey,
                mUserPrivateKey, userPrivateKeyPassword);

        String downloadUrl = getDownloadUrl(mNodeId);

        downloadFile(downloadUrl, mTrgStream, plainFileKey);

        notifyFinished(mId);
    }

    private EncryptedFileKey getFileKey(long nodeId) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String auth = mClient.buildAuthString();

        Call<ApiFileKey> call = mRestService.getFileKey(auth, nodeId);
        Response<ApiFileKey> response = mHttpHelper.executeRequest(call, this);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeyQueryError(response);
            String errorText = String.format("Query of file key for download '%s' failed with " +
                    "'%s'!", mId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiFileKey data = response.body();

        return FileMapper.fromApiFileKey(data);
    }

    private void downloadFile(String downloadUrl, OutputStream outStream, PlainFileKey plainFileKey)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException,
            DracoonFileIOException, InterruptedException {
        FileDecryptionCipher cipher;
        try {
            cipher = Crypto.createFileDecryptionCipher(plainFileKey);
        } catch (CryptoException e) {
            String errorText = String.format("Decryption failed at upload '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }

        long offset = 0L;
        long length = getFileSize(mNodeId);

        try {
            while (offset < length) {
                long remaining = length - offset;
                int count = remaining > JUNK_SIZE ? JUNK_SIZE : (int) remaining;
                byte[] encBytes = downloadFileChunk(downloadUrl, offset, count, length);

                EncryptedDataContainer encData = new EncryptedDataContainer(encBytes, null);
                PlainDataContainer plainData = cipher.processBytes(encData);

                outStream.write(plainData.getContent());

                offset = offset + count;
            }

            byte[] encTag = CryptoUtils.stringToByteArray(plainFileKey.getTag());
            EncryptedDataContainer encData = new EncryptedDataContainer(null, encTag);
            PlainDataContainer plainData = cipher.doFinal(encData);

            outStream.write(plainData.getContent());
        } catch (BadFileException | IllegalArgumentException | IllegalStateException |
                CryptoSystemException e) {
            String errorText = String.format("Decryption failed at download '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        } catch (IOException e) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = "File write failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        }
    }

}
