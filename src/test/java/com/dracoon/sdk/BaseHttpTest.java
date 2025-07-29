package com.dracoon.sdk;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dracoon.sdk.model.http.Body;
import com.dracoon.sdk.model.http.Header;
import com.dracoon.sdk.model.http.SavedRequest;
import com.dracoon.sdk.model.http.SavedResponse;
import com.dracoon.sdk.util.TestConsoleHandler;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import mockwebserver3.SocketEffect;
import okio.Buffer;
import okio.ByteString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseHttpTest extends BaseTest {

    private static final String HTTP_DIR = "http";

    private static final String HTTP_PROTOCOL = "http";

    private static final int MULTIPART_PREFIX_LEN = 40;
    private static final int MULTIPART_SUFFIX_LEN = 44;

    private MockWebServer mMockWebServer;

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
        // Start Mock Web Server
        mMockWebServer = new MockWebServer();
        mMockWebServer.start();

        // Get hostname and port
        mServerHostName = mMockWebServer.getHostName();
        mServerPort = mMockWebServer.getPort();

        // Build server URL which is used in tests
        mServerUrl = new URL(buildServerUrl(HTTP_PROTOCOL, mServerHostName, mServerPort));
    }

    @AfterEach
    protected void tearDown() throws IOException {
        // Clear interrupted flag to allow closure of Mock Web Server
        Thread.interrupted();
        // Close Mock Web Server
        mMockWebServer.close();
    }

    protected void enqueueResponse(String name) {
        MockResponse.Builder responseBuilder = createMockResponseBuilder(name);
        mMockWebServer.enqueue(responseBuilder.build());
    }

    protected void enqueueIOErrorResponse(String name, long delay) {
        MockResponse.Builder responseBuilder = createMockResponseBuilder(name);
        responseBuilder.bodyDelay(delay, TimeUnit.MILLISECONDS);
        responseBuilder.onResponseBody(new SocketEffect.CloseSocket());
        mMockWebServer.enqueue(responseBuilder.build());
    }

    private MockResponse.Builder createMockResponseBuilder(String name) {
        // Create replacements
        Map<String, String> replacements = new HashMap<>();
        replacements.put("SERVER_URL", buildServerUrl(HTTP_PROTOCOL, mServerHostName, mServerPort));

        // Read stored response
        SavedResponse storedResponse = TestUtils.readData(SavedResponse.class, HTTP_DIR + name, replacements);

        // Create HTTP response builder
        MockResponse.Builder mockResponseBuilder = new MockResponse.Builder();

        // Set status code
        mockResponseBuilder.code(storedResponse.status);
        // Add headers
        if (storedResponse.headers != null) {
            for (Header header : storedResponse.headers) {
                mockResponseBuilder.addHeader(header.name, header.value);
            }
        }
        // Add body
        if (storedResponse.body != null) {
            // Text content
            if (storedResponse.body.type == Body.Type.TEXT) {
                mockResponseBuilder.body(storedResponse.body.content);
            // Binary content
            } else if (storedResponse.body.type == Body.Type.BASE64) {
                Base64.Decoder decoder = Base64.getDecoder();
                Buffer buffer = new Buffer();
                buffer.write(decoder.decode(storedResponse.body.content));
                mockResponseBuilder.body(buffer);
            // File reference
            } else if (storedResponse.body.type == Body.Type.FILE) {
                Buffer buffer = new Buffer();
                buffer.write(TestUtils.readFile(HTTP_DIR + storedResponse.body.content));
                mockResponseBuilder.body(buffer);
            // Part file reference
            } else if (storedResponse.body.type == Body.Type.PART_FILE) {
                throw new RuntimeException(String.format("Body type 'part-file' is not allowed " +
                        "in file '%s'!", HTTP_DIR + name));
            }
        }

        // Return created response builder
        return mockResponseBuilder;
    }

    protected void dropRequest() throws InterruptedException {
        mMockWebServer.takeRequest();
    }

    protected void checkRequest(String name) throws InterruptedException {
        checkRecordedRequest(name, mMockWebServer.takeRequest());
    }

    private void checkRecordedRequest(String name, RecordedRequest recordedRequest) {
        // Create replacements
        Map<String, String> replacements = new HashMap<>();
        replacements.put("SERVER_URL", buildServerUrl(HTTP_PROTOCOL, mServerHostName, mServerPort));

        // Read stored request
        SavedRequest savedRequest = TestUtils.readData(SavedRequest.class, HTTP_DIR + name,
                replacements);

        // Get request URL
        URL storedUrl;
        try {
            storedUrl = new URL(savedRequest.url);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not parse request URL in file '%s'!",
                    HTTP_DIR + name), e);
        }

        // Check HTTP request
        assertNotNull(recordedRequest, "Request is null.");
        // Check method
        assertEquals(savedRequest.method.name(), recordedRequest.getMethod(),
                "Request method does not match!");
        // Check protocol
        assertEquals(HTTP_PROTOCOL, recordedRequest.getUrl().scheme(),
                "Request protocol does not match!");
        // Check host/port
        assertEquals(mServerHostName, recordedRequest.getUrl().host(),
                "Request hostname does not match!");
        assertEquals(mServerPort, recordedRequest.getUrl().port(),
                "Request port does not match!");
        // Check path
        assertEquals(storedUrl.getPath(), recordedRequest.getUrl().encodedPath(),
                "Request path does not match!");
        // Check query
        assertEquals(storedUrl.getQuery(), recordedRequest.getUrl().query(),
                "Request query does not match");
        // Check headers
        if (savedRequest.headers != null) {
            for (Header header : savedRequest.headers) {
                String value = recordedRequest.getHeaders().get(header.name);
                assertEquals(header.value, value);
            }
        }
        // Check body
        if (savedRequest.body != null) {
            // Text content
            if (savedRequest.body.type == Body.Type.TEXT) {
                checkRecordedRequestTextBody(savedRequest, recordedRequest);
            // Binary content
            } else if (savedRequest.body.type == Body.Type.BASE64) {
                checkRecordedRequestBase64Body(savedRequest, recordedRequest);
            // File reference
            } else if (savedRequest.body.type == Body.Type.FILE) {
                checkRecordedRequestFileBody(savedRequest, recordedRequest);
            // Part file reference
            } else if ((savedRequest.body.type == Body.Type.PART_FILE)) {
                checkRecordedRequestPartFileBody(savedRequest, recordedRequest);
            }
        }
    }

    private static String buildServerUrl(String protocol, String hostName, int port) {
        return protocol + "://" + hostName + ":" + port;
    }

    private static void checkRecordedRequestTextBody(SavedRequest savedRequest,
            RecordedRequest recordedRequest) {
        String expected = savedRequest.body.content;
        String actual = getRecordedRequestBodyString(recordedRequest);
        assertEquals(expected, actual, "Request content does not match!");
    }

    private static void checkRecordedRequestBase64Body(SavedRequest savedRequest,
            RecordedRequest recordedRequest) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] expectedBytes = decoder.decode(savedRequest.body.content);
        byte[] actualBytes = getRecordedRequestBodyByteArray(recordedRequest);
        assertArrayEquals(expectedBytes, actualBytes, "Request content does not match!");
    }

    private static void checkRecordedRequestFileBody(SavedRequest savedRequest,
            RecordedRequest recordedRequest) {
        byte[] expectedBytes = TestUtils.readFile(HTTP_DIR + savedRequest.body.content);
        byte[] actualBytes = getRecordedRequestBodyByteArray(recordedRequest);
        assertArrayEquals(expectedBytes, actualBytes, "Request content does not match!");
    }

    private static void checkRecordedRequestPartFileBody(SavedRequest savedRequest,
            RecordedRequest recordedRequest) {
        byte[] recordedRequestBodyBytes = getRecordedRequestBodyByteArray(recordedRequest);

        // Check request body length
        assertTrue(recordedRequestBodyBytes.length > MULTIPART_PREFIX_LEN + MULTIPART_SUFFIX_LEN,
                "Request content length is too short.");

        // Get multi-part bytes
        byte[] expectedPartBytes = TestUtils.readFile(HTTP_DIR + savedRequest.body.content);
        Buffer expectedBuffer = new Buffer().write(expectedPartBytes);
        byte[] actualPartBytes = Arrays.copyOfRange(recordedRequestBodyBytes, MULTIPART_PREFIX_LEN,
                recordedRequestBodyBytes.length - MULTIPART_SUFFIX_LEN);
        Buffer actualBuffer = new Buffer().write(actualPartBytes);

        // Check multi-part header
        try {
            assertEquals(expectedBuffer.readUtf8Line(), actualBuffer.readUtf8Line(),
                    "Request multi-part content-disposition is invalid!");
            assertEquals(expectedBuffer.readUtf8Line(), actualBuffer.readUtf8Line(),
                    "Request multi-part content-type is invalid!");
            expectedBuffer.readUtf8Line();
            actualBuffer.readUtf8Line();
        } catch (EOFException e) {
            fail("Request multi-part header is invalid!");
        }

        // Check multi-part body
        assertArrayEquals(expectedBuffer.readByteArray(), actualBuffer.readByteArray(),
                "Request multi-part content does not match!");
    }

    private static String getRecordedRequestBodyString(RecordedRequest recordedRequest) {
        ByteString body = recordedRequest.getBody();
        return body != null ? body.string(StandardCharsets.UTF_8) : "";
    }

    private static byte[] getRecordedRequestBodyByteArray(RecordedRequest recordedRequest) {
        ByteString body = recordedRequest.getBody();
        return body != null ? body.toByteArray() : new byte[]{};
    }

}
