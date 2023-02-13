package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by creator's user name.
 */
public class CreatedByFilter extends UserNameFilter {

    private static final String NAME = "createdBy";

    private CreatedByFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link CreatedByFilter}.
     */
    public static class Builder extends UserNameFilter.Builder {

        public Builder() {
            super(new CreatedByFilter());
        }

    }

}
