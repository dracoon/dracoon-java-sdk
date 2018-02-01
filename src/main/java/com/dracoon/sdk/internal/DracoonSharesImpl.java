package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.ShareMapper;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiDownloadShare;
import com.dracoon.sdk.internal.validator.ShareValidator;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.DownloadShare;
import retrofit2.Call;
import retrofit2.Response;

public class DracoonSharesImpl extends DracoonRequestHandler implements DracoonClient.Shares {

    private static final String LOG_TAG = DracoonSharesImpl.class.getSimpleName();

    DracoonSharesImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public DownloadShare createDownloadShare(CreateDownloadShareRequest request)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        assertServerApiVersion();

        boolean isEncrypted = mClient.getNodesImpl().isNodeEncrypted(request.getNodeId());

        ShareValidator.validateCreateRequest(request, isEncrypted);

        UserKeyPair userKeyPair = null;
        EncryptedFileKey userEncFileKey = null;
        if (isEncrypted) {
            long nodeId = request.getNodeId();

            String creatorEncPw = mClient.getEncryptionPassword();
            UserKeyPair creatorKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            EncryptedFileKey creatorEncFileKey = mClient.getNodesImpl().getFileKey(nodeId);

            PlainFileKey plainFileKey = mClient.getNodesImpl().decryptFileKey(nodeId,
                    creatorEncFileKey, creatorKeyPair.getUserPrivateKey(), creatorEncPw);

            String userEncPw = request.getEncryptionPassword();
            userKeyPair = mClient.getAccountImpl().generateUserKeyPair(userEncPw);
            userEncFileKey = mClient.getNodesImpl().encryptFileKey(nodeId, plainFileKey,
                    userKeyPair.getUserPublicKey());
        }

        String accessToken = mClient.getAccessToken();
        ApiCreateDownloadShareRequest apiRequest = ShareMapper.toApiCreateDownloadShareRequest(
                request, userKeyPair, userEncFileKey);
        Call<ApiDownloadShare> call = mService.createDownloadShare(accessToken, apiRequest);
        Response<ApiDownloadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadShareCreateError(response);
            String errorText = String.format("Creation of download share for node '%d' failed " +
                    "with '%s'!", request.getNodeId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadShare data = response.body();

        return ShareMapper.fromApiDownloadShare(data);
    }

}
