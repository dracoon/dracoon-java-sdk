package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by favorite status.
 */
public class FavoriteStatusFilter extends Filter<String> {

    private static final String NAME = "isFavorite";
    private static final Type TYPE = Type.MULTI_VALUE;

    private FavoriteStatusFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link FavoriteStatusFilter}.
     */
    public static class Builder extends Filter.Builder<Boolean> {

        private final FavoriteStatusFilter mFilter;

        public Builder() {
            mFilter = new FavoriteStatusFilter();
        }

        @Override
        public Concater eq(Boolean value) {
            validateRestrictionValue(value);
            mFilter.addValue(EQ, value ? "true" : "false");
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private final FavoriteStatusFilter mFilter;

        Concater(FavoriteStatusFilter filter) {
            mFilter = filter;
        }

        @Override
        public FavoriteStatusFilter build() {
            return mFilter;
        }

    }

}
