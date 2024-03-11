package com.dracoon.sdk;

import java.util.Objects;

import com.dracoon.sdk.internal.util.GsonCharArrayTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseTest {

    private static final String DATA_DIR = "data";
    private static final String FILES_DIR = "files";

    private static final Gson sGson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(GsonCharArrayTypeAdapter.TYPE, new GsonCharArrayTypeAdapter())
            .create();

    protected static byte[] readFile(String filePath) {
        return TestUtils.readFile(FILES_DIR + filePath);
    }

    protected static <T> T readData(Class<? extends T> clazz, String filePath) {
        return TestUtils.readData(clazz, DATA_DIR + filePath);
    }

    protected void assertDeepEquals(Object o1, Object o2) {
        String s1 = sGson.toJson(o1);
        String s2 = sGson.toJson(o2);
        assertEquals(s1, s2);
    }

    protected boolean deepEquals(Object o1, Object o2) {
        String s1 = sGson.toJson(o1);
        String s2 = sGson.toJson(o2);
        return Objects.equals(s1, s2);
    }

}
