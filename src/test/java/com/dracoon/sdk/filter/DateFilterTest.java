package com.dracoon.sdk.filter;

import java.util.Date;

import com.dracoon.sdk.internal.util.DateUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateFilterTest {

    @SuppressWarnings("unused")
    private static abstract class BaseFilterTests {

        protected final String mName;

        protected BaseFilterTests(String name) {
            mName = name;
        }

        @Test
        void testLeFilterCorrect() {
            Date date = new Date();
            DateFilter filter = getBuilder()
                    .le(date)
                    .build();
            assertEquals(buildTestFilterString("le", date) , filter.toString());
        }

        @Test
        void testGeFilterCorrect() {
            Date date = new Date();
            DateFilter filter = getBuilder()
                    .ge(date)
                    .build();
            assertEquals(buildTestFilterString("ge", date) , filter.toString());
        }

        @Test
        void testLeAndGeFilterCorrect() {
            Date date1 = new Date();
            Date date2 = new Date();
            DateFilter filter = getBuilder()
                    .ge(date1)
                    .and()
                    .le(date2)
                    .build();
            assertEquals(buildTestFilterString("ge", date1, "le", date2) , filter.toString());
        }

        private String buildTestFilterString(String op, Date date) {
            return mName + ":" + op + ":" + DateUtils.formatDate(date);
        }

        private String buildTestFilterString(String op1, Date date1, String op2, Date date2) {
            return buildTestFilterString(op1, date1) + "|" + buildTestFilterString(op2, date2);
        }

        protected abstract DateFilter.Builder<?> getBuilder();

    }

    @Nested
    class CreatedAtFilterTests extends BaseFilterTests {

        CreatedAtFilterTests() {
            super("createdAt");
        }

        @Override
        protected DateFilter.Builder<?> getBuilder() {
            return new CreatedAtFilter.Builder();
        }

    }

    @Nested
    class ExpireAtFilterTests extends BaseFilterTests {

        ExpireAtFilterTests() {
            super("expireAt");
        }

        @Override
        protected DateFilter.Builder<?> getBuilder() {
            return new ExpireAtFilter.Builder();
        }

    }

    @Nested
    class UpdatedAtFilterTests extends BaseFilterTests {

        UpdatedAtFilterTests() {
            super("updatedAt");
        }

        @Override
        protected DateFilter.Builder<?> getBuilder() {
            return new UpdatedAtFilter.Builder();
        }

    }

}
