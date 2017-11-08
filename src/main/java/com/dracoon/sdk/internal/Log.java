package com.dracoon.sdk.internal;

public class Log {

    private static com.dracoon.sdk.Log sLog;

    private Log() {

    }

    public static void setLog(com.dracoon.sdk.Log log) {
        sLog = log;
    }

    public static void v(String tag, String msg) {
        if (sLog != null) {
            sLog.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sLog != null) {
            sLog.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (sLog != null) {
            sLog.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sLog != null) {
            sLog.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sLog != null) {
            sLog.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sLog != null) {
            sLog.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sLog != null) {
            sLog.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sLog != null) {
            sLog.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (sLog != null) {
            sLog.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sLog != null) {
            sLog.e(tag, msg, tr);
        }
    }

}
