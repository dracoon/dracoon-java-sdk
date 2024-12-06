package com.dracoon.sdk.internal.api.model;

import java.util.Date;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiNodeVirusProtectionInfo {
    public long nodeId;
    public String verdict;
    public Date lastCheckedAt;
}
