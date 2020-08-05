package com.dracoon.sdk.internal.model;

import java.util.List;

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
}
