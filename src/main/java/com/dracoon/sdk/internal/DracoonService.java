package com.dracoon.sdk.internal;

import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DracoonService {

    String API_PATH = "/api/v4";

    String AUTHORIZATION_HEADER = "X-Sds-Auth-Token";

    @GET(API_PATH + "/public/software/version")
    Call<ApiServerVersion> getServerVersion();

    @GET(API_PATH + "/public/time")
    Call<ApiServerTime> getServerTime();

    @GET(API_PATH + "/nodes")
    Call<ApiNodeList> getChildNodes(@Header(AUTHORIZATION_HEADER) String token,
                                    @Query("parent_id") Long id,
                                    @Query("depth_level") Integer depthLevel,
                                    @Query(value = "filter", encoded = true) String filter,
                                    @Query("offset") Integer offset,
                                    @Query("limit") Integer limit);

    @GET(API_PATH + "/nodes/{node_id}")
    Call<ApiNode> getNode(@Header(AUTHORIZATION_HEADER) String token,
                          @Path("node_id") Long id);

}
