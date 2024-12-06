package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.crypto.CryptoErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoVersionConverter;
import com.dracoon.sdk.internal.mapper.FileMapper;
import com.dracoon.sdk.internal.model.ApiFileKey;
import retrofit2.Call;
import retrofit2.Response;

class FileKeyFetcher {

    private static final String LOG_TAG = FileKeyFetcher.class.getSimpleName();

    private final DracoonClientImpl mClient;
    private final Log mLog;
    private final DracoonApi mApi;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    FileKeyFetcher(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mApi = client.getDracoonApi();
        mHttpHelper = client.getHttpHelper();
        mErrorParser = client.getDracoonErrorParser();
    }

    public PlainFileKey getPlainFileKey(long nodeId) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        if (!mClient.getNodesImpl().isNodeEncrypted(nodeId)) {
            return null;
        }

        char[] userPrivateKeyPassword = mClient.getEncryptionPasswordOrAbort();

        EncryptedFileKey encFileKey = getFileKey(nodeId);

        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.determineUserKeyPairVersion(
                encFileKey.getVersion());
        UserKeyPair userKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair(
                userKeyPairVersion);

        return mClient.getCryptoWrapper().decryptFileKey(nodeId, encFileKey,
                userKeyPair.getUserPrivateKey(), userPrivateKeyPassword);
    }

    private EncryptedFileKey getFileKey(long nodeId) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        Call<ApiFileKey> call = mApi.getFileKey(nodeId);
        Response<ApiFileKey> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeyQueryError(response);
            String errorText = String.format("Query of file key for node '%d' failed with " +
                    "'%s'!", nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiFileKey data = response.body();

        try {
            return FileMapper.fromApiFileKey(data);
        } catch (UnknownVersionException e) {
            String errorText = String.format("Query of file key for node '%d' failed! File key " +
                    "version is unknown!", nodeId);
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

}
