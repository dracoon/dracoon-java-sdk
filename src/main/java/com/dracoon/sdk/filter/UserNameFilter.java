package com.dracoon.sdk.filter;

abstract class UserNameFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_VALUE;

    protected UserNameFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder<T extends UserNameFilter> extends Filter.Builder<String, String> {

        private final T mFilter;

        protected Builder(T filter) {
            mFilter = filter;
        }

        @Override
        public Concater<T> eq(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, value);
            return new Concater<>(mFilter);
        }

        @Override
        public Concater<T> cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_CN, value);
            return new Concater<>(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater<T extends UserNameFilter> extends Filter.Concater<String, String> {

        private final T mFilter;

        Concater(T filter) {
            mFilter = filter;
        }

        @Override
        public T build() {
            return mFilter;
        }

    }

}
