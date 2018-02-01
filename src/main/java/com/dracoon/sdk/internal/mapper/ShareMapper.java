package com.dracoon.sdk.internal.mapper;

import java.util.Date;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiDownloadShare;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.util.DateUtils;
import com.dracoon.sdk.internal.util.TextUtils;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.DownloadShare;

public class ShareMapper {

    public static ApiCreateDownloadShareRequest toApiCreateDownloadShareRequest(
            CreateDownloadShareRequest request, UserKeyPair keyPair, EncryptedFileKey fileKey) {
        ApiCreateDownloadShareRequest apiRequest = new ApiCreateDownloadShareRequest();
        apiRequest.nodeId = request.getNodeId();
        apiRequest.name = request.getName();
        apiRequest.notes = request.getNotes();
        Date expirationDate = request.getExpirationDate();
        if (expirationDate != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = expirationDate.getTime() != 0L;
            apiExpiration.expireAt = expirationDate;
            apiRequest.expiration = apiExpiration;
        }
        apiRequest.showCreatorName = request.showCreatorName();
        apiRequest.showCreatorUsername = request.showCreatorUserName();
        apiRequest.notifyCreator = request.notifyCreator();
        apiRequest.maxDownloads = request.getMaxDownloads();
        apiRequest.password = request.getAccessPassword();
        if (keyPair != null) {
            apiRequest.keyPair = UserMapper.toApiUserKeyPair(keyPair);
        }
        if (fileKey != null) {
            apiRequest.fileKey = FileMapper.toApiFileKey(fileKey);
        }
        apiRequest.sendMail = request.sendEmail();
        apiRequest.mailRecipients = TextUtils.join(request.getEmailRecipients());
        apiRequest.mailSubject = request.getEmailSubject();
        apiRequest.mailBody = request.getEmailBody();
        apiRequest.sendSms = request.sendSms();
        apiRequest.smsRecipients = TextUtils.join(request.getSmsRecipients());
        return apiRequest;
    }

    public static DownloadShare fromApiDownloadShare(ApiDownloadShare apiDownloadShare) {
        DownloadShare downloadShare = new DownloadShare();
        downloadShare.setId(apiDownloadShare.id);
        downloadShare.setNodeId(apiDownloadShare.nodeId);
        downloadShare.setNodePath(apiDownloadShare.nodePath);
        downloadShare.setName(apiDownloadShare.name);
        if (apiDownloadShare.classification != null) {
            downloadShare.setClassification(Classification.getByValue(apiDownloadShare.classification));
        }
        downloadShare.setNotes(apiDownloadShare.notes);
        downloadShare.setExpireAt(DateUtils.parseDate(apiDownloadShare.expireAt));
        downloadShare.setAccessKey(apiDownloadShare.accessKey);
        downloadShare.setShowsCreatorName(apiDownloadShare.showCreatorName);
        downloadShare.setShowsCreatorUserName(apiDownloadShare.showCreatorUsername);
        downloadShare.setNotifiesCreator(apiDownloadShare.notifyCreator);
        downloadShare.setMaxDownloads(apiDownloadShare.maxDownloads);
        downloadShare.setCntDownloads(apiDownloadShare.cntDownloads);
        downloadShare.setCreatedAt(DateUtils.parseDate(apiDownloadShare.createdAt));
        downloadShare.setCreatedBy(UserMapper.fromApiUserInfo(apiDownloadShare.createdBy));
        downloadShare.setIsProtected(apiDownloadShare.isProtected);
        downloadShare.setIsEncrypted(apiDownloadShare.isEncrypted);
        return downloadShare;
    }

}
