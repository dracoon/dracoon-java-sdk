package com.dracoon.sdk.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TestGsonDateTypeAdapter extends TypeAdapter<Date> {

    public static final Type TYPE = Date.class;

    @Override
    public void write(JsonWriter out, Date date) throws IOException {
        String value = null;
        if (date != null) {
            value = createDateFormat().format(date);
        }
        out.value(value);
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        String value = in.nextString();
        if (value == null) {
            return null;
        }

        try {
            return createDateFormat().parse(value);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    private static DateFormat createDateFormat() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

}
