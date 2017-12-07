package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileKey;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.UpdateFileRequest;

import java.util.Date;

public class FileMapper {

    public static ApiUpdateFileRequest toApiUpdateFileRequest(UpdateFileRequest request) {
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

    public static ApiFileKey toApiFileKey(EncryptedFileKey encryptedFileKey) {
        ApiFileKey apiFileKey = new ApiFileKey();
        apiFileKey.key = encryptedFileKey.getKey();
        apiFileKey.iv = encryptedFileKey.getIv();
        apiFileKey.tag = encryptedFileKey.getTag();
        apiFileKey.version = encryptedFileKey.getVersion();
        return apiFileKey;
    }

    public static EncryptedFileKey fromApiFileKey(ApiFileKey apiEncryptedFileKey) {
        EncryptedFileKey encryptedFileKey = new EncryptedFileKey();
        encryptedFileKey.setKey(apiEncryptedFileKey.key);
        encryptedFileKey.setIv(apiEncryptedFileKey.iv);
        encryptedFileKey.setTag(apiEncryptedFileKey.tag);
        encryptedFileKey.setVersion(apiEncryptedFileKey.version);
        return encryptedFileKey;
    }

}
