package com.dracoon.sdk.error;

/**
 * Enumeration of Dracoon crypto error codes.
 */
@SuppressWarnings("unused")
public enum DracoonCryptoCode {

    UNSUPPORTED_VERSION_ERROR(-1, "The provided crypto version is not supported."),
    MISSING_PASSWORD_ERROR(-2, "No password has been provided."),
    INVALID_PASSWORD_ERROR(-3, "The provided password is invalid."),
    BAD_FILE_ERROR(-4, "The file integrity check failed. It may have been modified."),

    INTERNAL_ERROR(-5, "A internal error occurred."),
    SYSTEM_ERROR(-6, "A system error occurred."),
    UNKNOWN_ERROR(-7, "A unknown error occurred.");

    private final int mNumber;
    private final String mText;

    /**
     * Constructs a new enumeration constant with the provided error number and message.
     *
     * @param number The error number.
     * @param text   The error message.
     */
    DracoonCryptoCode(int number, String text) {
        mNumber = number;
        mText = text;
    }

    /**
     * Returns the error number of the enumeration constant.<br>
     * <br>
     * This number can be used to map localized error messages.
     *
     * @return the error number of this enum constant
     */
    public int getNumber() {
        return mNumber;
    }

    /**
     * Returns the error message of the enumeration constant.
     *
     * @return the error message of this enum constant
     */
    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mNumber + " " + mText;
    }

    /**
     * Finds a enumeration constant by a provided error number.
     *
     * @param number The error number of the constant to return
     *
     * @return the appropriate enumeration constant
     *
     * @throws IllegalArgumentException if no enumeration constant could be found
     */
    public static DracoonCryptoCode valueOf(int number) {
        for (DracoonCryptoCode code : values()) {
            if (code.mNumber == number) {
                return code;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + number + "]");
    }

}
