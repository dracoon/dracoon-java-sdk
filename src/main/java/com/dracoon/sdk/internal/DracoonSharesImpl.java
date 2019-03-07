package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.filter.Filters;
import com.dracoon.sdk.filter.GetDownloadSharesFilter;
import com.dracoon.sdk.filter.GetUploadSharesFilter;
import com.dracoon.sdk.internal.mapper.ShareMapper;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiCreateUploadShareRequest;
import com.dracoon.sdk.internal.model.ApiDownloadShare;
import com.dracoon.sdk.internal.model.ApiDownloadShareList;
import com.dracoon.sdk.internal.model.ApiUploadShare;
import com.dracoon.sdk.internal.model.ApiUploadShareList;
import com.dracoon.sdk.internal.util.EncodingUtils;
import com.dracoon.sdk.internal.validator.BaseValidator;
import com.dracoon.sdk.internal.validator.ShareValidator;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.DownloadShareList;
import com.dracoon.sdk.model.UploadShare;
import com.dracoon.sdk.model.UploadShareList;
import retrofit2.Call;
import retrofit2.Response;

public class DracoonSharesImpl extends DracoonRequestHandler implements DracoonClient.Shares {

    private static final String LOG_TAG = DracoonSharesImpl.class.getSimpleName();
    private static final String SHARES_QR_PREFIX = "data:image/png;base64,";

    DracoonSharesImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public DownloadShare createDownloadShare(CreateDownloadShareRequest request)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();

        boolean isEncrypted = mClient.getNodesImpl().isNodeEncrypted(request.getNodeId());

        ShareValidator.validateCreateDownloadRequest(request, isEncrypted);

        UserKeyPair userKeyPair = null;
        EncryptedFileKey userEncFileKey = null;
        if (isEncrypted) {
            long nodeId = request.getNodeId();

            UserKeyPair creatorKeyPair = mClient.getAccountImpl().getAndCheckUserKeyPair();
            String creatorEncPw = mClient.getEncryptionPassword();
            EncryptedFileKey creatorEncFileKey = mClient.getNodesImpl().getFileKey(nodeId);

            PlainFileKey plainFileKey = mClient.getNodesImpl().decryptFileKey(nodeId,
                    creatorEncFileKey, creatorKeyPair.getUserPrivateKey(), creatorEncPw);

            String userEncPw = request.getEncryptionPassword();
            userKeyPair = mClient.getAccountImpl().generateUserKeyPair(userEncPw);
            userEncFileKey = mClient.getNodesImpl().encryptFileKey(nodeId, plainFileKey,
                    userKeyPair.getUserPublicKey());
        }

        String auth = mClient.buildAuthString();
        ApiCreateDownloadShareRequest apiRequest = ShareMapper.toApiCreateDownloadShareRequest(
                request, userKeyPair, userEncFileKey);
        Call<ApiDownloadShare> call = mService.createDownloadShare(auth, apiRequest);
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

    @Override
    public DownloadShareList getDownloadShares() throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(null, null, null);
    }

    @Override
    public DownloadShareList getDownloadShares(long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(null, offset, limit);
    }

    @Override
    public DownloadShareList getDownloadShares(GetDownloadSharesFilter filters)
            throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(filters, null, null);
    }

    @Override
    public DownloadShareList getDownloadShares(GetDownloadSharesFilter filters, long offset,
            long limit) throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(filters, offset, limit);
    }

    private DownloadShareList getDownloadSharesInternally(Filters filters, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        ShareValidator.validateRange(offset, limit, true);

        String auth = mClient.buildAuthString();
        String filter = filters != null ? filters.toString() : null;
        Call<ApiDownloadShareList> call = mService.getDownloadShares(auth, filter, offset, limit);
        Response<ApiDownloadShareList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadSharesGetError(response);
            String errorText = String.format("Query of get download shares failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadShareList data = response.body();
        return ShareMapper.fromApiDownloadShareList(data);
    }

    @Override
    public byte[] getDownloadShareQRCode(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        ShareValidator.validateShareId(shareId);

        String auth = mClient.buildAuthString();
        Call<ApiDownloadShare> call = mService.getDownloadShareQR(auth, shareId);
        Response<ApiDownloadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadSharesGetError(response);
            String errorText = String.format("Query of get download share qr failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        String base64QrString = ShareMapper.fromApiDownloadShareQRCode(response.body());
        String base64QrStringWithoutPrefix = base64QrString.substring(SHARES_QR_PREFIX.length());
        return EncodingUtils.decodeBase64(base64QrStringWithoutPrefix);
    }

    @Override
    public void deleteDownloadShare(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteDownloadShare(auth, shareId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadShareDeleteError(response);
            String errorText = String.format("Deletion of download share %s failed with '%s'!",
                    shareId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @Override
    public UploadShare createUploadShare(CreateUploadShareRequest request)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        ShareValidator.validateCreateUploadRequest(request);

        String auth = mClient.buildAuthString();
        ApiCreateUploadShareRequest apiRequest = ShareMapper.toApiCreateUploadShareRequest(request);
        Call<ApiUploadShare> call = mService.createUploadShare(auth, apiRequest);
        Response<ApiUploadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadShareCreateError(response);
            String errorText = String.format("Creation of upload share for node '%d' failed " +
                    "with '%s'!", request.getTargetNodeId(), errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUploadShare data = response.body();

        return ShareMapper.fromApiUploadShare(data);
    }

    @Override
    public UploadShareList getUploadShares() throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(null, null, null);
    }

    @Override
    public UploadShareList getUploadShares(long offset, long limit) throws DracoonNetIOException,
            DracoonApiException {
        return getUploadSharesInternally(null, offset, limit);
    }

    @Override
    public UploadShareList getUploadShares(GetUploadSharesFilter filters)
            throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(filters, null, null);
    }

    @Override
    public UploadShareList getUploadShares(GetUploadSharesFilter filters, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(filters, offset, limit);
    }

    private UploadShareList getUploadSharesInternally(Filters filters, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        ShareValidator.validateRange(offset, limit, true);

        String auth = mClient.buildAuthString();
        String filter = filters != null ? filters.toString() : null;
        Call<ApiUploadShareList> call = mService.getUploadShares(auth, filter, offset, limit);
        Response<ApiUploadShareList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadSharesGetError(response);
            String errorText = String.format("Query of get upload shares failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUploadShareList data = response.body();
        return ShareMapper.fromApiUploadShareList(data);
    }

    @Override
    public byte[] getUploadShareQRCode(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        ShareValidator.validateShareId(shareId);

        String auth = mClient.buildAuthString();
        Call<ApiUploadShare> call = mService.getUploadShareQR(auth, shareId);
        Response<ApiUploadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadSharesGetError(response);
            String errorText = String.format("Query of get upload share qr failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        String base64QrString = ShareMapper.fromApiUploadShareQRCode(response.body());
        return EncodingUtils.decodeBase64(base64QrString);
    }

    @Override
    public void deleteUploadShare(long shareId) throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteUploadShare(auth, shareId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadShareDeleteError(response);
            String errorText = String.format("Deletion of upload share %s failed with '%s'!",
                    shareId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

}
