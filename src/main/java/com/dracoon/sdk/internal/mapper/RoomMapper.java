package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.GroupMemberAcceptance;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomMapper extends BaseMapper {

    public static ApiCreateRoomRequest toApiCreateRoomRequest(CreateRoomRequest request) {
        ApiCreateRoomRequest apiRequest = new ApiCreateRoomRequest();
        apiRequest.parentId = request.getParentId();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        apiRequest.hasRecycleBin = request.hasRecycleBin();
        apiRequest.recycleBinRetentionPeriod = request.getRecycleBinRetentionPeriod();
        apiRequest.inheritPermissions = request.hasInheritPermissions();
        apiRequest.adminIds = request.getAdminUserIds();
        apiRequest.adminGroupIds = request.getAdminGroupIds();
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
