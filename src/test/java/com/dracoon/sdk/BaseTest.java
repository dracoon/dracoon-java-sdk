package com.dracoon.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseTest {

    private static final String DATA_DIR = "data";
    private static final String FILES_DIR = "files";

    private static final Gson sGson = new GsonBuilder().disableHtmlEscaping().create();

    protected static byte[] readFile(String fileName) {
        return TestUtils.readFile(FILES_DIR + fileName);
    }

    protected static <T> T readData(Class<? extends T> clazz, String fileName) {
        return TestUtils.readData(clazz, DATA_DIR + fileName);
    }

    protected void assertDeepEquals(Object o1, Object o2) {
        String s1 = sGson.toJson(o1);
        String s2 = sGson.toJson(o2);
        assertEquals(s1, s2);
    }

}
