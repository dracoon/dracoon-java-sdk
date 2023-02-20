package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by updater's user name.
 */
public class UpdatedByFilter extends UserNameFilter {

    private static final String NAME = "updatedBy";

    private UpdatedByFilter() {
        super(NAME);
    }

    /**
     * Builder for creating new instances of {@link UpdatedByFilter}.
     */
    public static class Builder extends UserNameFilter.Builder {

        public Builder() {
            super(new UpdatedByFilter());
        }

    }

}
