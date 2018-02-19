package com.dracoon.sdk.internal.model;

public class ApiCreateUploadShareRequest {
    public Long targetId;
    public String name;
    public String notes;
    public ApiExpiration expiration;
    public Integer filesExpiryPeriod;

    public Boolean showUploadedFiles;
    public Boolean notifyCreator;

    public String password;

    public Boolean sendMail;
    public String mailRecipients;
    public String mailSubject;
    public String mailBody;

    public Boolean sendSms;
    public String smsRecipients;
}
