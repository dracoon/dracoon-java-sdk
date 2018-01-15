package com.dracoon.sdk.model;

/**
 * Enumeration of user role types.
 */
@SuppressWarnings("unused")
public enum UserRole {

    CONFIG_MANAGER(1),
    USER_MANAGER(2),
    GROUP_MANAGER(3),
    ROOM_MANAGER(4),
    LOG_AUDITOR(5);

    private int mValue;

    /**
     * Constructs a new enumeration constant with the provided user role type value.
     *
     * @param value The user role value.
     */
    UserRole(int value) {
        mValue = value;
    }

    /**
     * Returns the value of the user role type.
     *
     * @return the user role type value
     */
    public int getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided user role type value.
     *
     * @param value The user role type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static UserRole getByValue(int value) {
        for (UserRole r : UserRole.values()) {
            if (r.mValue == value) {
                return r;
            }
        }
        return null;
    }

}
