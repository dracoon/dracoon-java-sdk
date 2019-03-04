package com.dracoon.sdk.internal.mapper;

import java.util.ArrayList;
import java.util.Date;

import com.dracoon.sdk.crypto.model.EncryptedFileKey;
import com.dracoon.sdk.crypto.model.UserKeyPair;
import com.dracoon.sdk.internal.model.ApiCreateDownloadShareRequest;
import com.dracoon.sdk.internal.model.ApiCreateUploadShareRequest;
import com.dracoon.sdk.internal.model.ApiDownloadShare;
import com.dracoon.sdk.internal.model.ApiDownloadShareList;
import com.dracoon.sdk.internal.model.ApiExpiration;
import com.dracoon.sdk.internal.model.ApiUploadShare;
import com.dracoon.sdk.internal.model.ApiUploadShareList;
import com.dracoon.sdk.internal.util.TextUtils;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.DownloadShareList;
import com.dracoon.sdk.model.UploadShare;
import com.dracoon.sdk.model.UploadShareList;

public class ShareMapper extends BaseMapper {

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
        if (apiDownloadShare == null) {
            return null;
        }

        DownloadShare downloadShare = new DownloadShare();
        downloadShare.setId(apiDownloadShare.id);
        downloadShare.setNodeId(apiDownloadShare.nodeId);
        downloadShare.setNodePath(apiDownloadShare.nodePath);
        downloadShare.setName(apiDownloadShare.name);
        if (apiDownloadShare.classification != null) {
            downloadShare.setClassification(Classification.getByValue(
                    apiDownloadShare.classification));
        }
        downloadShare.setNotes(apiDownloadShare.notes);
        downloadShare.setExpireAt(apiDownloadShare.expireAt);
        downloadShare.setAccessKey(apiDownloadShare.accessKey);
        downloadShare.setShowsCreatorName(toBoolean(apiDownloadShare.showCreatorName));
        downloadShare.setShowsCreatorUserName(toBoolean(apiDownloadShare.showCreatorUsername));
        downloadShare.setNotifiesCreator(toBoolean(apiDownloadShare.notifyCreator));
        downloadShare.setMaxDownloads(apiDownloadShare.maxDownloads);
        downloadShare.setCntDownloads(apiDownloadShare.cntDownloads);
        downloadShare.setCreatedAt(apiDownloadShare.createdAt);
        downloadShare.setCreatedBy(UserMapper.fromApiUserInfo(apiDownloadShare.createdBy));
        downloadShare.setIsProtected(toBoolean(apiDownloadShare.isProtected));
        downloadShare.setIsEncrypted(toBoolean(apiDownloadShare.isEncrypted));
        return downloadShare;
    }

    public static DownloadShareList fromApiDownloadShareList(ApiDownloadShareList apiDownloadShareList) {
        if (apiDownloadShareList == null) {
            return null;
        }

        DownloadShareList shareList = new DownloadShareList();
        shareList.setOffset(apiDownloadShareList.range.offset);
        shareList.setLimit(apiDownloadShareList.range.limit);
        shareList.setTotal(apiDownloadShareList.range.total);
        ArrayList<DownloadShare> items = new ArrayList<>();
        for (ApiDownloadShare apiDownloadShare : apiDownloadShareList.items) {
            items.add(ShareMapper.fromApiDownloadShare(apiDownloadShare));
        }
        shareList.setItems(items);
        return shareList;
    }

    public static ApiCreateUploadShareRequest toApiCreateUploadShareRequest(
            CreateUploadShareRequest request) {
        ApiCreateUploadShareRequest apiRequest = new ApiCreateUploadShareRequest();
        apiRequest.targetId = request.getTargetNodeId();
        apiRequest.name = request.getName();
        apiRequest.notes = request.getNotes();
        Date expirationDate = request.getExpirationDate();
        if (expirationDate != null) {
            ApiExpiration apiExpiration = new ApiExpiration();
            apiExpiration.enableExpiration = expirationDate.getTime() != 0L;
            apiExpiration.expireAt = expirationDate;
            apiRequest.expiration = apiExpiration;
        }
        apiRequest.maxSlots = request.getMaxUploads();
        apiRequest.maxSize = request.getMaxQuota();
        apiRequest.filesExpiryPeriod = request.getFilesExpirationPeriod();
        apiRequest.showUploadedFiles = request.showUploadedFiles();
        apiRequest.notifyCreator = request.notifyCreator();
        apiRequest.password = request.getAccessPassword();
        apiRequest.sendMail = request.sendEmail();
        apiRequest.mailRecipients = TextUtils.join(request.getEmailRecipients());
        apiRequest.mailSubject = request.getEmailSubject();
        apiRequest.mailBody = request.getEmailBody();
        apiRequest.sendSms = request.sendSms();
        apiRequest.smsRecipients = TextUtils.join(request.getSmsRecipients());
        return apiRequest;
    }

    public static UploadShare fromApiUploadShare(ApiUploadShare apiUploadShare) {
        if (apiUploadShare == null) {
            return null;
        }

        UploadShare uploadShare = new UploadShare();
        uploadShare.setId(apiUploadShare.id);
        uploadShare.setTargetNodeId(apiUploadShare.targetId);
        uploadShare.setTargetNodePath(apiUploadShare.targetPath);
        uploadShare.setName(apiUploadShare.name);
        uploadShare.setNotes(apiUploadShare.notes);
        uploadShare.setExpireAt(apiUploadShare.expireAt);
        uploadShare.setFilesExpirePeriod(apiUploadShare.filesExpiryPeriod);
        uploadShare.setAccessKey(apiUploadShare.accessKey);
        uploadShare.setShowsUploadedFiles(toBoolean(apiUploadShare.showUploadedFiles));
        uploadShare.setNotifiesCreator(toBoolean(apiUploadShare.notifyCreator));
        uploadShare.setMaxUploads(apiUploadShare.maxSlots);
        uploadShare.setMaxQuota(apiUploadShare.maxSize);
        uploadShare.setCntUploads(apiUploadShare.cntUploads);
        uploadShare.setCntFiles(apiUploadShare.cntFiles);
        uploadShare.setCreatedAt(apiUploadShare.createdAt);
        uploadShare.setCreatedBy(UserMapper.fromApiUserInfo(apiUploadShare.createdBy));
        uploadShare.setIsProtected(toBoolean(apiUploadShare.isProtected));
        uploadShare.setIsEncrypted(toBoolean(apiUploadShare.isEncrypted));
        return uploadShare;
    }

    public static UploadShareList fromApiUploadShareList(ApiUploadShareList apiUploadShareList) {
        if (apiUploadShareList == null) {
            return null;
        }

        UploadShareList shareList = new UploadShareList();
        shareList.setOffset(apiUploadShareList.range.offset);
        shareList.setLimit(apiUploadShareList.range.limit);
        shareList.setTotal(apiUploadShareList.range.total);
        ArrayList<UploadShare> items = new ArrayList<>();
        for (ApiUploadShare apiUploadShare : apiUploadShareList.items) {
            items.add(ShareMapper.fromApiUploadShare(apiUploadShare));
        }
        shareList.setItems(items);
        return shareList;
    }

}
