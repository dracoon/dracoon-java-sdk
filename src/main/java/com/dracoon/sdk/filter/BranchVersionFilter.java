package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by branch version.
 */
public class BranchVersionFilter extends Filter<String> {

    private static final String NAME = "branchVersion";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private BranchVersionFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link BranchVersionFilter}.
     */
    public static class Builder extends Filter.Builder<Long> {

        private BranchVersionFilter mFilter;

        public Builder() {
            mFilter = new BranchVersionFilter();
        }

        Builder(BranchVersionFilter filter) {
            mFilter = filter;
        }

        @Override
        protected Concater ge(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(GE, Long.toString(value));
            return new Concater(mFilter);
        }

        @Override
        protected Concater le(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(LE, Long.toString(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private BranchVersionFilter mFilter;

        Concater(BranchVersionFilter filter) {
            mFilter = filter;
        }

        @Override
        protected Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public BranchVersionFilter build() {
            return mFilter;
        }

    }

}
