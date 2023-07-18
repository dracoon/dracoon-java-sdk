package com.dracoon.sdk.filter;

import java.util.Date;

import com.dracoon.sdk.internal.util.DateUtils;

abstract class DateFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_RESTRICTION;

    protected DateFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder<T extends DateFilter> extends Filter.Builder<Date, String> {

        private final T mFilter;

        protected Builder(T filter) {
            mFilter = filter;
        }

        @Override
        public Concater<T> ge(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_GE, DateUtils.formatDate(value));
            return new Concater<>(mFilter);
        }

        @Override
        public Concater<T> le(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_LE, DateUtils.formatDate(value));
            return new Concater<>(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater<T extends DateFilter> extends Filter.Concater<Date, String> {

        private final T mFilter;

        Concater(T filter) {
            mFilter = filter;
        }

        @Override
        public Builder<T> and() {
            return new Builder<>(mFilter);
        }

        @Override
        public T build() {
            return mFilter;
        }

    }

}
