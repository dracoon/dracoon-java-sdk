package com.dracoon.sdk.internal.mapper;

public abstract class BaseMapper {

    protected static Boolean toBoolean(Boolean b) {
        return b != null ? b : false;
    }

}
