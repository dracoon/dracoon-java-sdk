package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiGetS3FileUploadUrlsRequest {
    public Long size;
    public Integer firstPartNumber;
    public Integer lastPartNumber;
}
