package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by user ID.
 */
public class UserIdFilter extends Filter<String> {

    private static final String NAME = "userId";
    private static final Type TYPE = Type.MULTI_VALUE;

    private UserIdFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link UserIdFilter}.
     */
    public static class Builder extends Filter.Builder<Long> {

        private final UserIdFilter mFilter;

        public Builder() {
            mFilter = new UserIdFilter();
        }

        @Override
        public Concater eq(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(EQ, Long.toString(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private final UserIdFilter mFilter;

        Concater(UserIdFilter filter) {
            mFilter = filter;
        }

        @Override
        public UserIdFilter build() {
            return mFilter;
        }

    }

}
