package com.dracoon.sdk;

public interface Log {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Send a {@link #VERBOSE} Log message.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void v(String tag, String msg);

    /**
     * Send a {@link #VERBOSE} Log message and log the exception.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void v(String tag, String msg, Throwable tr);

    /**
     * Send a {@link #DEBUG} Log message.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void d(String tag, String msg);

    /**
     * Send a {@link #DEBUG} Log message and log the exception.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void d(String tag, String msg, Throwable tr);

    /**
     * Send an {@link #INFO} Log message.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void i(String tag, String msg);

    /**
     * Send a {@link #INFO} Log message and log the exception.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void i(String tag, String msg, Throwable tr);

    /**
     * Send a {@link #WARN} Log message.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void w(String tag, String msg);

    /**
     * Send a {@link #WARN} Log message and log the exception.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void w(String tag, String msg, Throwable tr);

    /**
     * Send an {@link #ERROR} Log message.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void e(String tag, String msg);

    /**
     * Send a {@link #ERROR} Log message and log the exception.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void e(String tag, String msg, Throwable tr);

}
