package com.dracoon.sdk.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    private StreamUtils() {

    }

    public static void closeStream(InputStream is) {
        if (is == null) {
            return;
        }

        try {
            is.close();
        } catch (IOException e) {
            // Nothing to do here
        }
    }

    public static void closeStream(OutputStream os) {
        if (os == null) {
            return;
        }

        try {
            os.close();
        } catch (IOException e) {
            // Nothing to do here
        }
    }

}
