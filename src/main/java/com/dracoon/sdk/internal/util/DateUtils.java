package com.dracoon.sdk.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static SimpleDateFormat sDateFormat;

    static {
        sDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private DateUtils() {

    }

    public static Date parseDate(String value) {
        if (value == null) {
            return null;
        }

        try {
            return sDateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(Date value) {
        if (value == null) {
            return null;
        }

        return sDateFormat.format(value);
    }

}
