package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.util.Date;

public class FileMapper {

    public static ApiUpdateFileRequest toApi(UpdateFileRequest request) {
        ApiUpdateFileRequest apiRequest = new ApiUpdateFileRequest();
        apiRequest.name = request.getName();
        Classification classification = request.getClassification();
        if (classification != null) {
            apiRequest.classification = classification.getValue();
        }
        apiRequest.notes = request.getNotes();
        Date expiration = request.getExpiration();
        if (expiration != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = expiration.getTime() != 0L;
            apiExpiration.expireAt = expiration;
            apiRequest.expiration = apiExpiration;
        }
        return apiRequest;
    }

}
