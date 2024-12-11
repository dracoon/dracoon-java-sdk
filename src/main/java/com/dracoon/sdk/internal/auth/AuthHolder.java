package com.dracoon.sdk.internal.auth;

import com.dracoon.sdk.DracoonAuth;

public class AuthHolder {

    private DracoonAuth mAuth;

    public AuthHolder() {}

    public AuthHolder(DracoonAuth auth) {
        mAuth = auth;
    }

    public DracoonAuth get() {
        return mAuth;
    }

    public void set(DracoonAuth auth) {
        mAuth = auth;
    }

}
