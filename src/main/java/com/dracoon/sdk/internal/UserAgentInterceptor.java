package com.dracoon.sdk.internal;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

    private final String mUserAgent;

    public UserAgentInterceptor(String userAgent) {
        mUserAgent = userAgent;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request requestWithUserAgent = chain.request().newBuilder()
                .header("User-Agent", mUserAgent)
                .build();

        return chain.proceed(requestWithUserAgent);
    }

}
