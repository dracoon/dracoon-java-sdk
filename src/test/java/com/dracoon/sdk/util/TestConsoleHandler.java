package com.dracoon.sdk.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class TestConsoleHandler extends Handler {

    private static final String LOG_FORMAT = "[%1$s] %2$s\n";

    public TestConsoleHandler() {
        setLevel(Level.ALL);
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        Level level = record.getLevel();
        String message = record.getMessage();
        Throwable throwable = record.getThrown();

        String m = buildMessage(level, message);
        String st = buildStackTrace(throwable);

        PrintStream ps = level.intValue() >= Level.WARNING.intValue() ? System.err : System.out;
        ps.print(m);
        if (st != null) {
            ps.print(st);
        }
    }

    private static String buildMessage(Level level, String message) {
        return String.format(LOG_FORMAT, level.getName(), message);
    }

    private static String buildStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
