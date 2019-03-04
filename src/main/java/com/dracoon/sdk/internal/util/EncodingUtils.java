package com.dracoon.sdk.internal.util;

import okio.ByteString;

public class EncodingUtils {

    public static String encodeBase64(byte[] data) {
        return ByteString.of(data).base64();
    }

    public static byte[] decodeBase64(String data) {
        ByteString byteString = ByteString.decodeBase64(data);
        return byteString != null ? byteString.toByteArray() : null;
    }
}
