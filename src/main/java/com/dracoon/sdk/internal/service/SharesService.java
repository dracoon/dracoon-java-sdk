package com.dracoon.sdk.internal.service;

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
import com.dracoon.sdk.internal.ClientImpl;
import com.dracoon.sdk.internal.ClientMethodImpl;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.api.mapper.ShareMapper;
import com.dracoon.sdk.internal.api.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.api.model.ApiCreateUploadShareRequest;
import com.dracoon.sdk.internal.api.model.ApiDownloadShare;
import com.dracoon.sdk.internal.api.model.ApiDownloadShareList;
import com.dracoon.sdk.internal.api.model.ApiUploadShare;
import com.dracoon.sdk.internal.api.model.ApiUploadShareList;
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

@ClientImpl(DracoonClient.Shares.class)
public class SharesService extends BaseService {

    private static final String LOG_TAG = SharesService.class.getSimpleName();

    public SharesService(DracoonClientImpl client) {
        super(client);
    }

    @ClientMethodImpl
    public DownloadShare createDownloadShare(CreateDownloadShareRequest request)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        long nodeId = request.getNodeId();

        PlainFileKey plainFileKey = mServiceLocator.getFileKeyFetcher().getPlainFileKey(nodeId);

        boolean isEncrypted = plainFileKey != null;
        ShareValidator.validateCreateDownloadRequest(request, isEncrypted);

        UserKeyPair shareUserKeyPair = null;
        EncryptedFileKey shareEncFileKey = null;
        if (plainFileKey != null) {
            UserKeyPair.Version userKeyPairVersion = mServiceLocator.getServerSettingsService()
                    .getPreferredUserKeyPairVersion();

            char[] userEncPw = request.getEncryptionPassword();
            shareUserKeyPair = mCryptoWrapper.generateUserKeyPair(userKeyPairVersion, userEncPw);

            shareEncFileKey = mCryptoWrapper.encryptFileKey(nodeId, plainFileKey,
                    shareUserKeyPair.getUserPublicKey());
        }

        ApiCreateDownloadShareRequest apiRequest = ShareMapper.toApiCreateDownloadShareRequest(
                request, shareUserKeyPair, shareEncFileKey);
        Call<ApiDownloadShare> call = mApi.createDownloadShare(apiRequest);
        Response<ApiDownloadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadShareCreateError(response);
            String errorText = String.format("Creation of download share for node '%d' failed " +
                    "with '%s'!", nodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadShare data = response.body();

        return ShareMapper.fromApiDownloadShare(data);
    }

    @ClientMethodImpl
    public DownloadShareList getDownloadShares() throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(null, null, null);
    }

    @ClientMethodImpl
    public DownloadShareList getDownloadShares(long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(null, offset, limit);
    }

    @ClientMethodImpl
    public DownloadShareList getDownloadShares(GetDownloadSharesFilter filters)
            throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(filters, null, null);
    }

    @ClientMethodImpl
    public DownloadShareList getDownloadShares(GetDownloadSharesFilter filters, long offset,
            long limit) throws DracoonNetIOException, DracoonApiException {
        return getDownloadSharesInternally(filters, offset, limit);
    }

    private DownloadShareList getDownloadSharesInternally(Filters filters, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        BaseValidator.validateRange(offset, limit, true);

        String filter = filters != null ? filters.toString() : null;
        Call<ApiDownloadShareList> call = mApi.getDownloadShares(filter, offset, limit);
        Response<ApiDownloadShareList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadSharesQueryError(response);
            String errorText = String.format("Query of download shares failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadShareList data = response.body();
        return ShareMapper.fromApiDownloadShareList(data);
    }

    @ClientMethodImpl
    public byte[] getDownloadShareQrCode(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        BaseValidator.validateShareId(shareId);

        Call<ApiDownloadShare> call = mApi.getDownloadShareQR(shareId);
        Response<ApiDownloadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadSharesQueryError(response);
            String errorText = String.format("Query of download share QR code failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return ShareMapper.fromApiDownloadShareQrCode(response.body());
    }

    @ClientMethodImpl
    public void deleteDownloadShare(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        Call<Void> call = mApi.deleteDownloadShare(shareId);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadShareDeleteError(response);
            String errorText = String.format("Deletion of download share %s failed with '%s'!",
                    shareId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @ClientMethodImpl
    public UploadShare createUploadShare(CreateUploadShareRequest request)
            throws DracoonNetIOException, DracoonApiException {
        ShareValidator.validateCreateUploadRequest(request);

        ApiCreateUploadShareRequest apiRequest = ShareMapper.toApiCreateUploadShareRequest(request);
        Call<ApiUploadShare> call = mApi.createUploadShare(apiRequest);
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

    @ClientMethodImpl
    public UploadShareList getUploadShares() throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(null, null, null);
    }

    @ClientMethodImpl
    public UploadShareList getUploadShares(long offset, long limit) throws DracoonNetIOException,
            DracoonApiException {
        return getUploadSharesInternally(null, offset, limit);
    }

    @ClientMethodImpl
    public UploadShareList getUploadShares(GetUploadSharesFilter filters)
            throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(filters, null, null);
    }

    @ClientMethodImpl
    public UploadShareList getUploadShares(GetUploadSharesFilter filters, long offset, long limit)
            throws DracoonNetIOException, DracoonApiException {
        return getUploadSharesInternally(filters, offset, limit);
    }

    private UploadShareList getUploadSharesInternally(Filters filters, Long offset, Long limit)
            throws DracoonNetIOException, DracoonApiException {
        BaseValidator.validateRange(offset, limit, true);

        String filter = filters != null ? filters.toString() : null;
        Call<ApiUploadShareList> call = mApi.getUploadShares(filter, offset, limit);
        Response<ApiUploadShareList> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadSharesQueryError(response);
            String errorText = String.format("Query of upload shares failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUploadShareList data = response.body();
        return ShareMapper.fromApiUploadShareList(data);
    }

    @ClientMethodImpl
    public byte[] getUploadShareQrCode(long shareId) throws DracoonNetIOException,
            DracoonApiException {
        BaseValidator.validateShareId(shareId);

        Call<ApiUploadShare> call = mApi.getUploadShareQR(shareId);
        Response<ApiUploadShare> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUploadSharesQueryError(response);
            String errorText = String.format("Query of upload share QR code failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return ShareMapper.fromApiUploadShareQrCode(response.body());
    }

    @ClientMethodImpl
    public void deleteUploadShare(long shareId) throws DracoonNetIOException, DracoonApiException {
        Call<Void> call = mApi.deleteUploadShare(shareId);
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
