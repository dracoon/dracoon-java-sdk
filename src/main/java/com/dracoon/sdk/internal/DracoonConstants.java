package com.dracoon.sdk.internal;

public interface DracoonConstants {

    int KIB = 1024;
    int MIB = 1024 * KIB;
    int GIB = 1024 * MIB;

    long SECOND = 1000L;
    long MINUTE = 60 * SECOND;
    long HOUR = 60 * MINUTE;
    long DAY = 24 * HOUR;

    String API_PATH = "/api/v4";
    String API_MIN_VERSION = "4.12.0";
    String API_MIN_PASSWORD_POLICIES = "4.13.0";
    String API_MIN_S3_DIRECT_UPLOAD = "4.15.0";
    String API_MIN_NEW_CRYPTO_ALGOS = "4.24.0";
    String API_MIN_CLASSIFICATION_POLICIES = "4.30.0";

    String API_TIME_ZONE = "UTC";
    String API_DATE_FORMAT = "yyyy-MM-dd";
    String API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    String AUTHORIZATION_HEADER = "Authorization";
    String AUTHORIZATION_TYPE = "Bearer";
    long AUTHORIZATION_REFRESH_INTERVAL = HOUR - MINUTE;

}
