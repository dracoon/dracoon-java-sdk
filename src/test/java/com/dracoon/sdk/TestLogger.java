package com.dracoon.sdk;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestLogger implements Log {

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final boolean mIsDebug;

    public TestLogger() {
        mIsDebug = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("-agentlib:jdwp") > 0;
    }

    @Override
    public void d(String tag, String msg) {
        if (mIsDebug) {
            System.out.println(buildLogMessage("d", tag, msg));
        }
    }

    @Override
    public void d(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            d(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (mIsDebug) {
            System.out.println(buildLogMessage("i", tag, msg));
        }
    }

    @Override
    public void i(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            i(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (mIsDebug) {
            System.out.println(buildLogMessage("w", tag, msg));
        }
    }

    @Override
    public void w(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
            w(tag, msg);
            tr.printStackTrace(System.out);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (mIsDebug) {
            System.out.println(buildLogMessage("e", tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (mIsDebug) {
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
