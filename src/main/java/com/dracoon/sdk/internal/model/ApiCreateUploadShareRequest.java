package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiCreateUploadShareRequest {
    public Long targetId;
    public String name;
    public String notes;
    public String internalNotes;
    public ApiExpiration expiration;

    public Integer filesExpiryPeriod;
    public Integer maxSlots;
    public Long maxSize;
    public Boolean showUploadedFiles;

    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;

    public String password;

    public Boolean sendMail;
    public String mailRecipients;
    public String mailSubject;
    public String mailBody;

    public Boolean sendSms;
    public String smsRecipients;
}
