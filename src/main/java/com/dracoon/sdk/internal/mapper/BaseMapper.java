package com.dracoon.sdk.internal.mapper;

public abstract class BaseMapper {

    protected BaseMapper() {

    }

    protected static Boolean toBoolean(Boolean b) {
        return b != null ? b : false;
    }

    protected static char[] toCharArray(String s) {
        return s != null ? s.toCharArray() : null;
    }

}
