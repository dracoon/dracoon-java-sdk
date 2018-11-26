package com.dracoon.sdk.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dracoon.sdk.Log;

@SuppressWarnings("WeakerAccess")
public class Logger implements Log {

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    private int mLevel;

    public Logger(int level) {
        mLevel = level;
    }

    @Override
    public void d(String tag, String msg) {
        if (mLevel <= DEBUG) {
            System.out.println(buildLogMessage("d", tag, msg));
        }
    }

    @Override
    public void d(String tag, String msg, Throwable tr) {
        if (mLevel <= DEBUG) {
            d(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (mLevel <= INFO) {
            System.out.println(buildLogMessage("i", tag, msg));
        }
    }

    @Override
    public void i(String tag, String msg, Throwable tr) {
        if (mLevel <= INFO) {
            i(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (mLevel <= WARN) {
            System.out.println(buildLogMessage("w", tag, msg));
        }
    }

    @Override
    public void w(String tag, String msg, Throwable tr) {
        if (mLevel <= WARN) {
            w(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (mLevel <= ERROR) {
            System.out.println(buildLogMessage("e", tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (mLevel <= ERROR) {
            e(tag, msg);
            tr.printStackTrace(System.err);
        }
    }

    private static String buildLogMessage(String lvl, String tag, String msg) {
        return new StringBuilder()
                .append(df.format(new Date()))
                .append(" ")
                .append(lvl)
                .append("/")
                .append(tag)
                .append(" ")
                .append(msg)
                .toString();
    }

}
