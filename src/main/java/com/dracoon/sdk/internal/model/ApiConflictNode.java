package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", "WeakerAccess"})
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
