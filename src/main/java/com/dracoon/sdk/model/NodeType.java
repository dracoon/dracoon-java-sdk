package com.dracoon.sdk.model;

public enum NodeType {

    ROOM("room"),
    FOLDER("folder"),
    FILE("file");

    private String mValue;

    NodeType(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public static NodeType getByValue(String value) {
        if (value == null) {
            return null;
        }

        for (NodeType t : NodeType.values()) {
            if (value.equals(t.mValue)) {
                return t;
            }
        }
        return null;
    }

}
