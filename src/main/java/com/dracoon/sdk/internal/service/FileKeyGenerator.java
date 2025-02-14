package com.dracoon.sdk.internal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.crypto.model.UserPrivateKey;
import com.dracoon.sdk.crypto.model.UserPublicKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.api.mapper.FileMapper;
import com.dracoon.sdk.internal.api.mapper.UserMapper;
import com.dracoon.sdk.internal.api.model.ApiFileIdFileKey;
import com.dracoon.sdk.internal.api.model.ApiFileKey;
import com.dracoon.sdk.internal.api.model.ApiMissingFileKeys;
import com.dracoon.sdk.internal.api.model.ApiSetFileKeysRequest;
import com.dracoon.sdk.internal.api.model.ApiUserIdFileId;
import com.dracoon.sdk.internal.api.model.ApiUserIdFileIdFileKey;
import com.dracoon.sdk.internal.api.model.ApiUserIdUserPublicKey;
import com.dracoon.sdk.internal.crypto.CryptoVersionConverter;
import com.dracoon.sdk.internal.crypto.CryptoWrapper;
import com.dracoon.sdk.internal.crypto.EncryptionPasswordHolder;
import com.dracoon.sdk.internal.http.HttpHelper;
import com.dracoon.sdk.internal.validator.BaseValidator;
import retrofit2.Call;
import retrofit2.Response;

public class FileKeyGenerator {

    private static final String LOG_TAG = FileKeyGenerator.class.getSimpleName();

    private final ServiceLocator mServiceLocator;

    private final Log mLog;
    private final DracoonApi mApi;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    private final EncryptionPasswordHolder mEncPasswordHolder;
    private final CryptoWrapper mCryptoWrapper;

    public FileKeyGenerator(ServiceLocator serviceLocator, ServiceDependencies serviceDependencies) {
        mServiceLocator = serviceLocator;

        mLog = serviceDependencies.getLog();
        mApi = serviceDependencies.getDracoonApi();
        mHttpHelper = serviceDependencies.getHttpHelper();
        mErrorParser = serviceDependencies.getDracoonErrorParser();

        mEncPasswordHolder = serviceDependencies.getEncryptionPasswordHolder();
        mCryptoWrapper = serviceDependencies.getCryptoWrapper();
    }

    public boolean generateMissingFileKeys(Long nodeId, Integer limit) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        BaseValidator.validateLimit(limit, false);

        List<UserKeyPair> userKeyPairs = mServiceLocator.getAccountService().getAndCheckUserKeyPairs();
        Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys = convertUserPrivateKeys(
                userKeyPairs);
        char[] userPrivateKeyPassword = mEncPasswordHolder.getOrAbort();

        boolean isFinished = false;
        long batchOffset = 0L;
        long batchMaxLimit = 10L;
        while (!isFinished) {
            long batchLimit = Math.min(limit - batchOffset, batchMaxLimit);
            isFinished = generateMissingFileKeysBatch(userPrivateKeys,
                    userPrivateKeyPassword, nodeId, batchOffset, batchLimit);
            batchOffset = batchOffset + batchLimit;
            if (batchOffset >= limit) {
                break;
            }
        }
        return isFinished;
    }

    private static Map<UserKeyPair.Version, UserPrivateKey> convertUserPrivateKeys(
            List<UserKeyPair> userKeyPairs) {
        Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys = new HashMap<>();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            UserPrivateKey userPrivateKey = userKeyPair.getUserPrivateKey();
            userPrivateKeys.put(userPrivateKey.getVersion(), userPrivateKey);
        }
        return userPrivateKeys;
    }

    private boolean generateMissingFileKeysBatch(Map<UserKeyPair.Version,
            UserPrivateKey> userPrivateKeys, char[] userPrivateKeyPassword, Long nodeId,
            long offset, long limit) throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        ApiMissingFileKeys apiMissingFileKeys = getMissingFileKeysBatch(nodeId, offset, limit);
        if (apiMissingFileKeys.items.isEmpty()) {
            return true;
        }

        List<ApiUserIdFileId> apiUserIdFileIds = apiMissingFileKeys.items;
        Map<Long, List<UserPublicKey>> usersPublicKeys = convertUserPublicKeys(
                apiMissingFileKeys.users);
        Map<Long, List<EncryptedFileKey>> encFilesKeys = convertFileKeys(apiMissingFileKeys.files);
        Map<Long, PlainFileKey> plainFileKeys = decryptFileKeys(encFilesKeys, userPrivateKeys,
                userPrivateKeyPassword);

        List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys = new ArrayList<>();

        for (ApiUserIdFileId apiUserIdFileId : apiUserIdFileIds) {
            List<UserPublicKey> userPublicKeys = usersPublicKeys.get(apiUserIdFileId.userId);
            PlainFileKey plainFileKey = plainFileKeys.get(apiUserIdFileId.fileId);
            if (userPublicKeys == null || plainFileKey == null) {
                continue;
            }

            for (UserPublicKey userPublicKey : userPublicKeys) {
                EncryptedFileKey encFileKey = mCryptoWrapper.encryptFileKey(apiUserIdFileId.fileId,
                        plainFileKey, userPublicKey);

                ApiFileKey apiFileKey = FileMapper.toApiFileKey(encFileKey);

                ApiUserIdFileIdFileKey apiUserIdFileIdFileKey = new ApiUserIdFileIdFileKey();
                apiUserIdFileIdFileKey.userId = apiUserIdFileId.userId;
                apiUserIdFileIdFileKey.fileId = apiUserIdFileId.fileId;
                apiUserIdFileIdFileKey.fileKey = apiFileKey;

                apiUserIdFileIdFileKeys.add(apiUserIdFileIdFileKey);
            }
        }

        setFileKeysBatch(apiUserIdFileIdFileKeys);

        return apiMissingFileKeys.range.total <= offset + limit;
    }

    private ApiMissingFileKeys getMissingFileKeysBatch(Long nodeId, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        Call<ApiMissingFileKeys> call = mApi.getMissingFileKeys(nodeId, offset, limit);
        Response<ApiMissingFileKeys> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseMissingFileKeysQueryError(response);
            String errorText = String.format("Query of missing file keys failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private static Map<Long, List<UserPublicKey>> convertUserPublicKeys(
            List<ApiUserIdUserPublicKey> apiUserIdUserPublicKeys) {
        Map<Long, List<UserPublicKey>> usersPublicKeys = new HashMap<>();
        for (ApiUserIdUserPublicKey apiUserIdUserPublicKey : apiUserIdUserPublicKeys) {
            try {
                UserPublicKey userPublicKey = UserMapper.fromApiUserPublicKey(
                        apiUserIdUserPublicKey.publicKeyContainer);
                List<UserPublicKey> userPublicKeys = usersPublicKeys.get(apiUserIdUserPublicKey.id);
                if (userPublicKeys != null) {
                    userPublicKeys.add(userPublicKey);
                } else {
                    usersPublicKeys.put(apiUserIdUserPublicKey.id, Collections.singletonList(
                            userPublicKey));
                }
            } catch (UnknownVersionException e) {
                // Not supported public keys are ignored
            }
        }
        return usersPublicKeys;
    }

    private static Map<Long, List<EncryptedFileKey>> convertFileKeys(
            List<ApiFileIdFileKey> apiFileIdFileKeys) {
        Map<Long, List<EncryptedFileKey>> encFilesKeys = new HashMap<>();
        for (ApiFileIdFileKey apiFileIdFileKey : apiFileIdFileKeys) {
            try {
                EncryptedFileKey encFileKey = FileMapper.fromApiFileKey(
                        apiFileIdFileKey.fileKeyContainer);
                List<EncryptedFileKey> encFileKeys = encFilesKeys.get(apiFileIdFileKey.id);
                if (encFileKeys != null) {
                    encFileKeys.add(encFileKey);
                } else {
                    encFilesKeys.put(apiFileIdFileKey.id, Collections.singletonList(encFileKey));
                }
            } catch (UnknownVersionException e) {
                // Not supported public keys are ignored
            }
        }
        return encFilesKeys;
    }

    private Map<Long, PlainFileKey> decryptFileKeys(Map<Long, List<EncryptedFileKey>> encFilesKeys,
            Map<UserKeyPair.Version, UserPrivateKey> userPrivateKeys, char[] userPrivateKeyPassword)
            throws DracoonCryptoException {
        Map<Long, PlainFileKey> plainFileKeys = new HashMap<>();
        for (Map.Entry<Long, List<EncryptedFileKey>> encFileKeys : encFilesKeys.entrySet()) {
            for (EncryptedFileKey encFileKey : encFileKeys.getValue()) {
                UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter
                        .determineUserKeyPairVersion(encFileKey.getVersion());

                UserPrivateKey userPrivateKey = userPrivateKeys.get(userKeyPairVersion);
                if (userPrivateKey != null) {
                    PlainFileKey plainFileKey = mCryptoWrapper.decryptFileKey(encFileKeys.getKey(),
                            encFileKey, userPrivateKey, userPrivateKeyPassword);
                    plainFileKeys.put(encFileKeys.getKey(), plainFileKey);
                    break;
                }
            }
        }
        return plainFileKeys;
    }

    private void setFileKeysBatch(List<ApiUserIdFileIdFileKey> apiUserIdFileIdFileKeys)
            throws DracoonNetIOException, DracoonApiException {
        ApiSetFileKeysRequest request = new ApiSetFileKeysRequest();
        request.items = apiUserIdFileIdFileKeys;
        Call<Void> call = mApi.setFileKeys(request);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseFileKeysSetError(response);
            String errorText = String.format("Setting missing file keys failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

}
