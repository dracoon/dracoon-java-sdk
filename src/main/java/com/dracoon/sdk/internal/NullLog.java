package com.dracoon.sdk.internal;

import com.dracoon.sdk.Log;

@SuppressWarnings("squid:S1186") // SONAR: The methods are intentionally-blank overrides
public class NullLog implements Log {

    @Override
    public void d(String tag, String msg) {

    }

    @Override
    public void d(String tag, String msg, Throwable tr) {

    }

    @Override
    public void i(String tag, String msg) {

    }

    @Override
    public void i(String tag, String msg, Throwable tr) {

    }

    @Override
    public void w(String tag, String msg) {

    }

    @Override
    public void w(String tag, String msg, Throwable tr) {

    }

    @Override
    public void e(String tag, String msg) {

    }

    @Override
    public void e(String tag, String msg, Throwable tr) {

    }

}
