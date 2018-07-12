package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by updater's user name.
 */
public class UpdatedByFilter extends Filter<String> {

    private static final String NAME = "updatedBy";
    private static final Type TYPE = Type.MULTI_VALUE;

    private UpdatedByFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link UpdatedByFilter}.
     */
    public static class Builder extends Filter.Builder<String> {

        private UpdatedByFilter mFilter;

        public Builder() {
            mFilter = new UpdatedByFilter();
        }

        @Override
        public Concater eq(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(EQ, value);
            return new Concater(mFilter);
        }

        @Override
        public Concater cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(CN, value);
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private UpdatedByFilter mFilter;

        Concater(UpdatedByFilter filter) {
            mFilter = filter;
        }

        @Override
        public UpdatedByFilter build() {
            return mFilter;
        }

    }

}
