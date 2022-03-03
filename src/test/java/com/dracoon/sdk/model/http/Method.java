package com.dracoon.sdk.model.http;

import com.google.gson.annotations.SerializedName;

public enum Method {
    @SerializedName("head")
    HEAD,
    @SerializedName("get")
    GET,
    @SerializedName("post")
    POST,
    @SerializedName("put")
    PUT,
    @SerializedName("patch")
    PATCH,
    @SerializedName("delete")
    DELETE;
}
