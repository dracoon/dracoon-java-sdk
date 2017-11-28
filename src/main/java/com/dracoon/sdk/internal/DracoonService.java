package com.dracoon.sdk.internal;

import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST(API_PATH + "/nodes/rooms")
    Call<ApiNode> createRoom(@Header(AUTHORIZATION_HEADER) String token,
                             @Body ApiCreateRoomRequest request);

    @PUT(API_PATH + "/nodes/rooms/{room_id}")
    Call<ApiNode> updateRoom(@Header(AUTHORIZATION_HEADER) String token,
                             @Path("room_id") Long roomId,
                             @Body ApiUpdateRoomRequest request);

    @POST(API_PATH + "/nodes/folders")
    Call<ApiNode> createFolder(@Header(AUTHORIZATION_HEADER) String token,
                               @Body ApiCreateFolderRequest request);

    @PUT(API_PATH + "/nodes/folders/{folder_id}")
    Call<ApiNode> updateFolder(@Header(AUTHORIZATION_HEADER) String token,
                               @Path("folder_id") Long folderId,
                               @Body ApiUpdateFolderRequest request);

    @POST(API_PATH + "/nodes/files/uploads")
    Call<ApiFileUpload> createFileUpload(@Header(AUTHORIZATION_HEADER) String token,
                                         @Body ApiCreateFileUploadRequest request);

    @Multipart
    @POST(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<Void> uploadFile(@Header(AUTHORIZATION_HEADER) String token,
                          @Path("upload_id") String uploadId,
                          @Header("Content-Range") String contentRange,
                          @Part MultipartBody.Part file);

    @PUT(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<ApiNode> completeFileUpload(@Header(AUTHORIZATION_HEADER) String token,
                                     @Path("upload_id") String uploadId,
                                     @Body ApiCompleteFileUploadRequest request);

    @POST(API_PATH + "/nodes/files/{file_id}/downloads")
    Call<ApiDownloadToken> getDownloadToken(@Header(AUTHORIZATION_HEADER) String token,
                                            @Path("file_id") Long fileId);

}
