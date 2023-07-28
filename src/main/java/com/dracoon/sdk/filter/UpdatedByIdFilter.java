package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by updater's user ID.
 */
public class UpdatedByIdFilter extends UserIdFilter {

    private static final String NAME = "updatedById";

    private UpdatedByIdFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link UpdatedByIdFilter}.
     */
    public static class Builder extends UserIdFilter.Builder<UpdatedByIdFilter> {

        public Builder() {
            super(new UpdatedByIdFilter());
        }

    }

}
