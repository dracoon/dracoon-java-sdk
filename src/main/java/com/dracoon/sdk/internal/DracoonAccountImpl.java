package com.dracoon.sdk.internal;

import java.util.Objects;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoException;
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
import retrofit2.Call;
import retrofit2.Response;

public class DracoonAccountImpl extends DracoonRequestHandler implements DracoonClient.Account {

    private static final String LOG_TAG = DracoonAccountImpl.class.getSimpleName();

    DracoonAccountImpl(DracoonClientImpl client) {
        super(client);
    }

    public void pingUser() throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();
        Call<Void> call = mService.pingUser(auth);
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

        String auth = mClient.buildAuthString();
        Call<ApiUserAccount> call = mService.getUserAccount(auth);
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

        String auth = mClient.buildAuthString();
        Call<ApiCustomerAccount> call = mService.getCustomerAccount(auth);
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

    public UserKeyPair generateUserKeyPair(String password) throws DracoonCryptoException {
        try {
            return Crypto.generateUserKeyPair(password);
        } catch (CryptoException e) {
            String errorText = String.format("Generation of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    @Override
    public void setUserKeyPair() throws DracoonCryptoException, DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        String encryptionPassword = mClient.getEncryptionPassword();
        UserKeyPair userKeyPair = generateUserKeyPair(encryptionPassword);

        ApiUserKeyPair apiUserKeyPair = UserMapper.toApiUserKeyPair(userKeyPair);

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.setUserKeyPair(auth, apiUserKeyPair);
        Response<Void> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairSetError(response);
            String errorText = String.format("Setting user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
    }

    public UserKeyPair getAndCheckUserKeyPair() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        UserKeyPair userKeyPair = getUserKeyPair();
        String encryptionPassword = mClient.getEncryptionPassword();

        boolean isValid = checkUserKeyPairPassword(userKeyPair, encryptionPassword);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }

        return userKeyPair;
    }

    private UserKeyPair getUserKeyPair() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<ApiUserKeyPair> call = mService.getUserKeyPair(auth);
        Response<ApiUserKeyPair> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairQueryError(response);
            String errorText = String.format("Query of user key pair failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiUserKeyPair data = response.body();

        return UserMapper.fromApiUserKeyPair(data);
    }

    @Override
    public void deleteUserKeyPair() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteUserKeyPair(auth);
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
    public boolean checkUserKeyPairPassword() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException {
        UserKeyPair userKeyPair = getUserKeyPair();
        String encryptionPassword = mClient.getEncryptionPassword();
        return checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    @Override
    public boolean checkUserKeyPairPassword(String encryptionPassword) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        UserKeyPair userKeyPair = getUserKeyPair();
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
            profileAttributes.items = new ApiUserProfileAttributes.Item[]{profileAttribute};

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

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.setUserProfileAttributes(auth, profileAttributes);
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

        String auth = mClient.buildAuthString();
        Call<ApiUserProfileAttributes> call = mService.getUserProfileAttributes(auth);
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

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteUserProfileAttribute(auth, key);
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
