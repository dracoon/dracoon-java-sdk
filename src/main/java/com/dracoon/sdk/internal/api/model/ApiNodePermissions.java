package com.dracoon.sdk.internal.api.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiNodePermissions {
    public Boolean manage;
    public Boolean read;
    public Boolean create;
    public Boolean change;
    public Boolean delete;
    public Boolean manageDownloadShare;
    public Boolean manageUploadShare;
    public Boolean readRecycleBin;
    public Boolean restoreRecycleBin;
    public Boolean deleteRecycleBin;
}
