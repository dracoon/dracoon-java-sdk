package com.dracoon.sdk.model;

import java.util.List;

public class CreateRoomRequest {

    private Long mParentId;
    private String mName;
    private Long mQuota;
    private String mNotes;

    private Boolean mHasRecycleBin;
    private Integer mRecycleBinRetentionPeriod;

    private Boolean mHasInheritPermissions;
    private List<Long> mAdminIds;
    private List<Long> mAdminGroupIds;
    private GroupMemberAcceptance mNewGroupMemberAcceptance;

    private CreateRoomRequest() {

    }

    public Long getParentId() {
        return mParentId;
    }

    public String getName() {
        return mName;
    }

    public Long getQuota() {
        return mQuota;
    }

    public String getNotes() {
        return mNotes;
    }

    public Boolean hasRecycleBin() {
        return mHasRecycleBin;
    }

    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    public Boolean hasInheritPermissions() {
        return mHasInheritPermissions;
    }

    public List<Long> getAdminIds() {
        return mAdminIds;
    }

    public List<Long> getAdminGroupIds() {
        return mAdminGroupIds;
    }

    public GroupMemberAcceptance getNewGroupMemberAcceptance() {
        return mNewGroupMemberAcceptance;
    }

    public static class Builder {

        private CreateRoomRequest mRequest;

        public Builder(String name) {
            mRequest = new CreateRoomRequest();
            mRequest.mName = name;
        }

        public Builder parentId(Long parentId) {
            mRequest.mParentId = parentId;
            return this;
        }

        public Builder quota(Long quota) {
            mRequest.mQuota = quota;
            return this;
        }

        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        public Builder hasRecycleBin(Boolean hasRecycleBin) {
            mRequest.mHasRecycleBin = hasRecycleBin;
            return this;
        }

        public Builder recycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
            mRequest.mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
            return this;
        }

        public Builder hasInheritPermissions(Boolean hasInheritPermissions) {
            mRequest.mHasInheritPermissions = hasInheritPermissions;
            return this;
        }

        public Builder adminIds(List<Long> adminIds) {
            mRequest.mAdminIds = adminIds;
            return this;
        }

        public Builder adminGroupIds(List<Long> adminGroupIds) {
            mRequest.mAdminGroupIds = adminGroupIds;
            return this;
        }

        public Builder newGroupMemberAcceptance(GroupMemberAcceptance newGroupMemberAcceptance) {
            mRequest.mNewGroupMemberAcceptance = newGroupMemberAcceptance;
            return this;
        }

        public CreateRoomRequest build() {
            return mRequest;
        }

    }

}
