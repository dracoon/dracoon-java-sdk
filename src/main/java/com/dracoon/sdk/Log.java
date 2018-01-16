package com.dracoon.sdk;

/**
 * Log is the interface for custom logger implementations of the Dracoon SDK.<br>
 * <br>
 * A custom logger can be set via the DracoonClient builder method
 * {@link DracoonClient.Builder#log(Log)}.
 */
@SuppressWarnings("unused")
public interface Log {

    /**
     * Priority constant for the println method; use Log.v.
     */
    int VERBOSE = 1;

    /**
     * Priority constant for the println method; use Log.d.
     */
    int DEBUG = 2;

    /**
     * Priority constant for the println method; use Log.i.
     */
    int INFO = 3;

    /**
     * Priority constant for the println method; use Log.w.
     */
    int WARN = 4;

    /**
     * Priority constant for the println method; use Log.e.
     */
    int ERROR = 5;

    /**
     * Writes a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void v(String tag, String msg);

    /**
     * Writes a {@link #VERBOSE} log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void v(String tag, String msg, Throwable tr);

    /**
     * Writes a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void d(String tag, String msg);

    /**
     * Writes a {@link #DEBUG} log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void d(String tag, String msg, Throwable tr);

    /**
     * Writes an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void i(String tag, String msg);

    /**
     * Writes a {@link #INFO} log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void i(String tag, String msg, Throwable tr);

    /**
     * Writes a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void w(String tag, String msg);

    /**
     * Writes a {@link #WARN} log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void w(String tag, String msg, Throwable tr);

    /**
     * Writes an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void e(String tag, String msg);

    /**
     * Writes a {@link #ERROR} log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void e(String tag, String msg, Throwable tr);

}
