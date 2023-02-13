package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by expire date. (Time information is ignored.)
 */
public class ExpireAtFilter extends DateFilter {

    private static final String NAME = "expireAt";

    private ExpireAtFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link ExpireAtFilter}.
     */
    public static class Builder extends DateFilter.Builder {

        public Builder() {
            super(new ExpireAtFilter());
        }

    }

}
