package com.dracoon.sdk;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

class ExampleHttpTest extends BaseHttpTest {

    @Test
    void test() throws InterruptedException, DracoonApiException, DracoonNetIOException {
        MockResponse response1 = createMockResponse("/example/get_version_response.json");
        sServer.enqueue(response1);

        DracoonClient client = new DracoonClient.Builder(sServerUrl)
                .build();

        RecordedRequest request1 = sServer.takeRequest();
        checkRecordedRequest("/example/get_version_request.json", request1);
    }

}
