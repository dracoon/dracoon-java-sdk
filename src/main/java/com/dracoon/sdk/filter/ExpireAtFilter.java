package com.dracoon.sdk.filter;

import java.util.Date;

import com.dracoon.sdk.internal.util.DateUtils;

/**
 * Filter for restricting API requests by expire date. (Time information is ignored.)
 */
public class ExpireAtFilter extends Filter<String> {

    private static final String NAME = "expireAt";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private ExpireAtFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link ExpireAtFilter}.
     */
    public static class Builder extends Filter.Builder<Date> {

        private ExpireAtFilter mFilter;

        public Builder() {
            mFilter = new ExpireAtFilter();
        }

        Builder(ExpireAtFilter filter) {
            mFilter = filter;
        }

        @Override
        protected Concater ge(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(GE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

        @Override
        protected Concater le(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(LE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private ExpireAtFilter mFilter;

        Concater(ExpireAtFilter filter) {
            mFilter = filter;
        }

        @Override
        protected Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public ExpireAtFilter build() {
            return mFilter;
        }

    }

}
