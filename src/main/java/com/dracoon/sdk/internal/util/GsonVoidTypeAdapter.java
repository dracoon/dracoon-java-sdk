package com.dracoon.sdk.internal.util;

import java.lang.reflect.Type;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonVoidTypeAdapter extends TypeAdapter<Void> {

    public static final Type TYPE = Void.class;

    @Override
    public void write(JsonWriter out, Void value) {
        // SONAR: Empty method body is intentional
    }

    @Override
    public Void read(JsonReader in) {
        return null;
    }

}
