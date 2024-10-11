package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonAuth;

class AuthHolder {

    private DracoonAuth mAuth;

    DracoonAuth get() {
        return mAuth;
    }

    void set(DracoonAuth auth) {
        mAuth = auth;
    }

}
