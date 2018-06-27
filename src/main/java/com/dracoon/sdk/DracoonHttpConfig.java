package com.dracoon.sdk;

import java.net.InetAddress;

import com.dracoon.sdk.internal.BuildDetails;
import com.dracoon.sdk.internal.validator.ValidatorUtils;

/**
 * DracoonHttpConfig is used to configure HTTP communication options.<br>
 * <br>
 * Following options can be configured:<br>
 * - User-Agent string             (Default: Java-SDK|[VERSION]|-|-|[BUILD_TIMESTAMP])<br>
 * - Auto-retry of failed requests (Default: disabled)<br>
 * - HTTP connection timeout       (Default: 15 seconds)<br>
 * - HTTP read timeout             (Default: 15 seconds)<br>
 * - HTTP write timeout            (Default: 15 seconds)<br>
 * - Proxy server enabled          (Default: false)<br>
 * - Proxy server address          (Default: null)<br>
 * - Proxy server port             (Default: null)
 */
public class DracoonHttpConfig {

    private String mUserAgent;
    private boolean mRetryEnabled;
    private int mConnectTimeout;
    private int mReadTimeout;
    private int mWriteTimeout;
    private boolean mProxyEnabled = false;
    private InetAddress mProxyAddress;
    private Integer mProxyPort;

    /**
     * Constructs a default HTTP configuration.
     */
    public DracoonHttpConfig() {
        mUserAgent = buildDefaultUserAgentString();
        mRetryEnabled = false;
        mConnectTimeout = 15;
        mReadTimeout = 15;
        mWriteTimeout = 15;
    }

    /**
     * Returns the User-Agent string.
     *
     * @return the User-Agent string
     */
    public String getUserAgent() {
        return mUserAgent;
    }

    /**
     * Sets the User-Agent string.
     *
     * @param userAgent The User-Agent string.
     */
    public void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
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

    /**
     * Enables the use of a proxy server and sets the address and port to use.
     *
     * @param address The proxy server address.
     * @param port    The proxy server port.
     */
    public void setProxy(InetAddress address, Integer port) {
        ValidatorUtils.validateNotNull("Address", address);
        ValidatorUtils.validateNotNull("Port", port);
        mProxyEnabled = true;
        mProxyAddress = address;
        mProxyPort = port;
    }

    /**
     * Returns <code>true</code> if a proxy server is used.
     *
     * @return <code>true</code> if a proxy server is used; <code>false</code> otherwise
     */
    public boolean isProxyEnabled() {
        return mProxyEnabled;
    }

    /**
     * Returns the address of the proxy server, if a proxy server was configured.
     *
     * @return the address of the proxy server; or <code>null</code> if no proxy server is configured
     */
    public InetAddress getProxyAddress() {
        return mProxyAddress;
    }

    /**
     * Returns the port of the proxy server, if a proxy server was configured.
     *
     * @return the port of the proxy server; or <code>null</code> if no proxy server is configured
     */
    public Integer getProxyPort() {
        return mProxyPort;
    }

    private static String buildDefaultUserAgentString() {
        return "Java-SDK" + "|" +
                BuildDetails.getVersion() + "|" +
                "-" + "|" +
                "-" + "|" +
                BuildDetails.getBuildTimestamp();
    }

}
