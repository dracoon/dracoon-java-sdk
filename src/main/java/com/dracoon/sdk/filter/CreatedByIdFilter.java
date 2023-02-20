package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by creator's user ID.
 */
public class CreatedByIdFilter extends UserIdFilter {

    private static final String NAME = "createdById";

    private CreatedByIdFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link CreatedByIdFilter}.
     */
    public static class Builder extends UserIdFilter.Builder {

        public Builder() {
            super(new CreatedByIdFilter());
        }

    }

}
