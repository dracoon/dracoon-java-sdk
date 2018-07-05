package com.dracoon.sdk.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.dracoon.sdk.internal.DracoonConstants;

public class DateUtils {

    private static SimpleDateFormat sDateFormat;
    private static SimpleDateFormat sTimeFormat;

    static {
        sDateFormat = new SimpleDateFormat(DracoonConstants.API_DATE_FORMAT);
        sDateFormat.setTimeZone(TimeZone.getTimeZone(DracoonConstants.API_TIME_ZONE));

        sTimeFormat = new SimpleDateFormat(DracoonConstants.API_TIME_FORMAT);
        sTimeFormat.setTimeZone(TimeZone.getTimeZone(DracoonConstants.API_TIME_ZONE));
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
            throw new Error(e);
        }
    }

    public static String formatDate(Date value) {
        if (value == null) {
            return null;
        }

        return sDateFormat.format(value);
    }

    public static Date parseTime(String value) {
        if (value == null) {
            return null;
        }

        try {
            return sTimeFormat.parse(value);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    public static String formatTime(Date value) {
        if (value == null) {
            return null;
        }

        return sTimeFormat.format(value);
    }

}
