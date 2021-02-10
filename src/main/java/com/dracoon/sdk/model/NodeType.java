package com.dracoon.sdk.model;

/**
 * Enumeration of node types.
 */
@SuppressWarnings("unused")
public enum NodeType {

    ROOM("room"),
    FOLDER("folder"),
    FILE("file");

    private final String mValue;

    /**
     * Constructs a new enumeration constant with the provided node type value.
     *
     * @param value The node type value.
     */
    NodeType(String value) {
        mValue = value;
    }

    /**
     * Returns the value of the node type.
     *
     * @return the node type value
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Finds a enumeration constant by a provided node type value.
     *
     * @param value The node type value of the constant to return.
     *
     * @return the appropriate enumeration constant, or <code>null</code> if no matching enumeration
     *         constant could be found
     */
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
