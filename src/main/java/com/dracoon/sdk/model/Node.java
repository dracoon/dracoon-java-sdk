package com.dracoon.sdk.model;

public class Node {

    private Long mId;
    private String mType;
    private Long mParentId;
    private String mParentPath;
    private String mName;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Long getParentId() {
        return mParentId;
    }

    public void setParentId(Long parentId) {
        mParentId = parentId;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public void setParentPath(String parentPath) {
        mParentPath = parentPath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
