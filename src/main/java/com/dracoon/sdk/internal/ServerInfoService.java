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

@ClientImpl(DracoonClient.Server.class)
class ServerInfoService extends BaseService {

    private static final String LOG_TAG = ServerInfoService.class.getSimpleName();

    ServerInfoService(DracoonClientImpl client) {
        super(client);
    }

    @ClientMethodImpl
    public String getVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().restApiVersion;
    }

    @ClientMethodImpl
    public Boolean isDracoonCloud() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().isDracoonCloud;
    }

    private ApiServerInfo getServerInfo() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerInfo> call = mApi.getServerInfo();
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

    @ClientMethodImpl
    public Date getTime() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerTime> call = mApi.getServerTime();
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

}
