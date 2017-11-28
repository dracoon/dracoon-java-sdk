package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiCreateFolderRequest;
import com.dracoon.sdk.internal.model.ApiUpdateFolderRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;

public class FolderMapper {

    public static ApiCreateFolderRequest toApiCreateFolderRequest(CreateFolderRequest request) {
        ApiCreateFolderRequest apiRequest = new ApiCreateFolderRequest();
        apiRequest.parentId = request.getParentId();
        apiRequest.name = request.getName();
        apiRequest.notes = request.getNotes();
        return apiRequest;
    }

    public static ApiUpdateFolderRequest toApiUpdateFolderRequest(UpdateFolderRequest request) {
        ApiUpdateFolderRequest apiRequest = new ApiUpdateFolderRequest();
        apiRequest.name = request.getName();
        apiRequest.notes = request.getNotes();
        return apiRequest;
    }

}
