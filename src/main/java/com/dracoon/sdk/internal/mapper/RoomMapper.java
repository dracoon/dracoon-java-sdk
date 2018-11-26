package com.dracoon.sdk.internal.mapper;

import java.util.List;

import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.GroupMemberAcceptance;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomMapper {

    public static ApiCreateRoomRequest toApiCreateRoomRequest(CreateRoomRequest request) {
        ApiCreateRoomRequest apiRequest = new ApiCreateRoomRequest();
        apiRequest.parentId = request.getParentId();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        apiRequest.hasRecycleBin = request.hasRecycleBin();
        apiRequest.recycleBinRetentionPeriod = request.getRecycleBinRetentionPeriod();
        apiRequest.inheritPermissions = request.hasInheritPermissions();
        List<Long> adminUserIds = request.getAdminUserIds();
        apiRequest.adminIds = adminUserIds != null ? adminUserIds.toArray(new Long[0]) : null;
        List<Long> adminGroupIds = request.getAdminGroupIds();
        apiRequest.adminGroupIds = adminGroupIds != null ? adminGroupIds.toArray(new Long[0]) : null;
        GroupMemberAcceptance groupMemberAcceptance = request.getNewGroupMemberAcceptance();
        apiRequest.newGroupMemberAcceptance = groupMemberAcceptance != null ?
                groupMemberAcceptance.getValue() : null;
        return apiRequest;
    }

    public static ApiUpdateRoomRequest toApiUpdateRoomRequest(UpdateRoomRequest request) {
        ApiUpdateRoomRequest apiRequest = new ApiUpdateRoomRequest();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        return apiRequest;
    }

}
