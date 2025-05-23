package com.dracoon.sdk.internal.api.mapper;

import com.dracoon.sdk.internal.api.model.ApiCreateRoomRequest;
import com.dracoon.sdk.internal.api.model.ApiUpdateRoomConfigRequest;
import com.dracoon.sdk.internal.api.model.ApiUpdateRoomRequest;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.GroupMemberAcceptance;
import com.dracoon.sdk.model.UpdateRoomConfigRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;

public class RoomMapper extends BaseMapper {

    private RoomMapper() {
        super();
    }

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
        Classification classification = request.getClassification();
        if (classification != null) {
            apiRequest.classification = classification.getValue();
        }
        return apiRequest;
    }

    public static ApiUpdateRoomRequest toApiUpdateRoomRequest(UpdateRoomRequest request) {
        ApiUpdateRoomRequest apiRequest = new ApiUpdateRoomRequest();
        apiRequest.name = request.getName();
        apiRequest.quota = request.getQuota();
        apiRequest.notes = request.getNotes();
        return apiRequest;
    }

    public static ApiUpdateRoomConfigRequest toApiUpdateRoomConfigRequest(
            UpdateRoomConfigRequest request) {
        ApiUpdateRoomConfigRequest apiRequest = new ApiUpdateRoomConfigRequest();
        apiRequest.recycleBinRetentionPeriod = request.getRecycleBinRetentionPeriod();
        apiRequest.inheritPermissions = request.hasInheritPermissions();
        apiRequest.adminIds = request.getAdminUserIds();
        apiRequest.adminGroupIds = request.getAdminGroupIds();
        GroupMemberAcceptance groupMemberAcceptance = request.getNewGroupMemberAcceptance();
        apiRequest.newGroupMemberAcceptance = groupMemberAcceptance != null ?
                groupMemberAcceptance.getValue() : null;
        Classification classification = request.getClassification();
        if (classification != null) {
            apiRequest.classification = classification.getValue();
        }
        return apiRequest;
    }

}
