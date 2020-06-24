package com.dracoon.sdk.internal;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoConstants;
import com.dracoon.sdk.crypto.error.CryptoException;
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

    @Override
    public List<String> getUserKeyPairCryptoVersions() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        List<UserKeyPair> userKeyPairs = getUserKeyPairs();

        ArrayList<String> cryptoVersions = new ArrayList<>();
        for (UserKeyPair userKeyPair : userKeyPairs) {
            switch (userKeyPair.getUserPrivateKey().getVersion()) {
                case CryptoConstants.KeyPairVersions.A:
                    cryptoVersions.add(DracoonClient.CryptoVersions.A);
                    break;
                case CryptoConstants.KeyPairVersions.RSA4096:
                    cryptoVersions.add(DracoonClient.CryptoVersions.RSA4096_AES256GCM);
                    break;
                default:
            }
        }
        return cryptoVersions;
    }

    @Override
    public void setUserKeyPair(String cryptoVersion) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();
        mClient.assertCryptoVersionSupported(cryptoVersion);

        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = generateUserKeyPair(cryptoVersion, encryptionPassword);

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

    public UserKeyPair generateUserKeyPair(String cryptoVersion, String password)
            throws DracoonCryptoException {
        String keyPairVersion = mClient.getUserKeyPairVersion(cryptoVersion);

        try {
            return Crypto.generateUserKeyPair(keyPairVersion, password);
        } catch (CryptoException e) {
            String errorText = String.format("Generation of user key pair failed! '%s'",
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    public UserKeyPair getAndCheckUserKeyPair(String cryptoVersion) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = getUserKeyPair(cryptoVersion);
        boolean isValid = checkUserKeyPairPassword(userKeyPair, encryptionPassword);
        if (!isValid) {
            throw new DracoonCryptoException(DracoonCryptoCode.INVALID_PASSWORD_ERROR);
        }

        return userKeyPair;
    }

    @Override
    public void deleteUserKeyPair(String cryptoVersion) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();
        mClient.assertCryptoVersionSupported(cryptoVersion);

        String keyPairVersion = mClient.getUserKeyPairVersion(cryptoVersion);

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteUserKeyPair(auth, keyPairVersion);
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
    public boolean checkUserKeyPairPassword(String cryptoVersion) throws DracoonNetIOException,
            DracoonApiException, DracoonCryptoException {
        String encryptionPassword = mClient.getEncryptionPasswordOrAbort();
        UserKeyPair userKeyPair = getUserKeyPair(cryptoVersion);
        return checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    @Override
    public boolean checkUserKeyPairPassword(String cryptoVersion, String encryptionPassword)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        UserKeyPair userKeyPair = getUserKeyPair(cryptoVersion);
        return checkUserKeyPairPassword(userKeyPair, encryptionPassword);
    }

    private List<UserKeyPair> getUserKeyPairs() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();

        ApiUserKeyPair[] data = new ApiUserKeyPair[]{};
        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS)) {
            Call<ApiUserKeyPair> call = mService.getUserKeyPair(auth, null);
            Response<ApiUserKeyPair> response = mHttpHelper.executeRequest(call);

            if (!response.isSuccessful() && response.code() != HttpStatus.NOT_FOUND.getNumber()) {
                DracoonApiCode errorCode = mErrorParser.parseUserKeyPairsQueryError(response);
                String errorText = String.format("Query of user key pairs failed with '%s'!",
                        errorCode.name());
                mLog.d(LOG_TAG, errorText);
                throw new DracoonApiException(errorCode);
            }

            if (response.isSuccessful()) {
                data = new ApiUserKeyPair[]{response.body()};
            }
        } else {
            Call<ApiUserKeyPair[]> call = mService.getUserKeyPairs(auth);
            Response<ApiUserKeyPair[]> response = mHttpHelper.executeRequest(call);

            if (!response.isSuccessful()) {
                DracoonApiCode errorCode = mErrorParser.parseUserKeyPairsQueryError(response);
                String errorText = String.format("Query of user key pairs failed with '%s'!",
                        errorCode.name());
                mLog.d(LOG_TAG, errorText);
                throw new DracoonApiException(errorCode);
            }

            data = response.body();
        }

        return UserMapper.fromApiUserKeyPairs(data);
    }

    private UserKeyPair getUserKeyPair(String cryptoVersion) throws DracoonCryptoException,
            DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();
        mClient.assertCryptoVersionSupported(cryptoVersion);

        String keyPairVersion = mClient.getUserKeyPairVersion(cryptoVersion);

        String auth = mClient.buildAuthString();
        Call<ApiUserKeyPair> call = mService.getUserKeyPair(auth, keyPairVersion);
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

}
