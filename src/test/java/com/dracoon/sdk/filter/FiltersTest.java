package com.dracoon.sdk.filter;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FiltersTest {

    private static abstract class BaseFiltersTests {

        protected static void assertFilterAddedCorrectly(Filters filters, Filter<?>... addedFilters) {
            String expectedFilterString = Arrays.stream(addedFilters)
                    .map(Filter::toString)
                    .collect(Collectors.joining("|"));
            assertEquals(expectedFilterString, filters.toString());
        }

    }

    @Nested
    class GetDownloadSharesFilterTests extends BaseFiltersTests {

        GetDownloadSharesFilter mFilters;

        @BeforeEach
        void setup() {
            mFilters = new GetDownloadSharesFilter();
        }

        @Test
        void testCreatedByFilterAddedCorrectly() {
            CreatedByFilter filter = new CreatedByFilter.Builder().eq("user").build();
            mFilters.addCreatedByFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testCreatedByIdFilterAddedCorrectly() {
            CreatedByIdFilter filter = new CreatedByIdFilter.Builder().eq(5L).build();
            mFilters.addCreatedByIdFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testNodeIdFilterAddedCorrectly() {
            NodeIdFilter filter = new NodeIdFilter.Builder().eq(3L).build();
            mFilters.addNodeIdFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testFiltersAddedCorrectly() {
            CreatedByFilter filter1 = new CreatedByFilter.Builder().eq("user").build();
            mFilters.addCreatedByFilter(filter1);
            NodeIdFilter filter2 = new NodeIdFilter.Builder().eq(3L).build();
            mFilters.addNodeIdFilter(filter2);
            assertFilterAddedCorrectly(mFilters, filter1, filter2);
        }

    }

    @Nested
    class GetUploadSharesFilterTests extends BaseFiltersTests {

        GetUploadSharesFilter mFilters;

        @BeforeEach
        void setup() {
            mFilters = new GetUploadSharesFilter();
        }

        @Test
        void testCreatedByFilterAddedCorrectly() {
            CreatedByFilter filter = new CreatedByFilter.Builder().eq("user").build();
            mFilters.addCreatedByFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testCreatedByIdFilterAddedCorrectly() {
            CreatedByIdFilter filter = new CreatedByIdFilter.Builder().eq(5L).build();
            mFilters.addCreatedByIdFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testNodeIdFilterAddedCorrectly() {
            NodeIdFilter filter = new NodeIdFilter.Builder().eq(3L).build();
            mFilters.addNodeIdFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testShareNameFilterAddedCorrectly() {
            ShareNameFilter filter = new ShareNameFilter.Builder().cn("share").build();
            mFilters.addNameFilter(filter);
            assertFilterAddedCorrectly(mFilters, filter);
        }

        @Test
        void testFiltersAddedCorrectly() {
            CreatedByFilter filter1 = new CreatedByFilter.Builder().eq("user").build();
            mFilters.addCreatedByFilter(filter1);
            ShareNameFilter filter2 = new ShareNameFilter.Builder().cn("share").build();
            mFilters.addNameFilter(filter2);
            assertFilterAddedCorrectly(mFilters, filter1, filter2);
        }

    }

}
