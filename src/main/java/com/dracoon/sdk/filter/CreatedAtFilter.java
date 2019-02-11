package com.dracoon.sdk.filter;

import java.util.Date;

import com.dracoon.sdk.internal.util.DateUtils;

/**
 * Filter for restricting API requests by creation date. (Time information is ignored.)
 */
public class CreatedAtFilter extends Filter<String> {

    private static final String NAME = "createdAt";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private CreatedAtFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link CreatedAtFilter}.
     */
    public static class Builder extends Filter.Builder<Date> {

        private CreatedAtFilter mFilter;

        public Builder() {
            mFilter = new CreatedAtFilter();
        }

        Builder(CreatedAtFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater ge(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(GE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

        @Override
        public Concater le(Date value) {
            validateRestrictionValue(value);
            mFilter.addValue(LE, DateUtils.formatDate(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private CreatedAtFilter mFilter;

        Concater(CreatedAtFilter filter) {
            mFilter = filter;
        }

        @Override
        public Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public CreatedAtFilter build() {
            return mFilter;
        }

    }

}
