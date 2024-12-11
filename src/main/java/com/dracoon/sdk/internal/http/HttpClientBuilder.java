package com.dracoon.sdk.internal.http;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.internal.DracoonConstants;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class HttpClientBuilder {

    private static final int HTTP_SOCKET_BUFFER_SIZE = 16 * DracoonConstants.KIB;

    public OkHttpClient build(DracoonHttpConfig httpConfig) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.socketFactory(new BufferedSocketFactory(HTTP_SOCKET_BUFFER_SIZE));
        if (httpConfig.isProxyEnabled()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    httpConfig.getProxyAddress(), httpConfig.getProxyPort()));
            builder.proxy(proxy);
        }
        for (Interceptor interceptor : httpConfig.getOkHttpApplicationInterceptors()) {
            builder.addInterceptor(interceptor);
        }
        builder.addNetworkInterceptor(new UserAgentInterceptor(httpConfig.getUserAgent()));
        for (Interceptor interceptor : httpConfig.getOkHttpNetworkInterceptors()) {
            builder.addNetworkInterceptor(interceptor);
        }
        return builder.build();
    }

}
