package com.dracoon.sdk.internal;

import java.net.URL;

import com.dracoon.sdk.BaseHttpTest;
import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.Log;
import com.dracoon.sdk.TestLogger;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.auth.AuthHolder;
import com.dracoon.sdk.internal.auth.AuthInterceptorImpl;
import com.dracoon.sdk.internal.auth.AuthInterceptor;
import com.dracoon.sdk.internal.auth.AuthTokenRefresher;
import com.dracoon.sdk.internal.api.DracoonApi;
import com.dracoon.sdk.internal.api.DracoonApiBuilder;
import com.dracoon.sdk.internal.api.DracoonErrorParser;
import com.dracoon.sdk.internal.http.HttpClientBuilder;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class BaseApiTest extends BaseHttpTest {

    private static final String USER_AGENT = "Java-SDK-Unit-Test";
    private static final String ACCESS_TOKEN = "L3O1eDsLxDgJhLaQbzOSmm8xr48mxPoW";

    @FunctionalInterface
    protected interface ErrorParserFunction {
        DracoonApiCode apply(Response response);
    }

    protected final Log mLog = new TestLogger();
    protected final DracoonAuth mAuth = new DracoonAuth(ACCESS_TOKEN);
    protected final DracoonHttpConfig mHttpConfig = createTestHttpConfig();

    protected OkHttpClient mHttpClient;
    protected TestHttpHelper mHttpHelper;
    protected DracoonApi mDracoonApi;

    @Mock
    protected DracoonErrorParser mDracoonErrorParser;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mHttpClient = createHttpClient(mHttpConfig);
        mHttpHelper = createTestHttpHelper();
        mDracoonApi = createDracoonApi(mServerUrl, mHttpClient, mAuth);
    }

    protected static void assertDracoonApiException(Exception thrown, DracoonApiCode code) {
        Throwable cause = thrown.getCause();
        assertInstanceOf(DracoonApiException.class, cause);
        DracoonApiException exception = (DracoonApiException) cause;
        assertEquals(code, exception.getCode());
    }

    protected void mockParseStandardError(DracoonApiCode code) {
        when(mDracoonErrorParser.parseStandardError(any(retrofit2.Response.class)))
                .thenReturn(code);
    }

    protected void mockParseError(ErrorParserFunction func, DracoonApiCode code) {
        when(func.apply(any(retrofit2.Response.class)))
                .thenReturn(code);
    }

    private static DracoonHttpConfig createTestHttpConfig() {
        DracoonHttpConfig httpConfig = new DracoonHttpConfig();
        httpConfig.setUserAgent(USER_AGENT);
        httpConfig.setConnectTimeout(1);
        httpConfig.setReadTimeout(1);
        httpConfig.setWriteTimeout(1);
        return httpConfig;
    }

    private static OkHttpClient createHttpClient(DracoonHttpConfig httpConfig) {
        return new HttpClientBuilder().build(httpConfig);
    }

    private static TestHttpHelper createTestHttpHelper() {
        TestHttpHelper httpHelper = new TestHttpHelper();
        httpHelper.init();
        return httpHelper;
    }

    private static DracoonApi createDracoonApi(URL serverUrl, OkHttpClient httpClient,
            DracoonAuth auth) {
        AuthHolder authHolder = new AuthHolder(auth);
        AuthTokenRefresher authTokenRefresher = () -> {};
        AuthInterceptor authInterceptor = new AuthInterceptorImpl(authHolder, authTokenRefresher);
        return new DracoonApiBuilder().build(serverUrl, httpClient, authInterceptor);
    }

}
