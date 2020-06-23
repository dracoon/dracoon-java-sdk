package com.dracoon.sdk.internal.mapper;

import java.util.Date;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiFileKey;
import com.dracoon.sdk.internal.model.ApiUpdateFileRequest;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.UpdateFileRequest;

public class FileMapper extends BaseMapper {

    public static ApiUpdateFileRequest toApiUpdateFileRequest(UpdateFileRequest request) {
        ApiUpdateFileRequest apiRequest = new ApiUpdateFileRequest();
        apiRequest.name = request.getName();
        Classification classification = request.getClassification();
        if (classification != null) {
            apiRequest.classification = classification.getValue();
        }
        apiRequest.notes = request.getNotes();
        Date expirationDate = request.getExpirationDate();
        if (expirationDate != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = expirationDate.getTime() != 0L;
            apiExpiration.expireAt = expirationDate;
            apiRequest.expiration = apiExpiration;
        }
        return apiRequest;
    }

    public static ApiFileKey toApiFileKey(EncryptedFileKey encryptedFileKey) {
        if (encryptedFileKey == null) {
            return null;
        }

        ApiFileKey apiFileKey = new ApiFileKey();
        apiFileKey.key = encryptedFileKey.getKey();
        apiFileKey.iv = encryptedFileKey.getIv();
        apiFileKey.tag = encryptedFileKey.getTag();
        apiFileKey.version = encryptedFileKey.getVersion();
        return apiFileKey;
    }

    public static EncryptedFileKey fromApiFileKey(ApiFileKey apiFileKey) {
        if (apiFileKey == null) {
            return null;
        }

        EncryptedFileKey encryptedFileKey = new EncryptedFileKey(apiFileKey.version, apiFileKey.key,
                apiFileKey.iv);
        encryptedFileKey.setTag(apiFileKey.tag);
        return encryptedFileKey;
    }

}
