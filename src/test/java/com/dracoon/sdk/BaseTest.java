package com.dracoon.sdk;

public abstract class BaseTest {

    private static final String DATA_DIR = "data";
    private static final String FILES_DIR = "files";

    protected static byte[] readFile(String fileName) {
        return TestUtils.readFile(FILES_DIR + fileName);
    }

    protected static <T> T readData(Class<? extends T> clazz, String fileName) {
        return TestUtils.readData(clazz, DATA_DIR + fileName);
    }

}
