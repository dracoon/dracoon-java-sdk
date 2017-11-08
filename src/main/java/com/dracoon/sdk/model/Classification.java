package com.dracoon.sdk.model;

public enum Classification {

    PUBLIC(1),
    INTERNAL(2),
    CONFIDENTIAL(3),
    STRICTLY_CONFIDENTIAL(4);

    private int mValue;

    Classification(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

}
