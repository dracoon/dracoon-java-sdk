package com.dracoon.sdk.internal;

import java.util.Date;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import retrofit2.Call;
import retrofit2.Response;

class DracoonServerImpl extends DracoonRequestHandler implements DracoonClient.Server {

    private static final String LOG_TAG = DracoonServerImpl.class.getSimpleName();

    private DracoonServerSettingsImpl mServerSettings;
    private DracoonServerPoliciesImpl mServerPolicies;

    DracoonServerImpl(DracoonClientImpl client) {
        super(client);

        mServerSettings = new DracoonServerSettingsImpl(client);
        mServerPolicies = new DracoonServerPoliciesImpl(client);
    }

    public DracoonServerSettingsImpl getServerSettingsImpl() {
        return mServerSettings;
    }

    @Override
    public String getVersion() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerVersion> call = mService.getServerVersion();
        Response<ApiServerVersion> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseServerVersionError(response);
            String errorText = String.format("Query of server version failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().restApiVersion;
    }

    @Override
    public Date getTime() throws DracoonNetIOException, DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiServerTime> call = mService.getServerTime();
        Response<ApiServerTime> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server time failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().time;
    }

    @Override
    public DracoonClient.ServerSettings settings() {
        return mServerSettings;
    }

    @Override
    public DracoonClient.ServerPolicies policies() {
        return mServerPolicies;
    }

}
