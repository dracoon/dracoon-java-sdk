package com.dracoon.sdk.error;

@SuppressWarnings("unused")
public enum DracoonCryptoCode {

    INVALID_PASSWORD_ERROR(-1, "The provided password is invalid."),
    BAD_FILE_ERROR(-2, "The file integrity check failed. It may have been modified."),

    INTERNAL_ERROR(-3, "A internal error occurred"),
    SYSTEM_ERROR(-4, "A system error occurred."),
    UNKNOWN_ERROR(-5, "A unknown error occurred.");

    private final int mNumber;
    private final String mText;

    DracoonCryptoCode(int number, String text) {
        mNumber = number;
        mText = text;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mNumber + " " + mText;
    }

    public static DracoonCryptoCode valueOf(int dracoonCryptoCode) {
        for (DracoonCryptoCode code : values()) {
            if (code.mNumber == dracoonCryptoCode) {
                return code;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + dracoonCryptoCode + "]");
    }

}
