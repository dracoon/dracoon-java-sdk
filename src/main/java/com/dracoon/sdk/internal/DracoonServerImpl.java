package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.model.ServerTime;
import com.dracoon.sdk.internal.model.ServerVersion;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Date;

class DracoonServerImpl implements DracoonClient.Server {

    private DracoonService mDracoonService;

    DracoonServerImpl(DracoonClientImpl client) {
        mDracoonService = client.getDracoonService();
    }

    @Override
    public String getVersion() throws DracoonException {
        Call<ServerVersion> call = mDracoonService.getServerVersion();
        Response<ServerVersion> response = DracoonServiceHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ServerVersion data = response.body();

        return data.sdsServerVersion;
    }

    @Override
    public Date getTime() throws DracoonException {
        Call<ServerTime> call = mDracoonService.getServerTime();
        Response<ServerTime> response = DracoonServiceHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            throw new DracoonApiException();
        }

        ServerTime data = response.body();

        return data.time;
    }

}
