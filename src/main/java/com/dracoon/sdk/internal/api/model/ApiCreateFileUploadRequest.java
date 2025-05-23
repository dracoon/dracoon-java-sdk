package com.dracoon.sdk.internal.api.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
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
