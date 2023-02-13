package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by update date. (Time information is ignored.)
 */
public class UpdatedAtFilter extends DateFilter {

    private static final String NAME = "updatedAt";

    private UpdatedAtFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link UpdatedAtFilter}.
     */
    public static class Builder extends DateFilter.Builder {

        public Builder() {
            super(new UpdatedAtFilter());
        }

    }

}
