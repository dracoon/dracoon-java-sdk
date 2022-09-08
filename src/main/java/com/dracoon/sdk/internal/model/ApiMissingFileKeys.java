package com.dracoon.sdk.internal.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiMissingFileKeys {
    public ApiRange range;
    public List<ApiUserIdFileId> items;
    public List<ApiUserIdUserPublicKey> users;
    public List<ApiFileIdFileKey> files;
}
