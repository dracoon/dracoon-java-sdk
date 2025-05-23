package com.dracoon.sdk.internal.api.mapper;

public abstract class BaseMapper {

    protected BaseMapper() {

    }

    protected static Boolean toBoolean(Boolean b) {
        return b != null ? b : false;
    }

}
