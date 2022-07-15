package com.dracoon.sdk;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TestUtils {

    private static final int BUFFER_SIZE = 1024;

    private static class DateTypeAdapter extends TypeAdapter<Date> {
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

    private static final Gson sGson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

    private TestUtils() {

    }

    public static <T> T readData(Class<? extends T> clazz, String fileName) {
        try {
            byte[] data = readFile(fileName);
            String s = new String(data);
            return sGson.fromJson(s, clazz);
        } catch (JsonParseException e) {
            throw new RuntimeException(String.format("Could not parse test resource file '%s'!",
                    fileName), e);
        }
    }

    public static <T> T readData(Class<? extends T> clazz, String fileName,
            Map<String, String> replacements) {
        try {
            byte[] data = readFile(fileName);
            String s = new String(data);
            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                s = s.replace("[" + replacement.getKey() + "]", replacement.getValue());
            }
            return sGson.fromJson(s, clazz);
        } catch (JsonParseException e) {
            throw new RuntimeException(String.format("Could not parse test resource file '%s'!",
                    fileName), e);
        }
    }

    public static byte[] readFile(String fileName) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = open(fileName);
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not read test resource file '%s'!",
                    fileName), e);
        } finally {
            close(os);
            close(is);
        }
    }

    private static InputStream open(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null!");
        }

        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new RuntimeException(String.format("Could not find test resource file '%s'!",
                    fileName));
        }

        return is;
    }

    private static void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // Nothing to do here
        }
    }

}
