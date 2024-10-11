package com.dracoon.sdk;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.BuildDetails;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.validator.ValidatorUtils;
import okhttp3.Interceptor;

/**
 * DracoonHttpConfig is used to configure HTTP communication options.<br>
 * <br>
 * Following options can be configured:<br>
 * - User-Agent string               (Default: Java-SDK|[VERSION]|-|-|[BUILD_TIMESTAMP])<br>
 * - Auto-retry of failed requests   (Default: disabled)<br>
 * - Auto-rate limiting of requests  (Default: disabled)<br>
 * - HTTP connection timeout         (Default: 15 seconds)<br>
 * - HTTP read timeout               (Default: 15 seconds)<br>
 * - HTTP write timeout              (Default: 15 seconds)<br>
 * - Upload/download chunk size      (Default: 5 MiB, Minimum: 5MiB)<br>
 * - Proxy server enabled            (Default: false)<br>
 * - Proxy server address            (Default: null)<br>
 * - Proxy server port               (Default: null)<br>
 * - OkHttp application interceptors (Default: none)<br>
 * - OkHttp network interceptors     (Default: none)
 */
public class DracoonHttpConfig {

    private static final int MIN_CHUNK_SIZE = (5 * DracoonConstants.MIB) / DracoonConstants.KIB;

    private String mUserAgent;
    private boolean mRetryEnabled;
    private boolean mRateLimitingEnabled;
    private int mConnectTimeout;
    private int mReadTimeout;
    private int mWriteTimeout;
    private int mChunkSize = MIN_CHUNK_SIZE;
    private boolean mProxyEnabled = false;
    private InetAddress mProxyAddress;
    private Integer mProxyPort;

    private final List<Interceptor> mOkHttpApplicationInterceptors = new ArrayList<>();
    private final List<Interceptor> mOkHttpNetworkInterceptors = new ArrayList<>();

    /**
     * Constructs a default HTTP configuration.
     */
    public DracoonHttpConfig() {
        mUserAgent = buildDefaultUserAgentString();
        mRetryEnabled = false;
        mRateLimitingEnabled = false;
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
        mRetryEnabled = retryEnabled;
    }

    /**
     * Returns <code>true</code> if auto-rate-limiting is enabled.
     *
     * @return <code>true</code> if auto-rate-limiting is enabled; <code>false</code> otherwise
     */
    public boolean isRateLimitingEnabled() {
        return mRateLimitingEnabled;
    }

    /**
     * Enables/disables auto-rate-limiting.
     *
     * @param rateLimitingEnabled <code>true</code> to enable auto-rate-limiting; otherwise
     *                            <code>false</code>.
     */
    public void setRateLimitingEnabled(boolean rateLimitingEnabled) {
        mRateLimitingEnabled = rateLimitingEnabled;
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
        mConnectTimeout = connectTimeout;
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
        mReadTimeout = readTimeout;
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
        mWriteTimeout = writeTimeout;
    }

    /**
     * Returns the upload/download chunk size in KiB.
     *
     * @return the upload/download chunk size
     */
    public int getChunkSize() {
        return mChunkSize;
    }

    /**
     * Sets the upload/download chunk size in KiB.
     *
     * @param chunkSize The upload/download chunk size.
     */
    public void setChunkSize(int chunkSize) {
        if (chunkSize > MIN_CHUNK_SIZE) {
            mChunkSize = chunkSize;
        } else {
            mChunkSize = MIN_CHUNK_SIZE;
        }
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
     * @return the address of the proxy server; or <code>null</code> if no proxy server is
     * configured
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

    /**
     * Returns the list of added OkHttp application interceptors.
     *
     * @return the list of added OkHttp application interceptors
     */
    public List<Interceptor> getOkHttpApplicationInterceptors() {
        return mOkHttpApplicationInterceptors;
    }

    /**
     * Adds an OkHttp application interceptor.<br>
     * <br>
     * The Dracoon SDK uses OkHttp for http communication. OkHttp interceptors allow you to monitor,
     * rewrite, ... outgoing requests and incoming responses. For more information see OkHttp's
     * <a href="https://github.com/square/okhttp/wiki/Interceptors">documentation</a>.
     *
     * @param interceptor The OkHttp application interceptor.
     */
    public void addOkHttpApplicationInterceptor(Interceptor interceptor) {
        ValidatorUtils.validateNotNull("OkHttp application interceptor", interceptor);
        mOkHttpApplicationInterceptors.add(interceptor);
    }

    /**
     * Returns the list of added OkHttp network interceptors.
     *
     * @return the list of added OkHttp network interceptors
     */
    public List<Interceptor> getOkHttpNetworkInterceptors() {
        return mOkHttpNetworkInterceptors;
    }

    /**
     * Adds an OkHttp network interceptor.<br>
     * <br>
     * The Dracoon SDK uses OkHttp for http communication. OkHttp interceptors allow you to monitor,
     * rewrite, ... outgoing requests and incoming responses. For more information see OkHttp's
     * <a href="https://github.com/square/okhttp/wiki/Interceptors">documentation</a>.
     *
     * @param interceptor The OkHttp network interceptor.
     */
    public void addOkHttpNetworkInterceptor(Interceptor interceptor) {
        ValidatorUtils.validateNotNull("OkHttp network interceptor", interceptor);
        mOkHttpNetworkInterceptors.add(interceptor);
    }

    private static String buildDefaultUserAgentString() {
        return "Java-SDK" + "|" +
                BuildDetails.getVersion() + "|" +
                "-" + "|" +
                "-" + "|" +
                BuildDetails.getBuildTimestamp();
    }

}
