package com.dracoon.sdk.error;

public enum DracoonApiCode {

    UNKNOWN(0, "Unknown error");

    private int mNumber;
    private String mText;

    DracoonApiCode(int number, String text) {
        mNumber = number;
        mText = text;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getText() {
        return mText;
    }

}
