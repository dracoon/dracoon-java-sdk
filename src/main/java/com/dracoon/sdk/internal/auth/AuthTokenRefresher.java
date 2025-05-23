package com.dracoon.sdk.internal.auth;

import java.io.IOException;

public interface AuthTokenRefresher {

    void refresh() throws IOException;

}
