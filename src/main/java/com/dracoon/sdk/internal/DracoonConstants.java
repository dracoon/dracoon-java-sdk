package com.dracoon.sdk.internal;

public interface DracoonConstants {

    String API_PATH = "/api/v4";
    String API_MIN_VERSION = "4.0.0";

    String API_TIME_ZONE = "UTC";
    String API_DATE_FORMAT = "yyyy-MM-dd";
    String API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    String AUTHORIZATION_HEADER = "Authorization";
    String AUTHORIZATION_TYPE = "Bearer";
    int AUTHORIZATION_REFRESH_INTERVAL = 60 * 60;

}
