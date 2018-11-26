package com.dracoon.sdk.example;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.dracoon.sdk.DracoonAuth;
import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.DracoonHttpConfig;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

public class OkHttpInterceptorExamples {

    private static final String LOG_TAG = OkHttpInterceptorExamples.class.getSimpleName();

    private static final String SERVER_URL = "https://dracoon.team";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String ENCRYPTION_PASSWORD = "encryption-password";

    private static class ExampleInterceptor implements Interceptor {

        private SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        private Logger mLogger;

        private int mCount = 0;

        public ExampleInterceptor(Logger logger) {
            mLogger = logger;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            int cnt = mCount++;

            Request req = chain.request();
            log(req, cnt);

            Response res = chain.proceed(req);
            log(res, cnt);

            return res;
        }

        public void log(Request request, int i) {
            String time = tf.format(new Date());

            StringBuilder sb = new StringBuilder();
            sb.append(time).append(": Request: ").append("(").append(i).append(")\n");
            sb.append(request.method()).append(": ").append(request.url()).append("\n");
            if (request.headers().size() > 0) {
                sb.append("Headers:").append("\n");
            }
            for (String name : request.headers().names()) {
                sb.append(name).append(": ").append(request.header(name)).append("\n");
            }
            if (request.body() != null) {
                sb.append("Body:").append("\n");
                try {
                    Request copy = request.newBuilder().build();
                    Buffer buffer = new Buffer();
                    copy.body().writeTo(buffer);
                    sb.append(buffer.readUtf8());
                } catch (IOException e) {
                    throw new RuntimeException("Unable to copy request body.", e);
                }
                sb.append("\n");
            }

            mLogger.d(LOG_TAG, sb.toString());
        }

        public void log(Response response, int i) {
            String time = tf.format(new Date());

            StringBuilder sb = new StringBuilder();
            sb.append(time).append(": Response: ").append("(").append(i).append(")\n");
            sb.append(response.code()).append("\n");
            if (response.body() != null) {
                sb.append("Body:").append("\n");
                try {
                    BufferedSource source = response.body().source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    sb.append(buffer.clone().readUtf8());
                    sb.append("\n");
                } catch (IOException e) {
                    throw new RuntimeException("Unable to clone response body.", e);
                }
            }

            mLogger.d(LOG_TAG, sb.toString());
        }

    }

    public static void main(String[] args) throws Exception {
        DracoonAuth auth = new DracoonAuth(ACCESS_TOKEN);

        Logger logger = new Logger(Logger.DEBUG);

        DracoonHttpConfig config = new DracoonHttpConfig();
        config.addOkHttpApplicationInterceptor(new ExampleInterceptor(logger));

        DracoonClient client = new DracoonClient.Builder(new URL(SERVER_URL))
                .log(logger)
                .auth(auth)
                .encryptionPassword(ENCRYPTION_PASSWORD)
                .httpConfig(config)
                .build();

        long parentNodeId = 0L;

        NodeList nodeList = client.nodes().getNodes(parentNodeId);
        for (Node node : nodeList.getItems()) {
            System.out.println(node.getId() + ": " + node.getParentPath() + node.getName());
        }
    }

}
