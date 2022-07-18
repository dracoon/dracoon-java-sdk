package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiNodeComment {
    public Long id;
    public String text;

    public Date createdAt;
    public ApiUserInfo createdBy;
    public Date updatedAt;
    public ApiUserInfo updatedBy;

    public Boolean isChanged;
    public Boolean isDeleted;
}
