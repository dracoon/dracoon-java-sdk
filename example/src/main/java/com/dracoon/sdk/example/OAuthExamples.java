package com.dracoon.sdk.example;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.OAuthHelper;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.sun.net.httpserver.HttpServer;

/**
 * This class shows the OAuth authorization of a desktop application via the Authorization Code
 * flow. The example uses a local TCP port to receive the callback.<br>
 * <br>
 * Notice: The logic to generate the state and to receive the callback is kept as short as possible.
 *         For a production application the generation of the state, the usage of Sun's HTTP server
 *         as well as the blocking queue might not be the best solution. Furthermore, error handling
 *         is ignored.
 */
public class OAuthExamples {

    private static final String SERVER_URL = "https://dracoon.team";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";

    private static final int REDIRECT_PORT = 10000;

    public static void main(String[] args) throws Exception {
        // Authorize client
        String authCode = authorizeClient();

        // Create client
        DracoonClient client = createClient(authCode);

        // Use client
        useClient(client);
    }

    private static String authorizeClient() throws MalformedURLException, InterruptedException,
            DracoonException {
        // Generate state
        String state = generateState();

        // Create authorization URL
        String authUrl = OAuthHelper.createAuthorizationUrl(new URL(SERVER_URL), CLIENT_ID, state);

        // Open authorization URL in user's browser and wait for callback
        URI redirUrl = openUrlInBrowser(authUrl);

        // Extract state and code from callback URI
        String callbackState = OAuthHelper.extractAuthorizationStateFromUri(redirUrl);
        String callbackCode = OAuthHelper.extractAuthorizationCodeFromUri(redirUrl);

        // Check state
        if (!callbackState.equals(state)) {
            throw new Error("State does not match!");
        }

        return callbackCode;
    }

    private static String generateState() {
        return Integer.toHexString(new SecureRandom().nextInt());
    }

    private static URI openUrlInBrowser(String url) throws InterruptedException {
        // Queue to store callback data
        final BlockingQueue<URI> callbackQueue = new LinkedBlockingQueue<>();

        // Open local TCP port to receive callback
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(REDIRECT_PORT), 0);
            server.createContext("/", exchange -> {
                try {
                    // Store callback data
                    callbackQueue.put(exchange.getRequestURI());

                    // Write response
                    String message = "Authorization completed.";
                    byte[] data = message.getBytes();
                    exchange.sendResponseHeaders(200, data.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(data);
                    os.close();
                } catch (InterruptedException e) {
                    // Nothing to do here
                }
            });
            server.start();
        } catch (IOException e) {
            throw new Error("Could not open redirect socket.");
        }

        // Open browser
        boolean wasBrowserOpened = false;
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                wasBrowserOpened = true;
            } catch (URISyntaxException e) {
                throw new Error("Invalid authorization URL!");
            } catch (IOException e) {
                // Nothing to do here
            }
        }
        if (!wasBrowserOpened) {
            System.out.println("Authorization URL could not be opened in browser!");
            System.out.println("Please open following URL: " + url);
        }

        // Wait for callback data
        return callbackQueue.take();
    }

    private static DracoonClient createClient(String authCode) throws MalformedURLException {
        // Create authorization configuration with obtained authorization code
        DracoonAuth auth = new DracoonAuth(CLIENT_ID, CLIENT_SECRET, authCode);

        // Create client and supply authorization configuration
        DracoonClient client = new DracoonClient.Builder(new URL(SERVER_URL))
                .log(new Logger(Logger.DEBUG))
                .auth(auth)
                .build();

        return client;
    }

    private static void useClient(DracoonClient client) throws DracoonException {
        long parentNodeId = 0L;

        NodeList nodeList = client.nodes().getNodes(parentNodeId);
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

}
