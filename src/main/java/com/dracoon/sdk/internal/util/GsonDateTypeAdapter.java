package com.dracoon.sdk.internal.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonDateTypeAdapter extends TypeAdapter<Date> {

    public static final Type TYPE = Date.class;

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(DateUtils.formatTime(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        return DateUtils.parseTime(in.nextString());
    }

}
