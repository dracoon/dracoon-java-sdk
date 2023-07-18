package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by creation date. (Time information is ignored.)
 */
public class CreatedAtFilter extends DateFilter {

    private static final String NAME = "createdAt";

    private CreatedAtFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link CreatedAtFilter}.
     */
    public static class Builder extends DateFilter.Builder<CreatedAtFilter> {

        public Builder() {
            super(new CreatedAtFilter());
        }

    }

}
