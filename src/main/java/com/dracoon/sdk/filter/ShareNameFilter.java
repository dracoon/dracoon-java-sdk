package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by share name.
 */
public class ShareNameFilter extends Filter<String> {

    private static final String NAME = "name";
    private static final Type TYPE = Type.MULTI_VALUE;

    private ShareNameFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link ShareNameFilter}.
     */
    public static class Builder extends Filter.Builder<String> {

        private final ShareNameFilter mFilter;

        public Builder() {
            mFilter = new ShareNameFilter();
        }

        @Override
        public Concater cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(CN, value);
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private final ShareNameFilter mFilter;

        Concater(ShareNameFilter filter) {
            mFilter = filter;
        }

        @Override
        public ShareNameFilter build() {
            return mFilter;
        }

    }
}
