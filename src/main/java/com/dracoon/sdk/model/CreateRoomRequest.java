package com.dracoon.sdk.model;

import java.util.List;

/**
 * Request to create a new room.<br>
 * <br>
 * A new instance can be created with {@link CreateRoomRequest.Builder Builder}.
 */
@SuppressWarnings("unused")
public class CreateRoomRequest {

    private Long mParentId;
    private String mName;
    private Long mQuota;
    private String mNotes;

    private Boolean mHasRecycleBin;
    private Integer mRecycleBinRetentionPeriod;

    private Boolean mHasInheritPermissions;
    private List<Long> mAdminUserIds;
    private List<Long> mAdminGroupIds;
    private GroupMemberAcceptance mNewGroupMemberAcceptance;

    private CreateRoomRequest() {

    }

    /**
     * Returns the parent node ID of the new room.
     *
     * @return the parent node ID
     */
    public Long getParentId() {
        return mParentId;
    }

    /**
     * Returns the name of the new room.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the quota of the new room.
     *
     * @return the quota
     */
    public Long getQuota() {
        return mQuota;
    }

    /**
     * Returns the notes of the new room.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Returns <code>true</code> if recycle bin is enabled on the new room.
     *
     * @return <code>true</code> if recycle bin is enabled; <code>false</code> otherwise
     */
    public Boolean hasRecycleBin() {
        return mHasRecycleBin;
    }

    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    /**
     * Returns <code>true</code> if permission inheritance is enabled on the new room.
     *
     * @return <code>true</code> if permission inheritance is enabled; <code>false</code> otherwise
     */
    public Boolean hasInheritPermissions() {
        return mHasInheritPermissions;
    }

    /**
     * Returns IDs of administrator users of the new room.
     *
     * @return IDs of administrator users
     */
    public List<Long> getAdminUserIds() {
        return mAdminUserIds;
    }

    /**
     * Returns IDs of administrator groups of the new room.
     *
     * @return IDs of administrator groups
     */
    public List<Long> getAdminGroupIds() {
        return mAdminGroupIds;
    }

    /**
     * Returns the group member acceptance type of the new room.
     *
     * @return the group member acceptance type
     */
    public GroupMemberAcceptance getNewGroupMemberAcceptance() {
        return mNewGroupMemberAcceptance;
    }

    /**
     * This builder creates new instances of {@link CreateRoomRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Name (mandatory):             {@link Builder#Builder(String)}<br>
     * - Parent node ID:               {@link Builder#parentId(Long)}<br>
     * - Quota:                        {@link Builder#quota(Long)}<br>
     * - Notes:                        {@link Builder#notes(String)}<br>
     * - Recycle bin:                  {@link Builder#hasRecycleBin(Boolean)}<br>
     * - Recycle bin retention period: {@link Builder#recycleBinRetentionPeriod(Integer)}<br>
     * - Permission inheritance:       {@link Builder#hasInheritPermissions(Boolean)}<br>
     * - Admin user IDs:               {@link Builder#adminUserIds(List)}<br>
     * - Admin group IDs:              {@link Builder#adminGroupIds(List)}<br>
     * - Group member acceptance       {@link Builder#newGroupMemberAcceptance(GroupMemberAcceptance)}
     */
    public static class Builder {

        private CreateRoomRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param name The name of the new room. (Name must not be empty.)
         */
        public Builder(String name) {
            mRequest = new CreateRoomRequest();
            mRequest.mName = name;
        }

        /**
         * Sets the parent node ID of the new room.<br>
         * <br>
         * For root rooms the ID can be left empty.
         *
         * @param parentId The parent node ID. (ID must be positive.)
         *
         * @return a reference to this object
         */
        public Builder parentId(Long parentId) {
            mRequest.mParentId = parentId;
            return this;
        }

        /**
         * Sets the quota of the new room.
         *
         * @param quota The quota. (Quota must be positive.)
         *
         * @return a reference to this object
         */
        public Builder quota(Long quota) {
            mRequest.mQuota = quota;
            return this;
        }

        /**
         * Sets the notes of the new room.
         *
         * @param notes The notes.
         *
         * @return a reference to this object
         */
        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        /**
         * Enables/disables the recycle bin on the new room.
         *
         * @param hasRecycleBin <code>true</code> to enable recycle bin; otherwise <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder hasRecycleBin(Boolean hasRecycleBin) {
            mRequest.mHasRecycleBin = hasRecycleBin;
            return this;
        }

        /**
         * Sets the recycle bin retention period of the new room.
         *
         * @param recycleBinRetentionPeriod The recycle bin retention period. (Period must be positive.)
         *
         * @return a reference to this object
         */
        public Builder recycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
            mRequest.mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
            return this;
        }

        /**
         * Enables/disables permission inheritance on the new room.
         *
         * @param hasInheritPermissions code>true</code> to enable permission inheritance; otherwise
         *                              <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder hasInheritPermissions(Boolean hasInheritPermissions) {
            mRequest.mHasInheritPermissions = hasInheritPermissions;
            return this;
        }

        /**
         * Sets IDs of administrator users on the new room.
         *
         * @param adminUserIds The administrator user IDs. (IDs must be positive.)
         *
         * @return a reference to this object
         */
        public Builder adminUserIds(List<Long> adminUserIds) {
            mRequest.mAdminUserIds = adminUserIds;
            return this;
        }

        /**
         * Sets IDs of administrator groups on the new room.
         *
         * @param adminGroupIds The administrator group IDs. (IDs must be positive.)
         *
         * @return a reference to this object
         */
        public Builder adminGroupIds(List<Long> adminGroupIds) {
            mRequest.mAdminGroupIds = adminGroupIds;
            return this;
        }

        /**
         * Sets the group member acceptance type of the new room.
         *
         * @param newGroupMemberAcceptance The group member acceptance type.
         *
         * @return a reference to this object
         */
        public Builder newGroupMemberAcceptance(GroupMemberAcceptance newGroupMemberAcceptance) {
            mRequest.mNewGroupMemberAcceptance = newGroupMemberAcceptance;
            return this;
        }

        /**
         * Creates a new {@link CreateRoomRequest} instance with the supplied configuration.
         *
         * @return a new {@link CreateRoomRequest} instance
         */
        public CreateRoomRequest build() {
            return mRequest;
        }

    }

}
