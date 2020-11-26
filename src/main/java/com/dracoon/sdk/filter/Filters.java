package com.dracoon.sdk.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all filter lists.<br>
 * <br>
 * Each filter in the filter list is concatenated via "and".
 */
public abstract class Filters {

    private static final String SEPARATOR = "|";

    protected final List<Filter> mFilters = new ArrayList<>();

    /**
     * Validates a filter which will be added to the list. It checks if the filter is not
     * <code>null</code> and is not added twice.
     *
     * @param filter The filter.
     */
    protected void validateSingleFilter(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        for (Filter existingFilter : mFilters) {
            if (existingFilter.getClass().equals(filter.getClass())) {
                throw new IllegalArgumentException("Filter cannot added twice.");
            }
        }
    }

    /**
     * Validates a filter which will be added to the list. It checks if the filter is not
     * <code>null</code>.
     *
     * @param filter The filter.
     */
    protected void validateMultiFilter(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null.");
        }
    }

    /**
     * Returns the string representation of the filter list.<br>
     * <br>
     * String syntax: FILTER_1[|...|FILTER_N]
     *
     * @return the string representation of the filter list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mFilters.size(); i++) {
            sb.append(mFilters.get(i));
            sb.append(i < mFilters.size() - 1 ? SEPARATOR : "");
        }

        return sb.toString();
    }

}
