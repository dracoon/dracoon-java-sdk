package com.dracoon.sdk.model;

import java.util.UUID;

/**
 * User info model.<br>
 * <br>
 * This model stores information about the user.
 */
@SuppressWarnings("unused")
public class UserInfo {

    private Long mId;
    private String mDisplayName;
    private UUID mAvatarUuid;

    /**
     * Returns the ID of the user.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the display name of the user.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param displayName The display name.
     */
    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    /**
     * Returns the avatar UUID of the user.
     *
     * @return the avatar UUID
     */
    public UUID getAvatarUuid() {
        return mAvatarUuid;
    }

    /**
     * Sets avatar UUID of the user.
     *
     * @param avatarUuid The avatar UUID.
     */
    public void setAvatarUuid(UUID avatarUuid) {
        mAvatarUuid = avatarUuid;
    }

}
