package com.dracoon.sdk.internal.api.mapper;

import java.util.Date;

import com.dracoon.sdk.crypto.CryptoUtils;
import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.error.UnknownVersionException;
import com.dracoon.sdk.internal.api.model.ApiExpiration;
import com.dracoon.sdk.internal.api.model.ApiFileKey;
import com.dracoon.sdk.internal.api.model.ApiUpdateFileRequest;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.UpdateFileRequest;

public class FileMapper extends BaseMapper {

    private FileMapper() {
        super();
    }

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
        apiFileKey.key = toBase64String(encryptedFileKey.getKey());
        apiFileKey.iv = toBase64String(encryptedFileKey.getIv());
        apiFileKey.tag = toBase64String(encryptedFileKey.getTag());
        apiFileKey.version = encryptedFileKey.getVersion().getValue();
        return apiFileKey;
    }

    public static EncryptedFileKey fromApiFileKey(ApiFileKey apiFileKey)
            throws UnknownVersionException {
        if (apiFileKey == null) {
            return null;
        }

        EncryptedFileKey.Version version = EncryptedFileKey.Version.getByValue(apiFileKey.version);

        EncryptedFileKey encryptedFileKey = new EncryptedFileKey(version,
                fromBase64String(apiFileKey.key), fromBase64String(apiFileKey.iv));
        encryptedFileKey.setTag(fromBase64String(apiFileKey.tag));
        return encryptedFileKey;
    }

    private static String toBase64String(byte[] data) {
        return data != null ? CryptoUtils.byteArrayToBase64String(data) : null;
    }

    private static byte[] fromBase64String(String data) {
        return data != null ? CryptoUtils.base64StringToByteArray(data) : null;
    }

}
