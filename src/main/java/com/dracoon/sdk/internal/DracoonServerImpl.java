package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Date;

class DracoonServerImpl implements DracoonClient.Server {

    private DracoonService mService;

    DracoonServerImpl(DracoonClientImpl client) {
        mService = client.getDracoonService();
    }

    @Override
    public String getVersion() throws DracoonException {
        Call<ApiServerVersion> call = mService.getServerVersion();
        Response<ApiServerVersion> response = DracoonHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ApiServerVersion data = response.body();

        return data.sdsServerVersion;
    }

    @Override
    public Date getTime() throws DracoonException {
        Call<ApiServerTime> call = mService.getServerTime();
        Response<ApiServerTime> response = DracoonHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ApiServerTime data = response.body();

        return data.serverTime;
    }

}
