package com.dracoon.sdk.internal;

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
import com.dracoon.sdk.internal.mapper.CustomerMapper;
import com.dracoon.sdk.internal.mapper.UserMapper;
import com.dracoon.sdk.internal.model.ApiCustomerAccount;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserAvatarInfo;
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.model.ApiUserProfileAttributes;
import com.dracoon.sdk.internal.validator.ValidatorUtils;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class DracoonAccountImpl extends DracoonRequestHandler implements DracoonClient.Account {

    private static final String LOG_TAG = DracoonAccountImpl.class.getSimpleName();

    DracoonAccountImpl(DracoonClientImpl client) {
        super(client);
    }

    public void pingUser() throws DracoonNetIOException, DracoonApiException {
        Call<Void> call = mService.pingUser();
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Auth ping failed with '%s'!", errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @Override
    public UserAccount getUserAccount() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiUserAccount> accountCall = mService.getUserAccount();
        Response<ApiUserAccount> accountResponse = mHttpHelper.executeRequest(accountCall);

        if (!accountResponse.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(accountResponse);
            String errorText = String.format("Query of user account failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserAccount accountData = accountResponse.body();

        Call<ApiUserAvatarInfo> avatarInfoCall = mService.getUserAvatarInfo();
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

    @Override
    public CustomerAccount getCustomerAccount() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiCustomerAccount> call = mService.getCustomerAccount();
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

    @Override
    public List<UserKeyPairAlgorithm.Version> getUserKeyPairAlgorithmVersions()
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();

        List<UserKeyPair> userKeyPairs = getUserKeyPairs();

        ArrayList<UserKeyPairAlgorithm.Version> versions = new ArrayList<>();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            versions.add(CryptoVersionConverter.fromUserKeyPairVersion(
                    userKeyPair.getUserPrivateKey().getVersion()));
        }
        return versions;
    }

    @Override
    public void setUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);

        mClient.assertUserKeyPairVersionSupported(userKeyPairVersion);

        char[] encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        CryptoWrapper crypto = mClient.getCryptoWrapper();
        UserKeyPair userKeyPair = crypto.generateUserKeyPair(userKeyPairVersion, encryptionPassword);

        ApiUserKeyPair apiUserKeyPair = UserMapper.toApiUserKeyPair(userKeyPair);

        Call<Void> call = mService.setUserKeyPair(apiUserKeyPair);
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
        mClient.assertApiVersionSupported();

        List<ApiUserKeyPair> apiUserKeyPairs;
        if (mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS)) {
            apiUserKeyPairs = getAllUserKeyPairs();
        } else {
            apiUserKeyPairs = getOneUserKeyPair();
        }

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
        Call<List<ApiUserKeyPair>> call = mService.getUserKeyPairs();
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

    private List<ApiUserKeyPair> getOneUserKeyPair() throws DracoonNetIOException, DracoonApiException {
        Call<ApiUserKeyPair> call = mService.getUserKeyPair(null);
        Response<ApiUserKeyPair> response = mHttpHelper.executeRequest(call);

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

        return Collections.singletonList(response.body());
    }

    private static boolean existsNoUserKeyPair(Response<?> response) {
        return !response.isSuccessful() && response.code() == HttpStatus.NOT_FOUND.getNumber();
    }

    private UserKeyPair getUserKeyPair(UserKeyPair.Version userKeyPairVersion)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();
        mClient.assertUserKeyPairVersionSupported(userKeyPairVersion);

        Call<ApiUserKeyPair> call = mService.getUserKeyPair(userKeyPairVersion.getValue());
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
        List<UserKeyPairAlgorithm> userKeyPairAlgorithms = mClient.getServerSettingsImpl()
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
        char[] encryptionPassword = mClient.getEncryptionPasswordOrAbort();

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
        char[] encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        checkUserKeyPair(userKeyPair, encryptionPassword);
        return userKeyPair;
    }

    private void checkUserKeyPair(UserKeyPair userKeyPair, char[] encryptionPassword)
            throws DracoonCryptoException {
        CryptoWrapper crypto = mClient.getCryptoWrapper();
        boolean isValid = crypto.checkUserKeyPairPassword(userKeyPair, encryptionPassword);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }
    }

    @Override
    public void deleteUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);

        mClient.assertUserKeyPairVersionSupported(userKeyPairVersion);

        Call<Void> call = mService.deleteUserKeyPair(userKeyPairVersion.getValue());
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairDeleteError(response);
            String errorText = String.format("Deleting user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    @Override
    public boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version)
            throws DracoonCryptoException, DracoonNetIOException, DracoonApiException {
        char[] password = mClient.getEncryptionPasswordOrAbort();
        return checkUserKeyPairPassword(version, password);
    }

    @Override
    public boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version,
            String encryptionPassword) throws DracoonCryptoException, DracoonNetIOException,
            DracoonApiException {
        char[] password = encryptionPassword != null ? encryptionPassword.toCharArray() : null;
        return checkUserKeyPairPassword(version, password);
    }

    private boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version,
            char[] encryptionPassword) throws DracoonCryptoException, DracoonNetIOException,
            DracoonApiException {
        UserKeyPair.Version userKeyPairVersion = CryptoVersionConverter.toUserKeyPairVersion(version);
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        CryptoWrapper crypto = mClient.getCryptoWrapper();
        return crypto.checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    @Override
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

    @Override
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
        mClient.assertApiVersionSupported();

        Call<Void> call = mService.setUserProfileAttributes(profileAttributes);
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
        mClient.assertApiVersionSupported();

        Call<ApiUserProfileAttributes> call = mService.getUserProfileAttributes();
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
        mClient.assertApiVersionSupported();

        Call<Void> call = mService.deleteUserProfileAttribute(key);
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

    @Override
    public void setUserAvatar(byte[] avatarImage) throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        ValidatorUtils.validateByteArray("Avatar image", avatarImage, false, 1L,
                5L * (long) DracoonConstants.MIB);

        Call<Void> call = mService.setUserAvatar(RequestBody.create(MediaType.parse(
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

    @Override
    public byte[] getUserAvatar() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiUserAvatarInfo> avatarInfoCall = mService.getUserAvatarInfo();
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

        return mClient.getAvatarDownloader().downloadAvatar(downloadUrl);
    }

    @Override
    public void deleteUserAvatar() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<Void> call = mService.deleteUserAvatar();
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
