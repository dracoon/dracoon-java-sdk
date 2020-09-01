package com.dracoon.sdk.model;

/**
 * Enumeration of password policies character types.
 */
@SuppressWarnings("unused")
public enum PasswordPoliciesCharacterType {

    ALPHA("alpha"),
    LOWERCASE("lowercase"),
    UPPERCASE("uppercase"),
    NUMERIC("numeric"),
    SPECIAL("special");

    private String mValue;

    /**
     * Constructs a new enumeration constant with the provided character type value.
     *
     * @param value The character type value.
     */
    PasswordPoliciesCharacterType(String value) {
        mValue = value;
    }

    /**
     * Returns the value of the character type.
     *
     * @return the character type value
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided character type value.
     *
     * @param value The character type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static PasswordPoliciesCharacterType getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (PasswordPoliciesCharacterType t : PasswordPoliciesCharacterType.values()) {
            if (value.equals(t.mValue)) {
                return t;
            }
        }
        return null;
    }

}
