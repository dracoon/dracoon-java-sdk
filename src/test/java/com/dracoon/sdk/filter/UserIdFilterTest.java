package com.dracoon.sdk.filter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserIdFilterTest {

    @SuppressWarnings("unused")
    private static abstract class BaseFilterTests {

        protected final String mName;

        protected BaseFilterTests(String name) {
            mName = name;
        }

        @Test
        void testEqFilterCorrect() {
            long id = 5L;
            UserIdFilter filter = getBuilder()
                    .eq(id)
                    .build();
            assertEquals(buildTestFilterString("eq", id) , filter.toString());
        }

        private String buildTestFilterString(String op, long id) {
            return mName + ":" + op + ":" + id;
        }

        protected abstract UserIdFilter.Builder<?> getBuilder();

    }

    @Nested
    class CreatedByIdFilterTests extends BaseFilterTests {

        CreatedByIdFilterTests() {
            super("createdById");
        }

        @Override
        protected UserIdFilter.Builder<?> getBuilder() {
            return new CreatedByIdFilter.Builder();
        }

    }

    @Nested
    class UpdatedByIdFilterTests extends BaseFilterTests {

        UpdatedByIdFilterTests() {
            super("updatedById");
        }

        @Override
        protected UserIdFilter.Builder<?> getBuilder() {
            return new UpdatedByIdFilter.Builder();
        }

    }

}
