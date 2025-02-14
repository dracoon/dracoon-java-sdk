package com.dracoon.sdk.internal.service;

import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.ClientImpl;
import com.dracoon.sdk.internal.ClientMethodImpl;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.api.mapper.CustomerMapper;
import com.dracoon.sdk.internal.api.mapper.UserMapper;
import com.dracoon.sdk.internal.api.model.ApiCustomerAccount;
import com.dracoon.sdk.internal.api.model.ApiUserAccount;
import com.dracoon.sdk.internal.api.model.ApiUserAvatarInfo;
import com.dracoon.sdk.internal.api.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.api.model.ApiUserProfileAttributes;
import com.dracoon.sdk.internal.crypto.CryptoErrorParser;
import com.dracoon.sdk.internal.crypto.CryptoVersionConverter;
import com.dracoon.sdk.internal.http.HttpStatus;
import com.dracoon.sdk.internal.validator.ValidatorUtils;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

@ClientImpl(DracoonClient.Account.class)
public class AccountService extends BaseService {

    private static final String LOG_TAG = AccountService.class.getSimpleName();

    public AccountService(DracoonClientImpl client) {
        super(client);
    }

    public void pingUser() throws DracoonNetIOException, DracoonApiException {
        Call<Void> call = mApi.pingUser();
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Auth ping failed with '%s'!", errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @ClientMethodImpl
    public UserAccount getUserAccount() throws DracoonNetIOException, DracoonApiException {
        Call<ApiUserAccount> accountCall = mApi.getUserAccount();
        Response<ApiUserAccount> accountResponse = mHttpHelper.executeRequest(accountCall);

        if (!accountResponse.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(accountResponse);
            String errorText = String.format("Query of user account failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserAccount accountData = accountResponse.body();

        Call<ApiUserAvatarInfo> avatarInfoCall = mApi.getUserAvatarInfo();
        Response<ApiUserAvatarInfo> avatarInfoResponse = mHttpHelper.executeRequest(avatarInfoCall);

        if (!avatarInfoResponse.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(avatarInfoResponse);
            String errorText = String.format("Query of user account failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserAvatarInfo avatarInfoData = avatarInfoResponse.body();

        return UserMapper.fromApiUserAccount(accountData, avatarInfoData);
    }

    @ClientMethodImpl
    public CustomerAccount getCustomerAccount() throws DracoonNetIOException, DracoonApiException {
        Call<ApiCustomerAccount> call = mApi.getCustomerAccount();
        Response<ApiCustomerAccount> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of customer account failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiCustomerAccount data = response.body();

        return CustomerMapper.fromApiCustomerAccount(data);
    }

    @ClientMethodImpl
    public List<UserKeyPairAlgorithm.Version> getUserKeyPairAlgorithmVersions()
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        List<UserKeyPair> userKeyPairs = getUserKeyPairs();

        ArrayList<UserKeyPairAlgorithm.Version> versions = new ArrayList<>();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            versions.add(CryptoVersionConverter.fromUserKeyPairVersion(
                    userKeyPair.getUserPrivateKey().getVersion()));
        }
        return versions;
    }

    @ClientMethodImpl
    public void setUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);

        checkUserKeyPairVersionSupported(userKeyPairVersion);

        char[] encryptionPassword = mEncPasswordHolder.getOrAbort();
        UserKeyPair userKeyPair = mCryptoWrapper.generateUserKeyPair(userKeyPairVersion,
                encryptionPassword);

        ApiUserKeyPair apiUserKeyPair = UserMapper.toApiUserKeyPair(userKeyPair);

        Call<Void> call = mApi.setUserKeyPair(apiUserKeyPair);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairSetError(response);
            String errorText = String.format("Setting user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private List<UserKeyPair> getUserKeyPairs() throws DracoonNetIOException, DracoonApiException {
        List<ApiUserKeyPair> apiUserKeyPairs = getAllUserKeyPairs();

        ArrayList<UserKeyPair> userKeyPairs = new ArrayList<>();
        for (ApiUserKeyPair apiUserKeyPair : apiUserKeyPairs) {
            try {
                userKeyPairs.add(UserMapper.fromApiUserKeyPair(apiUserKeyPair));
            } catch (UnknownVersionException e) {
                // Not supported key pairs are ignored
            }
        }
        return userKeyPairs;
    }

    private List<ApiUserKeyPair> getAllUserKeyPairs() throws DracoonNetIOException, DracoonApiException {
        Call<List<ApiUserKeyPair>> call = mApi.getUserKeyPairs();
        Response<List<ApiUserKeyPair>> response = mHttpHelper.executeRequest(call);

        if (existsNoUserKeyPair(response)) {
            return Collections.emptyList();
        }

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairsQueryError(response);
            String errorText = String.format("Query of user key pairs failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private static boolean existsNoUserKeyPair(Response<?> response) {
        return !response.isSuccessful() && response.code() == HttpStatus.NOT_FOUND.getNumber();
    }

    private UserKeyPair getUserKeyPair(UserKeyPair.Version userKeyPairVersion)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        checkUserKeyPairVersionSupported(userKeyPairVersion);

        Call<ApiUserKeyPair> call = mApi.getUserKeyPair(userKeyPairVersion.getValue());
        Response<ApiUserKeyPair> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairQueryError(response);
            String errorText = String.format("Query of user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserKeyPair data = response.body();

        try {
            return UserMapper.fromApiUserKeyPair(data);
        } catch (UnknownVersionException e) {
            String errorText = "Query of user key pair failed! Key pair version is unknown!";
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public UserKeyPair getPreferredUserKeyPair() throws DracoonNetIOException,
            DracoonApiException {
        List<UserKeyPairAlgorithm> userKeyPairAlgorithms = mServiceLocator.getServerSettingsService()
                .getAvailableUserKeyPairAlgorithms();

        List<UserKeyPair> userKeyPairs = getUserKeyPairs();

        for (UserKeyPairAlgorithm userKeyPairAlgorithm : userKeyPairAlgorithms) {
            Optional<UserKeyPair> userKeyPair = userKeyPairs.stream()
                    .filter(kp -> Objects.equals(kp.getUserPrivateKey().getVersion().getValue(),
                            userKeyPairAlgorithm.getVersion().getValue()))
                    .findAny();
            if (userKeyPair.isPresent()) {
                return userKeyPair.get();
            }
        }

        throw new DracoonApiException(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND);
    }

    public List<UserKeyPair> getAndCheckUserKeyPairs() throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        char[] encryptionPassword = mEncPasswordHolder.getOrAbort();

        List<UserKeyPair> userKeyPairs = getUserKeyPairs();
        if (userKeyPairs.isEmpty()) {
            throw new DracoonApiException(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND);
        }

        for (UserKeyPair userKeyPair : userKeyPairs) {
            checkUserKeyPair(userKeyPair, encryptionPassword);
        }
        return userKeyPairs;
    }

    public UserKeyPair getAndCheckUserKeyPair(UserKeyPair.Version userKeyPairVersion)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        char[] encryptionPassword = mEncPasswordHolder.getOrAbort();
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        checkUserKeyPair(userKeyPair, encryptionPassword);
        return userKeyPair;
    }

    private void checkUserKeyPair(UserKeyPair userKeyPair, char[] encryptionPassword)
            throws DracoonCryptoException {
        boolean isValid = mCryptoWrapper.checkUserKeyPairPassword(userKeyPair, encryptionPassword);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }
    }

    @ClientMethodImpl
    public void deleteUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);

        checkUserKeyPairVersionSupported(userKeyPairVersion);

        Call<Void> call = mApi.deleteUserKeyPair(userKeyPairVersion.getValue());
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairDeleteError(response);
            String errorText = String.format("Deleting user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private void checkUserKeyPairVersionSupported(UserKeyPair.Version version)
            throws DracoonNetIOException, DracoonApiException {
        if (version == null) {
            throw new IllegalArgumentException("Version can't be null.");
        }

        List<UserKeyPair.Version> versions = mServiceLocator.getServerSettingsService()
                .getAvailableUserKeyPairVersions();
        boolean apiSupportsVersion = versions.stream().anyMatch(v -> v == version);
        if (!apiSupportsVersion) {
            throw new DracoonApiException(DracoonApiCode.SERVER_CRYPTO_VERSION_NOT_SUPPORTED);
        }
    }

    @ClientMethodImpl
    public boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version)
            throws DracoonCryptoException, DracoonNetIOException, DracoonApiException {
        char[] encryptionPassword = mEncPasswordHolder.getOrAbort();
        return checkUserKeyPairPassword(version, encryptionPassword);
    }

    @ClientMethodImpl
    public boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version,
            char[] encryptionPassword) throws DracoonCryptoException, DracoonNetIOException,
            DracoonApiException {
        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        return mCryptoWrapper.checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    @ClientMethodImpl
    public void setUserProfileAttribute(String key, String value) throws DracoonNetIOException,
            DracoonApiException {
        ValidatorUtils.validateString("key", key, false);

        if (value != null) {
            ApiUserProfileAttributes.Item profileAttribute = new ApiUserProfileAttributes.Item();
            profileAttribute.key = key;
            profileAttribute.value = value;

            ApiUserProfileAttributes profileAttributes = new ApiUserProfileAttributes();
            profileAttributes.items = Collections.singletonList(profileAttribute);

            setUserProfileAttributes(profileAttributes);
        } else {
            deleteUserProfileAttribute(key);
        }
    }

    @ClientMethodImpl
    public String getUserProfileAttribute(String key) throws DracoonNetIOException,
            DracoonApiException {
        ValidatorUtils.validateString("key", key, false);

        ApiUserProfileAttributes profileAttributes = getUserProfileAttributes();
        if (profileAttributes.items == null) {
            return null;
        }

        for (ApiUserProfileAttributes.Item profileAttribute : profileAttributes.items) {
            if (Objects.equals(profileAttribute.key, key)) {
                return profileAttribute.value;
            }
        }
        return null;
    }

    private void setUserProfileAttributes(ApiUserProfileAttributes profileAttributes)
            throws DracoonNetIOException, DracoonApiException {
        Call<Void> call = mApi.setUserProfileAttributes(profileAttributes);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserProfileAttributesSetError(response);
            String errorText = String.format("Setting user profile attributes failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    private ApiUserProfileAttributes getUserProfileAttributes() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiUserProfileAttributes> call = mApi.getUserProfileAttributes();
        Response<ApiUserProfileAttributes> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserProfileAttributesQueryError(response);
            String errorText = String.format("Query of user profile attributes failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private void deleteUserProfileAttribute(String key) throws DracoonNetIOException,
            DracoonApiException {
        Call<Void> call = mApi.deleteUserProfileAttribute(key);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            if (response.code() == HttpStatus.NOT_FOUND.getNumber()) {
                return;
            }

            DracoonApiCode errorCode = mErrorParser.parseUserProfileAttributeDeleteError(response);
            String errorText = String.format("Deleting user profile attribute failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @ClientMethodImpl
    public void setUserAvatar(byte[] avatarImage) throws DracoonNetIOException, DracoonApiException {
        ValidatorUtils.validateByteArray("Avatar image", avatarImage, false, 1L,
                5L * (long) DracoonConstants.MIB);

        Call<Void> call = mApi.setUserAvatar(RequestBody.create(MediaType.parse(
                "application/octet-stream"), avatarImage));
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserAvatarSetError(response);
            String errorText = String.format("Setting user avatar failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @ClientMethodImpl
    public byte[] getUserAvatar() throws DracoonNetIOException, DracoonApiException {
        Call<ApiUserAvatarInfo> avatarInfoCall = mApi.getUserAvatarInfo();
        Response<ApiUserAvatarInfo> avatarInfoResponse = mHttpHelper.executeRequest(avatarInfoCall);

        if (!avatarInfoResponse.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(avatarInfoResponse);
            String errorText = String.format("Download of avatar failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserAvatarInfo avatarInfoData = avatarInfoResponse.body();
        String downloadUrl = avatarInfoData != null ? avatarInfoData.avatarUri : null;

        return mServiceLocator.getAvatarDownloader().downloadAvatar(downloadUrl);
    }

    @ClientMethodImpl
    public void deleteUserAvatar() throws DracoonNetIOException, DracoonApiException {
        Call<Void> call = mApi.deleteUserAvatar();
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserAvatarDeleteError(response);
            String errorText = String.format("Deleting user avatar failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

}
