package com.dracoon.sdk.internal.util;

public class VersionUtils {

    private VersionUtils() {

    }

    public static boolean isVersionGreaterEqual(String actualVersion, String compareVersion) {
        String[] actualVersionParts = splitVersion(actualVersion);
        String[] compareVersionParts = splitVersion(compareVersion);
        return isVersionGreaterEqual(actualVersionParts, compareVersionParts);
    }

    private static String[] splitVersion(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty.");
        }

        String[] parts = version.split("\\-");
        parts = parts[0].split("\\+");
        parts = parts[0].split("\\.");

        if (parts.length < 3) {
            throw new IllegalArgumentException("Version string must have at least 3 numeric parts.");
        }

        return parts;
    }

    private static boolean isVersionGreaterEqual(String[] actualVersion, String[] compareVersion) {
        for (int i = 0; i < 3; i++) {
            int avp;
            int cvp;

            try {
                avp = Integer.valueOf(actualVersion[i]);
                cvp = Integer.valueOf(compareVersion[i]);
            } catch (Exception e) {
                throw new RuntimeException("Can't parse version.", e);
            }

            if (avp > cvp) {
                break;
            } else if (avp < cvp) {
                return false;
            }
        }

        return true;
    }

}
