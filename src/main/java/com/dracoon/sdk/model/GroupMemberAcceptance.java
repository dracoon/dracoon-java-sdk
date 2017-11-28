package com.dracoon.sdk.model;

public enum GroupMemberAcceptance {

    AUTO_ALLOW("autoallow"),
    PENDING("pending");

    private String mValue;

    GroupMemberAcceptance(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public static GroupMemberAcceptance getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (GroupMemberAcceptance t : GroupMemberAcceptance.values()) {
            if (value.equals(t.mValue)) {
                return t;
            }
        }
        return null;
    }

}
