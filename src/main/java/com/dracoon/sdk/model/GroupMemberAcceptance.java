package com.dracoon.sdk.model;

/**
 * Enumeration of group member acceptance types.
 */
@SuppressWarnings("unused")
public enum GroupMemberAcceptance {

    AUTO_ALLOW("autoallow"),
    PENDING("pending");

    private String mValue;

    /**
     * Constructs a new enumeration constant with the provided group member acceptance type value.
     *
     * @param value The group member acceptance type value.
     */
    GroupMemberAcceptance(String value) {
        mValue = value;
    }

    /**
     * Returns the value of the group member acceptance type.
     *
     * @return the group member acceptance type value
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided group member acceptance type value.
     *
     * @param value The group member acceptance type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static GroupMemberAcceptance getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (GroupMemberAcceptance a : GroupMemberAcceptance.values()) {
            if (value.equals(a.mValue)) {
                return a;
            }
        }
        return null;
    }

}
