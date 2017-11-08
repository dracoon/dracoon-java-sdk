package com.dracoon.sdk.model;

public enum ResolutionStrategy {

    AUTO_RENAME("autorename"),
    OVERWRITE("overwrite"),
    FAIL("fail");

    private String mValue;

    ResolutionStrategy(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

}
