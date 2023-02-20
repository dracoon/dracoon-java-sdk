package com.dracoon.sdk.filter;

abstract class UserNameFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_VALUE;

    protected UserNameFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder extends Filter.Builder<String, String> {

        private final UserNameFilter mFilter;

        protected Builder(UserNameFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater eq(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, value);
            return new Concater(mFilter);
        }

        @Override
        public Concater cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_CN, value);
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<String, String> {

        private final UserNameFilter mFilter;

        Concater(UserNameFilter filter) {
            mFilter = filter;
        }

        @Override
        public UserNameFilter build() {
            return mFilter;
        }

    }

}
