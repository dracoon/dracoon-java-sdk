package com.dracoon.sdk.internal.model;

public class ApiCreateDownloadShareRequest {
    public Long nodeId;
    public String name;
    public String notes;
    public ApiExpiration expiration;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;
    public Integer maxDownloads;

    public String password;
    public ApiUserKeyPair keyPair;
    public ApiFileKey fileKey;

    public Boolean sendMail;
    public String mailRecipients;
    public String mailSubject;
    public String mailBody;

    public Boolean sendSms;
    public String smsRecipients;
}
