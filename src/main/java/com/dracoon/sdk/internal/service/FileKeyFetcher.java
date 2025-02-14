package com.dracoon.sdk.internal.service;

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
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.api.mapper.FileMapper;
import com.dracoon.sdk.internal.api.model.ApiFileKey;
import com.dracoon.sdk.internal.crypto.CryptoErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoVersionConverter;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;
import retrofit2.Call;
import retrofit2.Response;

public class FileKeyFetcher {

    private static final String LOG_TAG = FileKeyFetcher.class.getSimpleName();

    private final ServiceLocator mServiceLocator;

    private final Log mLog;
    private final DracoonApi mApi;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    private final EncryptionPasswordHolder mEncPasswordHolder;
    private final CryptoWrapper mCryptoWrapper;

    public FileKeyFetcher(ServiceLocator serviceLocator, ServiceDependencies serviceDependencies) {
        mServiceLocator = serviceLocator;

        mLog = serviceDependencies.getLog();
        mApi = serviceDependencies.getDracoonApi();
        mHttpHelper = serviceDependencies.getHttpHelper();
        mErrorParser = serviceDependencies.getDracoonErrorParser();

        mEncPasswordHolder = serviceDependencies.getEncryptionPasswordHolder();
        mCryptoWrapper = serviceDependencies.getCryptoWrapper();
    }

    public PlainFileKey getPlainFileKey(long nodeId) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        if (!mServiceLocator.getNodesService().isNodeEncrypted(nodeId)) {
            return null;
        }

        char[] userPrivateKeyPassword = mEncPasswordHolder.getOrAbort();

        EncryptedFileKey encFileKey = getFileKey(nodeId);

        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.determineUserKeyPairVersion(
                encFileKey.getVersion());
        UserKeyPair userKeyPair = mServiceLocator.getAccountService().getAndCheckUserKeyPair(
                userKeyPairVersion);

        return mCryptoWrapper.decryptFileKey(nodeId, encFileKey, userKeyPair.getUserPrivateKey(),
                userPrivateKeyPassword);
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
