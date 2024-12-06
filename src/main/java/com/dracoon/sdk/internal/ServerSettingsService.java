package com.dracoon.sdk.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.api.mapper.ServerMapper;
import com.dracoon.sdk.internal.api.model.ApiServerCryptoAlgorithms;
import com.dracoon.sdk.internal.api.model.ApiServerDefaults;
import com.dracoon.sdk.internal.api.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.api.model.ApiUserKeyPairAlgorithm;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;
import com.dracoon.sdk.model.UserKeyPairAlgorithm;
import retrofit2.Call;
import retrofit2.Response;

@ClientImpl(DracoonClient.ServerSettings.class)
class ServerSettingsService extends BaseService {

    private static final String LOG_TAG = ServerSettingsService.class.getSimpleName();

    private static final UserKeyPair.Version FALLBACK_USER_KEY_PAIR_VERSION =
            UserKeyPair.Version.RSA4096;

    ServerSettingsService(DracoonClientImpl client) {
        super(client);
    }

    @ClientMethodImpl
    public ServerGeneralSettings getGeneralSettings() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerGeneralSettings> call = mApi.getServerGeneralSettings();
        Response<ApiServerGeneralSettings> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server general settings failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerGeneralSettings data = response.body();

        return ServerMapper.fromApiGeneralSettings(data);
    }

    @ClientMethodImpl
    public ServerDefaults getDefaults() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerDefaults> call = mApi.getServerDefaults();
        Response<ApiServerDefaults> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server defaults failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerDefaults data = response.body();

        return ServerMapper.fromApiServerDefaults(data);
    }

    @ClientMethodImpl
    public List<UserKeyPairAlgorithm> getAvailableUserKeyPairAlgorithms() throws DracoonNetIOException,
            DracoonApiException {
        List<ApiUserKeyPairAlgorithm> apiUserKeyPairAlgorithms = getUserKeyPairAlgorithms();

        List<UserKeyPairAlgorithm> algorithms = new ArrayList<>();
        for (ApiUserKeyPairAlgorithm apiUserKeyPairAlgorithm : apiUserKeyPairAlgorithms) {
            UserKeyPairAlgorithm.Version version = UserKeyPairAlgorithm.Version.getByValue(
                    apiUserKeyPairAlgorithm.version);
            UserKeyPairAlgorithm.State state = UserKeyPairAlgorithm.State.getByValue(
                    apiUserKeyPairAlgorithm.status);
            if (version != null && state != null) {
                UserKeyPairAlgorithm algorithm = new UserKeyPairAlgorithm();
                algorithm.setVersion(version);
                algorithm.setState(state);
                algorithms.add(algorithm);
            }
        }
        return algorithms;
    }

    public List<UserKeyPair.Version> getAvailableUserKeyPairVersions() throws DracoonNetIOException,
            DracoonApiException {
        List<ApiUserKeyPairAlgorithm> apiUserKeyPairAlgorithms = getUserKeyPairAlgorithms();

        List<UserKeyPair.Version> versions = new ArrayList<>();
        for (ApiUserKeyPairAlgorithm apiUserKeyPairAlgorithm : apiUserKeyPairAlgorithms) {
            try {
                versions.add(UserKeyPair.Version.getByValue(apiUserKeyPairAlgorithm.version));
            } catch (UnknownVersionException e) {
                // Not supported key pair versions are ignored
            }
        }
        return versions;
    }

    public UserKeyPair.Version getPreferredUserKeyPairVersion() throws DracoonNetIOException,
            DracoonApiException {
        List<UserKeyPair.Version> versions = getAvailableUserKeyPairVersions();
        return !versions.isEmpty() ? versions.get(0) : FALLBACK_USER_KEY_PAIR_VERSION;
    }

    private List<ApiUserKeyPairAlgorithm> getUserKeyPairAlgorithms()  throws DracoonNetIOException,
            DracoonApiException {
        ApiServerCryptoAlgorithms apiCryptoAlgorithms = getCryptoAlgorithms();

        if (apiCryptoAlgorithms == null || apiCryptoAlgorithms.keyPairAlgorithms == null) {
            return Collections.emptyList();
        }

        List<ApiUserKeyPairAlgorithm> apiUserKeyPairAlgorithms = apiCryptoAlgorithms.keyPairAlgorithms;
        sortUserKeyPairAlgorithms(apiUserKeyPairAlgorithms);
        return apiUserKeyPairAlgorithms;
    }

    private static void sortUserKeyPairAlgorithms(List<ApiUserKeyPairAlgorithm> userKeyPairAlgorithms) {
        String statusRequired = UserKeyPairAlgorithm.State.REQUIRED.getValue();
        userKeyPairAlgorithms.sort((alg1, alg2) -> {
            boolean isStatus1Required = alg1.status != null && alg1.status.equals(statusRequired);
            boolean isStatus2Required = alg2.status != null && alg2.status.equals(statusRequired);
            if (isStatus1Required && !isStatus2Required) {
                return -1;
            } else if (!isStatus1Required && isStatus2Required) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    private ApiServerCryptoAlgorithms getCryptoAlgorithms() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerCryptoAlgorithms> call = mApi.getServerCryptoAlgorithms();
        Response<ApiServerCryptoAlgorithms> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server crypto algorithms failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

}
