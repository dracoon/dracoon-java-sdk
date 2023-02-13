package com.dracoon.sdk.filter;

import java.util.Date;

import com.dracoon.sdk.internal.util.DateUtils;

abstract class DateFilter extends Filter<String> {

    private static final Type TYPE = Type.MULTI_RESTRICTION;

    protected DateFilter(String name) {
        super(name, TYPE);
    }

    protected static class Builder extends Filter.Builder<Date, String> {

        private final DateFilter mFilter;

        protected Builder(DateFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater ge(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_GE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

        @Override
        public Concater le(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_LE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<Date, String> {

        private final DateFilter mFilter;

        Concater(DateFilter filter) {
            mFilter = filter;
        }

        @Override
        public Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public DateFilter build() {
            return mFilter;
        }

    }

}
