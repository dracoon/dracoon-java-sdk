package com.dracoon.sdk.internal;

import com.dracoon.sdk.BaseHttpTest;
import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.TestLogger;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
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
public abstract class DracoonRequestHandlerTest extends BaseHttpTest {

    private static final String USER_AGENT = "Java-SDK-Unit-Test";
    private static final String ACCESS_TOKEN = "L3O1eDsLxDgJhLaQbzOSmm8xr48mxPoW";

    @FunctionalInterface
    protected interface Executable {
        void execute() throws Exception;
    }

    @FunctionalInterface
    protected interface ErrorParserFunction {
        DracoonApiCode apply(Response response);
    }

    protected DracoonClientImplMock mDracoonClientImpl;

    @Mock
    protected DracoonErrorParser mDracoonErrorParser;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        DracoonHttpConfig httpConfig = new DracoonHttpConfig();
        httpConfig.setUserAgent(USER_AGENT);
        httpConfig.setConnectTimeout(1);
        httpConfig.setReadTimeout(1);
        httpConfig.setWriteTimeout(1);

        mDracoonClientImpl = new DracoonClientImplMock(mServerUrl);
        mDracoonClientImpl.setHttpConfig(httpConfig);
        mDracoonClientImpl.setAuth(new DracoonAuth(ACCESS_TOKEN));
        mDracoonClientImpl.init();
        mDracoonClientImpl.setDracoonErrorParser(mDracoonErrorParser);
        mDracoonClientImpl.setLog(new TestLogger());
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

}
