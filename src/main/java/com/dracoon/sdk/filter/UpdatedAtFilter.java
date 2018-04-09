package com.dracoon.sdk.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Filter for restricting API requests by update date. (Time information is ignored.)
 */
public class UpdatedAtFilter extends Filter<String> {

    private static final String NAME = "updatedAt";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private UpdatedAtFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link UpdatedAtFilter}.
     */
    public static class Builder extends Filter.Builder<Date> {

        private UpdatedAtFilter mFilter;

        public Builder() {
            mFilter = new UpdatedAtFilter();
        }

        Builder(UpdatedAtFilter filter) {
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

        private UpdatedAtFilter mFilter;

        Concater(UpdatedAtFilter filter) {
            mFilter = filter;
        }

        @Override
        protected Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public UpdatedAtFilter build() {
            return mFilter;
        }

    }

}
