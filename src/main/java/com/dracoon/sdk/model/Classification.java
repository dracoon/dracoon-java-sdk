package com.dracoon.sdk.model;

/**
 * Enumeration of file classification types.
 */
@SuppressWarnings("unused")
public enum Classification {

    PUBLIC(1),
    INTERNAL(2),
    CONFIDENTIAL(3),
    STRICTLY_CONFIDENTIAL(4);

    private int mValue;

    /**
     * Constructs a new enumeration constant with the provided classification type value.
     *
     * @param value The classification type value.
     */
    Classification(int value) {
        mValue = value;
    }

    /**
     * Returns the value of the classification type.
     *
     * @return the classification type value
     */
    public int getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided classification type value.
     *
     * @param value The classification type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
    public static Classification getByValue(int value) {
        for (Classification c : Classification.values()) {
            if (c.mValue == value) {
                return c;
            }
        }
        return null;
    }

}
