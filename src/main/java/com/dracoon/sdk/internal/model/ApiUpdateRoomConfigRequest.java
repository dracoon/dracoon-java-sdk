package com.dracoon.sdk.internal.model;

import java.util.List;

@SuppressWarnings("unused")
public class ApiUpdateRoomConfigRequest {
    public Integer recycleBinRetentionPeriod;
    public Boolean inheritPermissions;
    public List<Long> adminIds;
    public List<Long> adminGroupIds;
    public String newGroupMemberAcceptance;
    public Integer classification;
}
