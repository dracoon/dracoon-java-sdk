package com.dracoon.sdk.filter;

abstract class UserIdFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_VALUE;

    protected UserIdFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder<T extends UserIdFilter> extends Filter.Builder<Long, String> {

        private final T mFilter;

        protected Builder(T filter) {
            mFilter = filter;
        }

        @Override
        public Concater<T> eq(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, Long.toString(value));
            return new Concater<>(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater<T extends UserIdFilter> extends Filter.Concater<Long, String> {

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
