package com.dracoon.sdk.internal.api.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiCreateFolderRequest {
    public Long parentId;
    public String name;
    public Integer classification;
    public String notes;
}
