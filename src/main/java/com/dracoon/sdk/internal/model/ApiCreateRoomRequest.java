package com.dracoon.sdk.internal.model;

public class ApiCreateRoomRequest {
    public Long parentId;
    public String name;
    public Long quota;
    public String notes;

    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;

    public Boolean inheritPermissions;
    public Long[] adminIds;
    public Long[] adminGroupIds;
    public String newGroupMemberAcceptance;
}
