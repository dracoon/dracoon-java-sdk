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
     * Writes a debug log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void d(String tag, String msg);

    /**
     * Writes a debug log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void d(String tag, String msg, Throwable tr);

    /**
     * Writes an information log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void i(String tag, String msg);

    /**
     * Writes a information log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void i(String tag, String msg, Throwable tr);

    /**
     * Writes a warning log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void w(String tag, String msg);

    /**
     * Writes a warning log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void w(String tag, String msg, Throwable tr);

    /**
     * Writes a error log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    void e(String tag, String msg);

    /**
     * Writes a error log message and exception.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    void e(String tag, String msg, Throwable tr);

}
