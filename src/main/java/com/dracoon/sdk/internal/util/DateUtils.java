package com.dracoon.sdk.internal.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.dracoon.sdk.internal.DracoonConstants;

public class DateUtils {

    private DateUtils() {

    }

    public static Date parseDate(String value) {
        if (value == null) {
            return null;
        }

        DateFormat df = createDateFormat(DracoonConstants.API_DATE_FORMAT);

        try {
            return df.parse(value);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    public static String formatDate(Date value) {
        if (value == null) {
            return null;
        }

        DateFormat df = createDateFormat(DracoonConstants.API_DATE_FORMAT);

        return df.format(value);
    }

    public static Date parseTime(String value) {
        if (value == null) {
            return null;
        }

        DateFormat tf = createDateFormat(DracoonConstants.API_TIME_FORMAT);

        try {
            return tf.parse(value);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    public static String formatTime(Date value) {
        if (value == null) {
            return null;
        }

        DateFormat tf = createDateFormat(DracoonConstants.API_TIME_FORMAT);

        return tf.format(value);
    }

    private static DateFormat createDateFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(DracoonConstants.API_TIME_ZONE));
        return df;
    }

}
