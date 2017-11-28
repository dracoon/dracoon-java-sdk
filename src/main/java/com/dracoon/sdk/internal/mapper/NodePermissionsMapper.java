package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiNodePermissions;
import com.dracoon.sdk.model.NodePermissions;

public class NodePermissionsMapper {

    public static NodePermissions fromApiNodePermissions(ApiNodePermissions apiNodePermissions) {
        if (apiNodePermissions == null) {
            return null;
        }

        NodePermissions nodePermissions = new NodePermissions();
        nodePermissions.setManage(apiNodePermissions.manage);
        nodePermissions.setRead(apiNodePermissions.read);
        nodePermissions.setCreate(apiNodePermissions.create);
        nodePermissions.setChange(apiNodePermissions.change);
        nodePermissions.setDelete(apiNodePermissions.delete);
        nodePermissions.setManageDownloadShare(apiNodePermissions.manageDownloadShare);
        nodePermissions.setManageUploadShare(apiNodePermissions.manageUploadShare);
        nodePermissions.setReadRecycleBin(apiNodePermissions.readRecycleBin);
        nodePermissions.setRestoreRecycleBin(apiNodePermissions.restoreRecycleBin);
        nodePermissions.setDeleteRecycleBin(apiNodePermissions.deleteRecycleBin);
        return nodePermissions;
    }

}
