package com.dracoon.sdk.internal;

import java.util.List;

import com.dracoon.sdk.internal.model.ApiCompleteFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCompleteS3FileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCopyNodesRequest;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiCreateFileUploadRequest;
import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiCreateNodeCommentRequest;
import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiCreateUploadShareRequest;
import com.dracoon.sdk.internal.model.ApiGetNodesVirusProtectionInfoRequest;
import com.dracoon.sdk.internal.model.ApiNodeComment;
import com.dracoon.sdk.internal.model.ApiNodeCommentList;
import com.dracoon.sdk.internal.model.ApiNodeVirusProtectionInfo;
import com.dracoon.sdk.internal.model.ApiServerClassificationPolicies;
import com.dracoon.sdk.internal.model.ApiServerCryptoAlgorithms;
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
import com.dracoon.sdk.internal.model.ApiS3FileUploadStatus;
import com.dracoon.sdk.internal.model.ApiS3FileUploadUrlList;
import com.dracoon.sdk.internal.model.ApiGetS3FileUploadUrlsRequest;
import com.dracoon.sdk.internal.model.ApiServerDefaults;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.model.ApiServerPasswordPolicies;
import com.dracoon.sdk.internal.model.ApiServerInfo;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiSetFileKeysRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateNodeCommentRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomConfigRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUploadShare;
import com.dracoon.sdk.internal.model.ApiUploadShareList;
import com.dracoon.sdk.internal.model.ApiUserAccount;
import com.dracoon.sdk.internal.model.ApiUserAvatarInfo;
import com.dracoon.sdk.internal.model.ApiUserKeyPair;
import com.dracoon.sdk.internal.model.ApiUserProfileAttributes;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

public interface DracoonApi {

    @GET(API_PATH + "/public/software/version")
    Call<ApiServerInfo> getServerInfo();

    @GET(API_PATH + "/public/time")
    Call<ApiServerTime> getServerTime();

    @GET(API_PATH + "/config/info/general")
    Call<ApiServerGeneralSettings> getServerGeneralSettings();

    @GET(API_PATH + "/config/info/defaults")
    Call<ApiServerDefaults> getServerDefaults();

    @GET(API_PATH + "/config/info/policies/algorithms")
    Call<ApiServerCryptoAlgorithms> getServerCryptoAlgorithms();

    @GET(API_PATH + "/config/info/policies/passwords")
    Call<ApiServerPasswordPolicies> getServerPasswordPolicies();

    @GET(API_PATH + "/config/info/policies/classifications")
    Call<ApiServerClassificationPolicies> getServerClassificationPolicies();

    @GET(API_PATH + "/user/ping")
    Call<Void> pingUser();

    @GET(API_PATH + "/user/account")
    Call<ApiUserAccount> getUserAccount();

    @GET(API_PATH + "/user/account/customer")
    Call<ApiCustomerAccount> getCustomerAccount();

    @GET(API_PATH + "/user/account/keypairs")
    Call<List<ApiUserKeyPair>> getUserKeyPairs();

    @POST(API_PATH + "/user/account/keypair")
    Call<Void> setUserKeyPair(@Body ApiUserKeyPair request);

    @GET(API_PATH + "/user/account/keypair")
    Call<ApiUserKeyPair> getUserKeyPair(@Query("version") String version);

    @DELETE(API_PATH + "/user/account/keypair")
    Call<Void> deleteUserKeyPair(@Query("version") String version);

    @PUT(API_PATH + "/user/profileAttributes")
    Call<Void> setUserProfileAttributes(@Body ApiUserProfileAttributes request);

    @GET(API_PATH + "/user/profileAttributes")
    Call<ApiUserProfileAttributes> getUserProfileAttributes();

    @DELETE(API_PATH + "/user/profileAttributes/{key}")
    Call<Void> deleteUserProfileAttribute(@Path("key") String key);

    @GET(API_PATH + "/user/account/avatar")
    Call<ApiUserAvatarInfo> getUserAvatarInfo();

    @POST(API_PATH + "/user/account/avatar")
    Call<Void> setUserAvatar(@Body RequestBody requestBody);

    @DELETE(API_PATH + "/user/account/avatar")
    Call<Void> deleteUserAvatar();

    @GET(API_PATH + "/nodes")
    Call<ApiNodeList> getNodes(@Query("parent_id") Long parentNodeId,
            @Query("depth_level") Integer depthLevel,
            @Query("filter") String filter,
            @Query("sort") String sort,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @GET(API_PATH + "/nodes/{node_id}")
    Call<ApiNode> getNode(@Path("node_id") Long nodeId);

    @GET(API_PATH + "/nodes/search")
    Call<ApiNodeList> searchNodes(@Query("search_string") String searchString,
            @Query("parent_id") Long parentNodeId,
            @Query("depth_level") Integer depthLevel,
            @Query("filter") String filter,
            @Query("sort") String sort,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @POST(API_PATH + "/nodes/rooms")
    Call<ApiNode> createRoom(@Body ApiCreateRoomRequest request);

    @PUT(API_PATH + "/nodes/rooms/{room_id}")
    Call<ApiNode> updateRoom(@Path("room_id") Long roomId,
            @Body ApiUpdateRoomRequest request);

    @PUT(API_PATH + "/nodes/rooms/{room_id}/config")
    Call<ApiNode> updateRoomConfig(@Path("room_id") Long roomId,
            @Body ApiUpdateRoomConfigRequest request);

    @POST(API_PATH + "/nodes/folders")
    Call<ApiNode> createFolder(@Body ApiCreateFolderRequest request);

    @PUT(API_PATH + "/nodes/folders/{folder_id}")
    Call<ApiNode> updateFolder(@Path("folder_id") Long folderId,
            @Body ApiUpdateFolderRequest request);

    @PUT(API_PATH + "/nodes/files/{file_id}")
    Call<ApiNode> updateFile(@Path("file_id") Long fileId,
            @Body ApiUpdateFileRequest request);

    @HTTP(method = "DELETE", hasBody = true, path = API_PATH + "/nodes")
    Call<Void> deleteNodes(@Body ApiDeleteNodesRequest request);

    @DELETE(API_PATH + "/nodes/{node_id}")
    Call<Void> deleteNode(@Path("node_id") Long nodeId);

    @POST(API_PATH + "/nodes/{node_id}/copy_to")
    Call<ApiNode> copyNodes(@Path("node_id") Long nodeId,
            @Body ApiCopyNodesRequest request);

    @POST(API_PATH + "/nodes/{node_id}/move_to")
    Call<ApiNode> moveNodes(@Path("node_id") Long nodeId,
            @Body ApiMoveNodesRequest request);

    @POST(API_PATH + "/nodes/files/uploads")
    Call<ApiFileUpload> createFileUpload(@Body ApiCreateFileUploadRequest request);

    @Multipart
    @POST(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<Void> uploadFile(@Path("upload_id") String uploadId,
            @Header("Content-Range") String contentRange,
            @Part MultipartBody.Part file);

    @PUT(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<ApiNode> completeFileUpload(@Path("upload_id") String uploadId,
            @Body ApiCompleteFileUploadRequest request);

    @POST(API_PATH + "/nodes/files/uploads/{upload_id}/s3_urls")
    Call<ApiS3FileUploadUrlList> getS3FileUploadUrls(@Path("upload_id") String uploadId,
            @Body ApiGetS3FileUploadUrlsRequest request);

    @PUT(API_PATH + "/nodes/files/uploads/{upload_id}/s3")
    Call<Void> completeS3FileUpload(@Path("upload_id") String uploadId,
            @Body ApiCompleteS3FileUploadRequest request);

    @GET(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<ApiS3FileUploadStatus> getS3FileUploadStatus(@Path("upload_id") String uploadId);

    @POST(API_PATH + "/nodes/files/{file_id}/downloads")
    Call<ApiDownloadToken> getDownloadToken(@Path("file_id") Long fileId);

    @GET(API_PATH + "/nodes/files/{file_id}/user_file_key")
    Call<ApiFileKey> getFileKey(@Path("file_id") Long fileId);

    @GET(API_PATH + "/nodes/missingFileKeys")
    Call<ApiMissingFileKeys> getMissingFileKeys(@Query("file_id") Long fileId,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @POST(API_PATH + "/nodes/files/keys")
    Call<Void> setFileKeys(@Body ApiSetFileKeysRequest request);

    @POST(API_PATH + "/nodes/{node_id}/favorite")
    Call<Void> markFavorite(@Path("node_id") Long nodeId);

    @DELETE(API_PATH + "/nodes/{node_id}/favorite")
    Call<Void> unmarkFavorite(@Path("node_id") Long nodeId);

    @GET(API_PATH + "/nodes/{node_id}/comments")
    Call<ApiNodeCommentList> getNodeComments(@Path("node_id") Long nodeId,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @POST(API_PATH + "/nodes/{node_id}/comments")
    Call<ApiNodeComment> createNodeComment(@Path("node_id") Long nodeId,
            @Body ApiCreateNodeCommentRequest request);

    @PUT(API_PATH + "/nodes/comments/{comment_id}")
    Call<ApiNodeComment> updateNodeComment(@Path("comment_id") Long commentId,
            @Body ApiUpdateNodeCommentRequest request);

    @DELETE(API_PATH + "/nodes/comments/{comment_id}")
    Call<Void> deleteNodeComment(@Path("comment_id") Long commentId);

    @POST(API_PATH + "/nodes/files/generate_verdict_info")
    Call<List<ApiNodeVirusProtectionInfo>> getNodesVirusProtectionInfo(
            @Body ApiGetNodesVirusProtectionInfoRequest request);

    @DELETE(API_PATH + "/nodes/malicious_files/{malicious_file_id}")
    Call<Void> deleteMaliciousFile(@Path("malicious_file_id") Long maliciousFileId);

    @POST(API_PATH + "/shares/downloads")
    Call<ApiDownloadShare> createDownloadShare(@Body ApiCreateDownloadShareRequest request);

    @GET(API_PATH + "/shares/downloads")
    Call<ApiDownloadShareList> getDownloadShares(@Query("filter") String filter,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @GET(API_PATH + "/shares/downloads/{share_id}/qr")
    Call<ApiDownloadShare> getDownloadShareQR(@Path("share_id") Long shareId);

    @DELETE(API_PATH + "/shares/downloads/{share_id}")
    Call<Void> deleteDownloadShare(@Path("share_id") Long shareId);

    @POST(API_PATH + "/shares/uploads")
    Call<ApiUploadShare> createUploadShare(@Body ApiCreateUploadShareRequest request);

    @GET(API_PATH + "/shares/uploads")
    Call<ApiUploadShareList> getUploadShares(@Query("filter") String filter,
            @Query("offset") Long offset,
            @Query("limit") Long limit);

    @GET(API_PATH + "/shares/uploads/{share_id}/qr")
    Call<ApiUploadShare> getUploadShareQR(@Path("share_id") Long shareId);

    @DELETE(API_PATH + "/shares/uploads/{share_id}")
    Call<Void> deleteUploadShare(@Path("share_id") Long shareId);

}
