package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by classification.
 */
public class ClassificationFilter extends Filter<String> {

    private static final String NAME = "classification";
    private static final Type TYPE = Type.MULTI_VALUE;

    private ClassificationFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link ClassificationFilter}.
     */
    public static class Builder extends Filter.Builder<Long, String> {

        private final ClassificationFilter mFilter;

        public Builder() {
            mFilter = new ClassificationFilter();
        }

        Builder(ClassificationFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater eq(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, Long.toString(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<Long, String> {

        private final ClassificationFilter mFilter;

        Concater(ClassificationFilter filter) {
            mFilter = filter;
        }

        @Override
        public Builder or() {
            return new Builder(mFilter);
        }

        @Override
        public ClassificationFilter build() {
            return mFilter;
        }

    }

}
