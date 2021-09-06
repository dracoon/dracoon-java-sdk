package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings("unused")
public class ApiCreateFileUploadRequest {
    public Long parentId;
    public String name;
    public Long size;
    public Integer classification;
    public String notes;
    public ApiExpiration expiration;
    public Date timestampCreation;
    public Date timestampModification;
    public Boolean directS3Upload;
}
