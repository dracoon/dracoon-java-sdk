package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "WeakerAccess", // Weaker access is not possible (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiConflictNode {

    public Long nodeId;
    public String name;
    public Integer errorCode;
    public String errorMessage;

    @Override
    public String toString() {
        return "ConflictNode{" +
                "nodeId=" + nodeId + ", " +
                "name='" + name + "', " +
                "errorCode=" + errorCode + "', " +
                "errorMessage='" + errorMessage + "'" +
                '}';
    }

}
