package com.dracoon.sdk.internal;

import com.dracoon.sdk.BaseHttpTest;
import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class DracoonRequestHandlerTest extends BaseHttpTest {

    private static final String USER_AGENT = "Java-SDK-Unit-Test";
    private static final String ACCESS_TOKEN = "L3O1eDsLxDgJhLaQbzOSmm8xr48mxPoW";

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

}
