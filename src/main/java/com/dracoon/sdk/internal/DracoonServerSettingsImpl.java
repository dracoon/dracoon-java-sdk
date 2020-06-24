package com.dracoon.sdk.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.ServerMapper;
import com.dracoon.sdk.internal.model.ApiServerDefaults;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.model.CryptoAlgorithm;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;
import retrofit2.Call;
import retrofit2.Response;

class DracoonServerSettingsImpl extends DracoonRequestHandler
        implements DracoonClient.ServerSettings {

    private static final String LOG_TAG = DracoonServerSettingsImpl.class.getSimpleName();

    DracoonServerSettingsImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public ServerGeneralSettings getGeneralSettings() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<ApiServerGeneralSettings> call = mService.getServerGeneralSettings(auth);
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

    @Override
    public ServerDefaults getDefaults() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        String auth = mClient.buildAuthString();
        Call<ApiServerDefaults> call = mService.getServerDefaults(auth);
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

    @Override
    public List<CryptoAlgorithm> getAvailableCryptoAlgorithms() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_NEW_CRYPTO_ALGOS)) {
            CryptoAlgorithm cryptoAlgorithm = new CryptoAlgorithm();
            cryptoAlgorithm.setVersion(DracoonClient.CryptoVersions.A);
            cryptoAlgorithm.setState(CryptoAlgorithm.State.REQUIRED);
            return Collections.singletonList(cryptoAlgorithm);
        }

        // TODO!!!: Replace when an API is available
        // TODO!!!: Ensure this list is sorted
        CryptoAlgorithm cryptoAlgorithm1 = new CryptoAlgorithm();
        cryptoAlgorithm1.setVersion(DracoonClient.CryptoVersions.RSA4096_AES256GCM);
        cryptoAlgorithm1.setState(CryptoAlgorithm.State.REQUIRED);
        CryptoAlgorithm cryptoAlgorithm2 = new CryptoAlgorithm();
        cryptoAlgorithm2.setVersion(DracoonClient.CryptoVersions.A);
        cryptoAlgorithm2.setState(CryptoAlgorithm.State.DISCOURAGED);
        List<CryptoAlgorithm> cryptoAlgorithms = Arrays.asList(cryptoAlgorithm1, cryptoAlgorithm2);

        return cryptoAlgorithms;
    }

}
