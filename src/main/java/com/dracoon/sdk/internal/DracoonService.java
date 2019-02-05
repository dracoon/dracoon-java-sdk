package com.dracoon.sdk.internal;

import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCopyNodesRequest;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiCreateUploadShareRequest;
import com.dracoon.sdk.internal.model.ApiCustomerAccount;
import com.dracoon.sdk.internal.model.ApiDeleteNodesRequest;
import com.dracoon.sdk.internal.model.ApiDownloadShare;
import com.dracoon.sdk.internal.model.ApiDownloadShareList;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.internal.model.ApiFileKey;
import com.dracoon.sdk.internal.model.ApiFileUpload;
import com.dracoon.sdk.internal.model.ApiMissingFileKeys;
import com.dracoon.sdk.internal.model.ApiMoveNodesRequest;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.model.ApiNodeList;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import com.dracoon.sdk.internal.model.ApiSetFileKeysRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUploadShare;
import com.dracoon.sdk.internal.model.ApiUploadShareList;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.dracoon.sdk.internal.DracoonConstants.API_PATH;
import static com.dracoon.sdk.internal.DracoonConstants.AUTHORIZATION_HEADER;

public interface DracoonService {

    @GET(API_PATH + "/public/software/version")
    Call<ApiServerVersion> getServerVersion();

    @GET(API_PATH + "/public/time")
    Call<ApiServerTime> getServerTime();

    @GET(API_PATH + "/config/info/general")
    Call<ApiServerGeneralSettings> getServerGeneralSettings(
            @Header(AUTHORIZATION_HEADER) String token);

    @GET(API_PATH + "/user/ping")
    Call<Void> pingUser(@Header(AUTHORIZATION_HEADER) String token);

    @GET(API_PATH + "/user/account")
    Call<ApiUserAccount> getUserAccount(@Header(AUTHORIZATION_HEADER) String token);

    @GET(API_PATH + "/user/account/customer")
    Call<ApiCustomerAccount> getCustomerAccount(@Header(AUTHORIZATION_HEADER) String token);

    @POST(API_PATH + "/user/account/keypair")
    Call<Void> setUserKeyPair(@Header(AUTHORIZATION_HEADER) String token,
            @Body ApiUserKeyPair request);

    @GET(API_PATH + "/user/account/keypair")
    Call<ApiUserKeyPair> getUserKeyPair(@Header(AUTHORIZATION_HEADER) String token);

    @DELETE(API_PATH + "/user/account/keypair")
    Call<Void> deleteUserKeyPair(@Header(AUTHORIZATION_HEADER) String token);

    @GET(API_PATH + "/nodes")
    Call<ApiNodeList> getNodes(@Header(AUTHORIZATION_HEADER) String token,
            @Query("parent_id") Long id,
            @Query("depth_level") Integer depthLevel,
            @Query(value = "filter", encoded = true) String filter,
            @Query(value = "sort", encoded = true) String sort,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @GET(API_PATH + "/nodes/{node_id}")
    Call<ApiNode> getNode(@Header(AUTHORIZATION_HEADER) String token,
            @Path("node_id") Long id);

    @GET(API_PATH + "/nodes/search")
    Call<ApiNodeList> searchNodes(@Header(AUTHORIZATION_HEADER) String token,
            @Query("search_string") String searchString,
            @Query("parent_id") Long id,
            @Query("depth_level") Integer depthLevel,
            @Query(value = "filter", encoded = true) String filter,
            @Query(value = "sort", encoded = true) String sort,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

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

    @PUT(API_PATH + "/nodes/files/{file_id}")
    Call<ApiNode> updateFile(@Header(AUTHORIZATION_HEADER) String token,
            @Path("file_id") Long fileId,
            @Body ApiUpdateFileRequest request);

    @HTTP(method = "DELETE", hasBody = true, path = API_PATH + "/nodes")
    Call<Void> deleteNodes(@Header(AUTHORIZATION_HEADER) String token,
            @Body ApiDeleteNodesRequest request);

    @POST(API_PATH + "/nodes/{node_id}/copy_to")
    Call<ApiNode> copyNodes(@Header(AUTHORIZATION_HEADER) String token,
            @Path("node_id") Long nodeId,
            @Body ApiCopyNodesRequest request);

    @POST(API_PATH + "/nodes/{node_id}/move_to")
    Call<ApiNode> moveNodes(@Header(AUTHORIZATION_HEADER) String token,
            @Path("node_id") Long nodeId,
            @Body ApiMoveNodesRequest request);

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

    @GET(API_PATH + "/nodes/files/{file_id}/user_file_key")
    Call<ApiFileKey> getFileKey(@Header(AUTHORIZATION_HEADER) String token,
            @Path("file_id") Long fileId);

    @GET(API_PATH + "/nodes/missingFileKeys")
    Call<ApiMissingFileKeys> getMissingFileKeys(@Header(AUTHORIZATION_HEADER) String token,
            @Query("file_id") Long fileId,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @POST(API_PATH + "/nodes/files/keys")
    Call<Void> setFileKeys(@Header(AUTHORIZATION_HEADER) String token,
            @Body ApiSetFileKeysRequest request);

    @POST(API_PATH + "/nodes/{node_id}/favorite")
    Call<Void> markFavorite(@Header(AUTHORIZATION_HEADER) String token,
            @Path("node_id") Long nodeId);

    @DELETE(API_PATH + "/nodes/{node_id}/favorite")
    Call<Void> unmarkFavorite(@Header(AUTHORIZATION_HEADER) String token,
            @Path("node_id") Long nodeId);

    @POST(API_PATH + "/shares/downloads")
    Call<ApiDownloadShare> createDownloadShare(@Header(AUTHORIZATION_HEADER) String token,
            @Body ApiCreateDownloadShareRequest request);

    @GET(API_PATH + "/shares/downloads")
    Call<ApiDownloadShareList> getDownloadShares(@Header(AUTHORIZATION_HEADER) String token,
            @Query(value = "filter", encoded = true) String filter,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @DELETE(API_PATH + "/shares/downloads/{share_id}")
    Call<Void> deleteDownloadShare(@Header(AUTHORIZATION_HEADER) String token,
            @Path("share_id") Long shareId);

    @POST(API_PATH + "/shares/uploads")
    Call<ApiUploadShare> createUploadShare(@Header(AUTHORIZATION_HEADER) String token,
            @Body ApiCreateUploadShareRequest request);

    @GET(API_PATH + "/shares/uploads")
    Call<ApiUploadShareList> getUploadShares(@Header(AUTHORIZATION_HEADER) String token,
            @Query(value = "filter", encoded = true) String filter,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @DELETE(API_PATH + "/shares/uploads/{share_id}")
    Call<Void> deleteUploadShare(@Header(AUTHORIZATION_HEADER) String token,
            @Path("share_id") Long shareId);

}
