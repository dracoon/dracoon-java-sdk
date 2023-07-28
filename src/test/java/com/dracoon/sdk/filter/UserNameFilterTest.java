package com.dracoon.sdk.filter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNameFilterTest {

    @SuppressWarnings("unused")
    private static abstract class BaseFilterTests {

        protected final String mName;

        protected BaseFilterTests(String name) {
            mName = name;
        }

        @Test
        void testEqFilterCorrect() {
            String name = "test";
            UserNameFilter filter = getBuilder()
                    .eq(name)
                    .build();
            assertEquals(buildTestFilterString("eq", name) , filter.toString());
        }

        @Test
        void testCnFilterCorrect() {
            String name = "tset";
            UserNameFilter filter = getBuilder()
                    .cn(name)
                    .build();
            assertEquals(buildTestFilterString("cn", name) , filter.toString());
        }

        private String buildTestFilterString(String op, String name) {
            return mName + ":" + op + ":" + name;
        }

        protected abstract UserNameFilter.Builder<?> getBuilder();

    }

    @Nested
    class CreatedByFilterTests extends BaseFilterTests {

        CreatedByFilterTests() {
            super("createdBy");
        }

        @Override
        protected UserNameFilter.Builder<?> getBuilder() {
            return new CreatedByFilter.Builder();
        }

    }

    @Nested
    class UpdatedByFilterTests extends BaseFilterTests {

        UpdatedByFilterTests() {
            super("updatedBy");
        }

        @Override
        protected UserNameFilter.Builder<?> getBuilder() {
            return new UpdatedByFilter.Builder();
        }

    }

}
