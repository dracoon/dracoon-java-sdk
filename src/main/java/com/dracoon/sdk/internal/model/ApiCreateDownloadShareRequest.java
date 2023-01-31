package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiCreateDownloadShareRequest {
    public Long nodeId;
    public String name;
    public String notes;
    public String internalNotes;
    public ApiExpiration expiration;

    public Integer maxDownloads;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;

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
