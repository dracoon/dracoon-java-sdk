package com.dracoon.sdk.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        ApiUserKeyPair[] apiUserKeyPairs;
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

    private ApiUserKeyPair[] getOneUserKeyPair() throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();

        Call<ApiUserKeyPair> call = mService.getUserKeyPair(auth, null);
        Response<ApiUserKeyPair> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful() && response.code() != HttpStatus.NOT_FOUND.getNumber()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairsQueryError(response);
            String errorText = String.format("Query of user key pairs failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }
        if (!response.isSuccessful()) {
            return new ApiUserKeyPair[]{};
        }

        return new ApiUserKeyPair[]{response.body()};
    }

    private ApiUserKeyPair[] getAllUserKeyPairs() throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();

        Call<ApiUserKeyPair[]> call = mService.getUserKeyPairs(auth);
        Response<ApiUserKeyPair[]> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseUserKeyPairsQueryError(response);
            String errorText = String.format("Query of user key pairs failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private UserKeyPair getUserKeyPair(UserKeyPair.Version userKeyPairVersion)
            throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mClient.assertApiVersionSupported();
        mClient.assertUserKeyPairVersionSupported(userKeyPairVersion);

        String auth = mClient.buildAuthString();
        Call<ApiUserKeyPair> call = mService.getUserKeyPair(auth, userKeyPairVersion.getValue());
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

        String auth = mClient.buildAuthString();
        Call<Void> call = mService.deleteUserKeyPair(auth, userKeyPairVersion.getValue());
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

}
