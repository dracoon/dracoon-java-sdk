package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiNodePermissions;
import com.dracoon.sdk.model.NodePermissions;

public class NodePermissionsMapper extends BaseMapper {

    public static NodePermissions fromApiNodePermissions(ApiNodePermissions apiNodePermissions) {
        if (apiNodePermissions == null) {
            return null;
        }

        NodePermissions nodePermissions = new NodePermissions();
        nodePermissions.setManage(toBoolean(apiNodePermissions.manage));
        nodePermissions.setRead(toBoolean(apiNodePermissions.read));
        nodePermissions.setCreate(toBoolean(apiNodePermissions.create));
        nodePermissions.setChange(toBoolean(apiNodePermissions.change));
        nodePermissions.setDelete(toBoolean(apiNodePermissions.delete));
        nodePermissions.setManageDownloadShare(toBoolean(apiNodePermissions.manageDownloadShare));
        nodePermissions.setManageUploadShare(toBoolean(apiNodePermissions.manageUploadShare));
        nodePermissions.setReadRecycleBin(toBoolean(apiNodePermissions.readRecycleBin));
        nodePermissions.setRestoreRecycleBin(toBoolean(apiNodePermissions.restoreRecycleBin));
        nodePermissions.setDeleteRecycleBin(toBoolean(apiNodePermissions.deleteRecycleBin));
        return nodePermissions;
    }

}
