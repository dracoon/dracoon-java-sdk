package com.dracoon.sdk.internal.api.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiNodeCommentList {
    public ApiRange range;
    public List<ApiNodeComment> items;
}
