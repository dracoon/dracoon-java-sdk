package com.dracoon.sdk.internal.model;

import java.util.Date;

@SuppressWarnings("unused")
public class ApiNodeComment {
    public Long id;
    public String text;

    public Date createdAt;
    public ApiUserInfo createdBy;
    public Date updatedAt;
    public ApiUserInfo updatedBy;

    public Boolean isChanged;
    public Boolean isDeleted;
}
