package com.dracoon.sdk.internal;

import java.util.Date;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiServerInfo;
import com.dracoon.sdk.internal.model.ApiServerTime;
import retrofit2.Call;
import retrofit2.Response;

class DracoonServerImpl extends DracoonRequestHandler implements DracoonClient.Server {

    private static final String LOG_TAG = DracoonServerImpl.class.getSimpleName();

    DracoonServerImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public String getVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().restApiVersion;
    }

    @Override
    public Boolean isDracoonCloud() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().isDracoonCloud;
    }

    private ApiServerInfo getServerInfo() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerInfo> call = mService.getServerInfo();
        Response<ApiServerInfo> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseServerInfoQueryError(response);
            String errorText = String.format("Query of server info failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
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
        return mClient.getServerSettingsImpl();
    }

    @Override
    public DracoonClient.ServerPolicies policies() {
        return mClient.getServerPoliciesImpl();
    }

}
