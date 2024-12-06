package com.dracoon.sdk.internal.api.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiCreateRoomRequest {
    public Long parentId;
    public String name;
    public Long quota;
    public String notes;

    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;

    public Boolean inheritPermissions;
    public List<Long> adminIds;
    public List<Long> adminGroupIds;
    public String newGroupMemberAcceptance;

    public Integer classification;
}
