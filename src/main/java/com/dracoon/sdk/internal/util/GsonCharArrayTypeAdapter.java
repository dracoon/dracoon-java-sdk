package com.dracoon.sdk.internal.util;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonCharArrayTypeAdapter extends TypeAdapter<char[]> {

    public static final Type TYPE = char[].class;

    @Override
    public void write(JsonWriter out, char[] chars) throws IOException {
        String value = null;
        if (chars != null) {
            value = String.valueOf(chars);
        }
        out.value(value);
    }

    @Override
    public char[] read(JsonReader in) throws IOException {
        String value = in.nextString();
        return value != null ? value.toCharArray() : null;
    }

}
