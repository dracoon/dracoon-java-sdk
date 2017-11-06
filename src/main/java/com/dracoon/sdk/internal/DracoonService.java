package com.dracoon.sdk.internal;

import com.dracoon.sdk.internal.model.ServerTime;
import com.dracoon.sdk.internal.model.ServerVersion;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DracoonService {

    String API_PATH = "/api/v4";

    @GET(API_PATH + "/public/software/version")
    Call<ServerVersion> getServerVersion();

    @GET(API_PATH + "/public/time")
    Call<ServerTime> getServerTime();

}
