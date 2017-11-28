package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.GroupMemberAcceptance;
import com.dracoon.sdk.model.UpdateRoomRequest;

import java.util.List;

public class RoomMapper {

    public static ApiCreateRoomRequest toApi(CreateRoomRequest request) {
        ApiCreateRoomRequest apiRequest = new ApiCreateRoomRequest();
        apiRequest.parentId = request.getParentId();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        apiRequest.hasRecycleBin = request.hasRecycleBin();
        apiRequest.recycleBinRetentionPeriod = request.getRecycleBinRetentionPeriod();
        apiRequest.inheritPermissions = request.hasInheritPermissions();
        List<Long> adminIds = request.getAdminIds();
        apiRequest.adminIds = adminIds != null ? adminIds.toArray(new Long[0]) : null;
        List<Long> adminGroupIds = request.getAdminGroupIds();
        apiRequest.adminGroupIds = adminGroupIds != null ? adminGroupIds.toArray(new Long[0]) : null;
        GroupMemberAcceptance groupMemberAcceptance = request.getNewGroupMemberAcceptance();
        apiRequest.newGroupMemberAcceptance = groupMemberAcceptance != null ?
                groupMemberAcceptance.getValue() : null;
        return apiRequest;
    }

    public static ApiUpdateRoomRequest toApi(UpdateRoomRequest request) {
        ApiUpdateRoomRequest apiRequest = new ApiUpdateRoomRequest();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        return apiRequest;
    }

}
