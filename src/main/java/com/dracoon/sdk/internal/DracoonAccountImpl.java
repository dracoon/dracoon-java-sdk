package com.dracoon.sdk.internal;

import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.error.CryptoException;
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
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.model.ApiUserProfileAttributes;
import com.dracoon.sdk.internal.validator.ValidatorUtils;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.UserAccount;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
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

        Call<ApiUserAccount> call = mService.getUserAccount();
        Response<ApiUserAccount> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of user account failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserAccount data = response.body();

        return UserMapper.fromApiUserAccount(data);
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
            versions.add(DracoonClientImpl.fromUserKeyPairVersion(
                    userKeyPair.getUserPrivateKey().getVersion()));
        }
        return versions;
    }

    @Override
    public void setUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.toUserKeyPairVersion(version);

        mClient.assertUserKeyPairVersionSupported(userKeyPairVersion);

        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = generateUserKeyPair(userKeyPairVersion, encryptionPassword);

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

    public UserKeyPair generateUserKeyPair(UserKeyPair.Version userKeyPairVersion, String password)
            throws DracoonCryptoException {
        try {
            return Crypto.generateUserKeyPair(userKeyPairVersion, password);
        } catch (CryptoException e) {
            String errorText = String.format("Generation of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    private List<UserKeyPair> getUserKeyPairs() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        List<ApiUserKeyPair> apiUserKeyPairs;
        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS)) {
            apiUserKeyPairs = getOneUserKeyPair();
        } else {
            apiUserKeyPairs = getAllUserKeyPairs();
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

    private static boolean existsNoUserKeyPair(Response response) {
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
        List<UserKeyPairAlgorithm> userKeyPairAlgorithms = mClient.server().settings()
                .getAvailableUserKeyPairAlgorithms();

        List<UserKeyPair> userKeyPairs = getUserKeyPairs();

        for (UserKeyPairAlgorithm userKeyPairAlgorithm : userKeyPairAlgorithms) {
            Optional<UserKeyPair> userKeyPair = userKeyPairs.stream().findAny().filter(
                    kp -> Objects.equals(kp.getUserPrivateKey().getVersion().getValue(),
                            userKeyPairAlgorithm.getVersion().getValue()));
            if (userKeyPair.isPresent()) {
                return userKeyPair.get();
            }
        }

        throw new DracoonApiException(DracoonApiCode.SERVER_USER_KEY_PAIR_NOT_FOUND);
    }

    public List<UserKeyPair> getAndCheckUserKeyPairs() throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        List<UserKeyPair> userKeyPairs = getUserKeyPairs();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            checkUserKeyPair(userKeyPair, encryptionPassword);
        }
        return userKeyPairs;
    }

    public UserKeyPair getAndCheckUserKeyPair(UserKeyPair.Version userKeyPairVersion)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        checkUserKeyPair(userKeyPair, encryptionPassword);
        return userKeyPair;
    }

    private void checkUserKeyPair(UserKeyPair userKeyPair, String encryptionPassword)
            throws DracoonCryptoException {
        boolean isValid = checkUserKeyPairPassword(userKeyPair, encryptionPassword);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }
    }

    @Override
    public void deleteUserKeyPair(UserKeyPairAlgorithm.Version version) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.toUserKeyPairVersion(version);

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
        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.toUserKeyPairVersion(version);
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        return checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    @Override
    public boolean checkUserKeyPairPassword(UserKeyPairAlgorithm.Version version,
            String encryptionPassword) throws DracoonCryptoException, DracoonNetIOException,
            DracoonApiException {
        UserKeyPair.Version userKeyPairVersion = DracoonClientImpl.toUserKeyPairVersion(version);
        UserKeyPair userKeyPair = getUserKeyPair(userKeyPairVersion);
        return checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    private boolean checkUserKeyPairPassword(UserKeyPair userKeyPair, String encryptionPassword)
            throws DracoonCryptoException {
        try {
            return Crypto.checkUserKeyPair(userKeyPair, encryptionPassword);
        } catch (CryptoException e) {
            String errorText = String.format("Check of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
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

}
