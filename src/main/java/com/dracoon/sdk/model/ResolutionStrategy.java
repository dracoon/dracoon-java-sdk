package com.dracoon.sdk.model;

/**
 * Enumeration of resolution strategy types.
 */
@SuppressWarnings("unused")
public enum ResolutionStrategy {

    AUTO_RENAME("autorename"),
    OVERWRITE("overwrite"),
    FAIL("fail");

    private String mValue;

    /**
     * Constructs a new enumeration constant with the provided resolution strategy type value.
     *
     * @param value The resolution strategy type value.
     */
    ResolutionStrategy(String value) {
        mValue = value;
    }

    /**
     * Returns the value of the resolution strategy type.
     *
     * @return the resolution strategy type value
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided resolution strategy type value.
     *
     * @param value The resolution strategy type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static ResolutionStrategy getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (ResolutionStrategy s : ResolutionStrategy.values()) {
            if (value.equals(s.mValue)) {
                return s;
            }
        }
        return null;
    }

}
