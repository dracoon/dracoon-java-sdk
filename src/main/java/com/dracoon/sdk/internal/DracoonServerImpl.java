package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Date;

class DracoonServerImpl extends DracoonRequestHandler implements DracoonClient.Server {

    private static final String LOG_TAG = DracoonServerImpl.class.getSimpleName();

            DracoonServerImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public String getVersion() throws DracoonException {
        Call<ApiServerVersion> call = mService.getServerVersion();
        Response<ApiServerVersion> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server version failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerVersion data = response.body();

        return data.sdsServerVersion;
    }

    @Override
    public Date getTime() throws DracoonException {
        Call<ApiServerTime> call = mService.getServerTime();
        Response<ApiServerTime> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server time failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerTime data = response.body();

        return data.time;
    }

}
