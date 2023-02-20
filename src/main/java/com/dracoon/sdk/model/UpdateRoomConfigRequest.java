package com.dracoon.sdk.model;

import java.util.List;

/**
 * Request to update the config of a room.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class UpdateRoomConfigRequest {

    private Long mId;

    private Integer mRecycleBinRetentionPeriod;

    private Boolean mHasInheritPermissions;
    private List<Long> mAdminUserIds;
    private List<Long> mAdminGroupIds;
    private GroupMemberAcceptance mNewGroupMemberAcceptance;

    private Classification mClassification;

    private UpdateRoomConfigRequest() {

    }

    /**
     * Returns the node ID of the room which should be updated.
     *
     * @return the node ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Returns the recycle bin retention period (in days) of the new room.
     *
     * @return the recycle bin retention period
     */
    public Integer getRecycleBinRetentionPeriod() {
        return mRecycleBinRetentionPeriod;
    }

    /**
     * Returns <code>true</code> if permission inheritance is enabled on the room.
     *
     * @return <code>true</code> if permission inheritance is enabled; <code>false</code> otherwise
     */
    public Boolean hasInheritPermissions() {
        return mHasInheritPermissions;
    }

    /**
     * Returns IDs of administrator users of the room.
     *
     * @return IDs of administrator users
     */
    public List<Long> getAdminUserIds() {
        return mAdminUserIds;
    }

    /**
     * Returns IDs of administrator groups of the room.
     *
     * @return IDs of administrator groups
     */
    public List<Long> getAdminGroupIds() {
        return mAdminGroupIds;
    }

    /**
     * Returns the new group member acceptance type of the room.
     *
     * @return the new group member acceptance type
     */
    public GroupMemberAcceptance getNewGroupMemberAcceptance() {
        return mNewGroupMemberAcceptance;
    }

    /**
     * Returns the new default classification of folders and files of the room.
     *
     * @return the new default classification of folders and files
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * This builder creates new instances of {@link UpdateRoomConfigRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node ID (mandatory):          {@link #Builder(Long)}<br>
     * - Recycle bin retention period: {@link #recycleBinRetentionPeriod(Integer)}<br>
     * - Permission inheritance:       {@link #hasInheritPermissions(Boolean)}<br>
     * - Admin user IDs:               {@link #adminUserIds(List)}<br>
     * - Admin group IDs:              {@link #adminGroupIds(List)}<br>
     * - Group member acceptance       {@link #newGroupMemberAcceptance(GroupMemberAcceptance)}
     * - Classification:               {@link #classification(Classification)}<br>
     */
    public static class Builder {

        private final UpdateRoomConfigRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param id The node ID of the room which should be updated. (ID must be positive.)
         */
        public Builder(Long id) {
            mRequest = new UpdateRoomConfigRequest();
            mRequest.mId = id;
        }

        /**
         * Sets the new recycle bin retention period (in days) of the room.
         *
         * @param recycleBinRetentionPeriod The new recycle bin retention period. (Period must be
         *                                  positive.)
         *
         * @return a reference to this object
         */
        public Builder recycleBinRetentionPeriod(Integer recycleBinRetentionPeriod) {
            mRequest.mRecycleBinRetentionPeriod = recycleBinRetentionPeriod;
            return this;
        }

        /**
         * Enables/disables permission inheritance on the room.
         *
         * @param hasInheritPermissions <code>true</code> to enable permission inheritance;
         *                              otherwise <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder hasInheritPermissions(Boolean hasInheritPermissions) {
            mRequest.mHasInheritPermissions = hasInheritPermissions;
            return this;
        }

        /**
         * Sets IDs of administrator users on the room.
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
         * Sets IDs of administrator groups on the room.
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
         * Sets the new group member acceptance type of the room.
         *
         * @param newGroupMemberAcceptance The new group member acceptance type.
         *
         * @return a reference to this object
         */
        public Builder newGroupMemberAcceptance(GroupMemberAcceptance newGroupMemberAcceptance) {
            mRequest.mNewGroupMemberAcceptance = newGroupMemberAcceptance;
            return this;
        }

        /**
         * Sets the new default classification of folders and files of the room.
         *
         * @param classification The new default classification of folders and files.
         *
         * @return a reference to this object
         */
        public Builder classification(Classification classification) {
            mRequest.mClassification = classification;
            return this;
        }

        /**
         * Creates a new {@link UpdateRoomConfigRequest} instance with the supplied configuration.
         *
         * @return a new {@link UpdateRoomConfigRequest} instance
         */
        public UpdateRoomConfigRequest build() {
            return mRequest;
        }

    }

}
