package com.dracoon.sdk;

/**
 * DracoonHttpConfig is used to configure HTTP communication options.<br>
 * <br>
 * Following options can be configured:<br>
 * - Auto-retry of failed requests (Default: disabled)<br>
 * - HTTP connection timeout (Default: 15 seconds)<br>
 * - HTTP read timeout (Default: 15 seconds)<br>
 * - HTTP write timeout (Default: 15 seconds)<br>
 */
public class DracoonHttpConfig {

    private boolean mRetryEnabled;
    private int mConnectTimeout;
    private int mReadTimeout;
    private int mWriteTimeout;

    /**
     * Constructs a default HTTP configuration.
     */
    public DracoonHttpConfig() {
        mRetryEnabled = false;
        mConnectTimeout = 15;
        mReadTimeout = 15;
        mWriteTimeout = 15;
    }

    /**
     * Returns <code>true</code> if auto-retry is enabled.
     *
     * @return <code>true</code> if auto-retry is enabled; <code>false</code> otherwise
     */
    public boolean isRetryEnabled() {
        return mRetryEnabled;
    }

    /**
     * Enables/disables auto-retry.
     *
     * @param retryEnabled <code>true</code> to enable auto-retry; otherwise <code>false</code>.
     */
    public void setRetryEnabled(boolean retryEnabled) {
        this.mRetryEnabled = retryEnabled;
    }

    /**
     * Returns the HTTP connection timeout.
     *
     * @return the HTTP connection timeout
     */
    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    /**
     * Sets the HTTP connection timeout.
     *
     * @param connectTimeout The HTTP connection timeout.
     */
    public void setConnectTimeout(int connectTimeout) {
        this.mConnectTimeout = connectTimeout;
    }

    /**
     * Returns the HTTP read timeout.
     *
     * @return the HTTP read timeout
     */
    public int getReadTimeout() {
        return mReadTimeout;
    }

    /**
     * Sets the HTTP read timeout.
     *
     * @param readTimeout The HTTP read timeout.
     */
    public void setReadTimeout(int readTimeout) {
        this.mReadTimeout = readTimeout;
    }

    /**
     * Returns the HTTP write timeout.
     *
     * @return the HTTP write timeout
     */
    public int getWriteTimeout() {
        return mWriteTimeout;
    }

    /**
     * Sets the HTTP write timeout.
     *
     * @param writeTimeout The HTTP write timeout.
     */
    public void setWriteTimeout(int writeTimeout) {
        this.mWriteTimeout = writeTimeout;
    }

}
