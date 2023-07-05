package com.dracoon.sdk.internal;

@SuppressWarnings({"unused", "WeakerAccess"}) // Some constants are for future usage
public abstract class DracoonConstants {

    private DracoonConstants() {}

    public static final int KIB = 1024;
    public static final int MIB = 1024 * KIB;
    public static final int GIB = 1024 * MIB;

    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    public static final String API_PATH = "/api/v4";
    public static final String API_MIN_VERSION = "4.33.0";

    public static final String API_TIME_ZONE = "UTC";
    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_TYPE = "Bearer";

    public static final int S3_DEFAULT_CHUNK_SIZE = 5 * DracoonConstants.MIB;

}
