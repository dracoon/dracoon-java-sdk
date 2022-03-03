package com.dracoon.sdk.model.http;

import com.google.gson.annotations.SerializedName;

public class Body {

    public enum Type {
        @SerializedName("text")
        TEXT,
        @SerializedName("base64")
        BASE64,
        @SerializedName("file")
        FILE,
        @SerializedName("part-file")
        PART_FILE;
    }

    public Type type;
    public String content;

}
