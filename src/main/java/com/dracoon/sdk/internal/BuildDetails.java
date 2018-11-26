package com.dracoon.sdk.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildDetails {

    private static final String PROPERTIES_FILE = "build-details.properties";

    private static String mVersion;
    private static String mBuildTimestamp;

    static {
        Properties p = loadProperties();
        init(p);
    }

    private BuildDetails() {}

    private static Properties loadProperties() {
        String fileName = PROPERTIES_FILE;

        Properties properties = new Properties();
        try (InputStream is = BuildDetails.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(is);
        } catch(IOException e) {
            throw new Error(String.format("Loading properties from '%s' failed!", fileName), e);
        }

        return properties;
    }

    private static void init(Properties props) {
        mVersion = props.getProperty("version");
        mBuildTimestamp = props.getProperty("buildTimestamp");
    }

    public static String getVersion() {
        return mVersion;
    }

    public static String getBuildTimestamp() {
        return mBuildTimestamp;
    }

}