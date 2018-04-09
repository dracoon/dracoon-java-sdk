package com.dracoon.sdk.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Filter for restricting API requests by expire date. (Time information is ignored.)
 */
public class ExpireAtFilter extends Filter<String> {

    private static final String NAME = "expireAt";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
            mFilter.addValue(GE, sDateFormat.format(value));
            return new Concater(mFilter);
        }

        @Override
        protected Concater le(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(LE, sDateFormat.format(value));
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
