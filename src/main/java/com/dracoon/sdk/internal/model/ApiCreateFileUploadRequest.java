package com.dracoon.sdk.internal.model;

@SuppressWarnings("unused")
public class ApiCreateFileUploadRequest {
    public Long parentId;
    public String name;
    public Long size;
    public Integer classification;
    public String notes;
    public ApiExpiration expiration;
    public Boolean directS3Upload;
}
