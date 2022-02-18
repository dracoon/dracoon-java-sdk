package com.dracoon.sdk;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dracoon.sdk.model.HttpBody;
import com.dracoon.sdk.model.HttpHeader;
import com.dracoon.sdk.model.HttpRequest;
import com.dracoon.sdk.model.HttpResponse;
import com.dracoon.sdk.util.TestConsoleHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class BaseHttpTest extends BaseTest {

    private static final String HTTP_DIR = "http";

    private static final String HTTP_PROTOCOL = "http";

    protected MockWebServer mMockWebServer;

    private String mServerHostName;
    private int mServerPort;

    protected URL mServerUrl;

    @BeforeAll
    protected static void init() {
        Logger l = Logger.getLogger(MockWebServer.class.getName());
        l.setLevel(Level.WARNING);
        l.setUseParentHandlers(false);
        l.addHandler(new TestConsoleHandler());
    }

    @BeforeEach
    protected void setup() throws Exception {
        mMockWebServer = new MockWebServer();
        mMockWebServer.start();

        mServerHostName = mMockWebServer.getHostName();
        mServerPort = mMockWebServer.getPort();

        mServerUrl = new URL(buildServerUrl(HTTP_PROTOCOL, mServerHostName, mServerPort));
    }

    @AfterEach
    protected void tearDown() throws IOException {
        mMockWebServer.shutdown();
    }

    protected void enqueueResponse(String name) {
        mMockWebServer.enqueue(createMockResponse(name));
    }

    private MockResponse createMockResponse(String name) {
        // Read response data
        HttpResponse data = TestUtils.readData(HttpResponse.class, HTTP_DIR + name);

        // Create HTTP response with specific status code
        MockResponse response = new MockResponse().setResponseCode(data.status);
        // Add headers
        if (data.headers != null) {
            for (HttpHeader header : data.headers) {
                response.addHeader(header.name, header.value);
            }
        }
        // Add body
        if (data.body != null) {
            // Text content
            if (data.body.type == HttpBody.Type.TEXT) {
                response.setBody(data.body.content);
            // Binary content
            } else if (data.body.type == HttpBody.Type.BASE64) {
                Base64.Decoder decoder = Base64.getDecoder();
                Buffer buffer = new Buffer();
                buffer.write(decoder.decode(data.body.content));
                response.setBody(buffer);
            // File reference
            } else if (data.body.type == HttpBody.Type.FILE) {
                Buffer buffer = new Buffer();
                buffer.write(TestUtils.readFile(HTTP_DIR + data.body.content));
                response.setBody(buffer);
            }
        }
        // Return created response
        return response;
    }

    protected void dropRequest() throws InterruptedException {
        mMockWebServer.takeRequest();
    }

    protected void checkRequest(String name) throws InterruptedException {
        checkRecordedRequest(name, mMockWebServer.takeRequest());
    }

    private void checkRecordedRequest(String name, RecordedRequest request) {
        // Read request data
        HttpRequest data = TestUtils.readData(HttpRequest.class, HTTP_DIR + name);

        // Get request URL
        URL url;
        try {
            url = new URL(data.url);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not parse request URL in file '%s'!",
                    HTTP_DIR + name), e);
        }

        // Check HTTP request
        assertNotNull(request);
        // Check method
        assertEquals(data.method.name(), request.getMethod());
        // Check path
        assertEquals(url.getPath(), request.getRequestUrl().encodedPath());
        // Check query
        assertEquals(url.getQuery(), request.getRequestUrl().query());
        // Check headers
        if (data.headers != null) {
            for (HttpHeader header : data.headers) {
                String value = request.getHeader(header.name);
                assertEquals(header.value, value);
            }
        }
        // Check body
        if (data.body != null) {
            Buffer buffer = request.getBody();
            // Text content
            if (data.body.type == HttpBody.Type.TEXT) {
                String expectedContent = data.body.content;
                String actualContent = buffer.readString(StandardCharsets.UTF_8);
                assertEquals(expectedContent, actualContent);
            // Binary content
            } else if (data.body.type == HttpBody.Type.BASE64) {
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] expectedContent = decoder.decode(data.body.content);
                byte[] actualContent = buffer.readByteArray();
                assertEquals(expectedContent, actualContent);
            // File reference
            } else if (data.body.type == HttpBody.Type.FILE) {
                byte[] expectedContent = TestUtils.readFile(HTTP_DIR + data.body.content);
                byte[] actualContent = buffer.readByteArray();
                assertEquals(expectedContent, actualContent);
            }
        }
    }

    private static String buildServerUrl(String protocol, String hostName, int port) {
        return protocol + "://" + hostName + ":" + port;
    }

}
