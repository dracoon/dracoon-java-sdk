package com.dracoon.sdk.model;

/**
 * Enumeration of gender types.
 */
@SuppressWarnings("unused")
public enum Gender {

    MALE("m"),
    FEMALE("f"),
    NEUTRAL("n");

    private final String mValue;

    /**
     * Constructs a new enumeration constant with the provided gender type value.
     *
     * @param value The gender type value.
     */
    Gender(String value) {
        mValue = value;
    }

    /**
     * Returns the value of the gender type.
     *
     * @return the gender type value
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided gender type value.
     *
     * @param value The gender type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static Gender getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (Gender g : Gender.values()) {
            if (value.equals(g.mValue)) {
                return g;
            }
        }
        return null;
    }

}
