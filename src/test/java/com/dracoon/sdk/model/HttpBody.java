package com.dracoon.sdk.model;

import com.google.gson.annotations.SerializedName;

public class HttpBody {

    public enum Type {
        @SerializedName("text")
        TEXT,
        @SerializedName("base64")
        BASE64,
        @SerializedName("file")
        FILE;
    }

    public Type type;
    public String content;

}
