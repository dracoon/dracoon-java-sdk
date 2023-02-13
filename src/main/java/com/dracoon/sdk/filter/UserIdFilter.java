package com.dracoon.sdk.filter;

abstract class UserIdFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_VALUE;

    protected UserIdFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder extends Filter.Builder<Long, String> {

        private final UserIdFilter mFilter;

        protected Builder(UserIdFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater eq(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, Long.toString(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<Long, String> {

        private final UserIdFilter mFilter;

        Concater(UserIdFilter filter) {
            mFilter = filter;
        }

        @Override
        public UserIdFilter build() {
            return mFilter;
        }

    }

}
