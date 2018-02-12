package com.dracoon.sdk.internal.oauth;

import com.google.gson.annotations.SerializedName;

public class OAuthError {

    @SerializedName("error")
    public String error;
    @SerializedName("error_description")
    public String errorDescription;

    @Override
    public String toString() {
        return "OAuthError{" +
                "error=" + error + ", " +
                "errorDescription='" + errorDescription +
                '}';
    }

}
