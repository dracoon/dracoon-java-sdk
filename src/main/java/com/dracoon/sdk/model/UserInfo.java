package com.dracoon.sdk.model;

import java.util.UUID;

/**
 * User info model.<br>
 * <br>
 * This model stores information about the user.
 */
@SuppressWarnings("unused")
public class UserInfo {

    /**
     * Enumeration of user types.
     */
    public enum UserType {

        SYSTEM("system"),
        INTERNAL("internal"),
        EXTERNAL("external"),
        DELETED("deleted");

        private final String mValue;

        /**
         * Constructs a new enumeration constant with the provided user type value.
         *
         * @param value The user type value.
         */
        UserType(String value) {
            mValue = value;
        }

        /**
         * Returns the value of the user type.
         *
         * @return the user type value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Finds a enumeration constant by a provided user type value.
         *
         * @param value The user type value of the constant to return.
         *
         * @return the appropriate enumeration constant, or <code>null</code> if no matching
         *         enumeration constant could be found
         */
        public static UserType getByValue(String value) {
            if (value == null) {
                return null;
            }

            for (UserType t : UserType.values()) {
                if (value.equals(t.mValue)) {
                    return t;
                }
            }
            return null;
        }

    }

    private Long mId;
    private UserType mUserType;
    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private String mEmail;
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
     * Returns the type of the user.
     *
     * @return the type
     */
    public UserType getUserType() {
        return mUserType;
    }

    /**
     * Sets the type of the user.
     *
     * @param userType The type.
     */
    public void setUserType(UserType userType) {
        mUserType = userType;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name.
     */
    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address.
     */
    public void setEmail(String email) {
        mEmail = email;
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
