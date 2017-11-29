package com.dracoon.sdk.model;

public enum UserRole {

    CONFIG_MANAGER(1),
    USER_MANAGER(2),
    GROUP_MANAGER(3),
    ROOM_MANAGER(4),
    LOG_AUDITOR(5);

    private int mValue;

    UserRole(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static UserRole getByValue(int value) {
        for (UserRole r : UserRole.values()) {
            if (r.mValue == value) {
                return r;
            }
        }
        return null;
    }

}
