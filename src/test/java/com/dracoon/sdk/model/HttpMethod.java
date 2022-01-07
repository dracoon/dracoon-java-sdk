package com.dracoon.sdk.model;

import com.google.gson.annotations.SerializedName;

public enum HttpMethod {
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
