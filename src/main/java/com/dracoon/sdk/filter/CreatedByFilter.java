package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by creator's user name.
 */
public class CreatedByFilter extends Filter<String> {

    private static final String NAME = "createdBy";
    private static final Type TYPE = Type.MULTI_VALUE;

    private CreatedByFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link CreatedByFilter}.
     */
    public static class Builder extends Filter.Builder<String> {

        private final CreatedByFilter mFilter;

        public Builder() {
            mFilter = new CreatedByFilter();
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

        private final CreatedByFilter mFilter;

        Concater(CreatedByFilter filter) {
            mFilter = filter;
        }

        @Override
        public CreatedByFilter build() {
            return mFilter;
        }

    }

}
