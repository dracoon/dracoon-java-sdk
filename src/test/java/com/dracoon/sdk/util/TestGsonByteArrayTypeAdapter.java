package com.dracoon.sdk.util;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okio.ByteString;

public class TestGsonByteArrayTypeAdapter extends TypeAdapter<byte[]> {

    public static final Type TYPE = byte[].class;

    @Override
    public void write(JsonWriter out, byte[] bytes) throws IOException {
        String value = null;
        if (bytes != null) {
            value = ByteString.of(bytes).base64();
        }
        out.value(value);
    }

    @Override
    public byte[] read(JsonReader in) throws IOException {
        ByteString byteString = ByteString.decodeBase64(in.nextString());
        return byteString != null ? byteString.toByteArray() : null;
    }

}
