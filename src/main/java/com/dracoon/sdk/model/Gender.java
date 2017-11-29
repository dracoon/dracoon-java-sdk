package com.dracoon.sdk.model;

public enum Gender {

    MALE("m"),
    FEMALE("f"),
    NEUTRAL("n");

    private String mValue;

    Gender(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

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
