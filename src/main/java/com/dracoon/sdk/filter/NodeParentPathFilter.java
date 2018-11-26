package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by node parent path.
 */
public class NodeParentPathFilter extends Filter<String> {

    private static final String NAME = "parentPath";
    private static final Type TYPE = Type.MULTI_VALUE;

    private NodeParentPathFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link NodeParentPathFilter}.
     */
    public static class Builder extends Filter.Builder<String> {

        private NodeParentPathFilter mFilter;

        public Builder() {
            mFilter = new NodeParentPathFilter();
        }

        @Override
        public Concater eq(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(EQ, value);
            return new Concater(mFilter);
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

        private NodeParentPathFilter mFilter;

        Concater(NodeParentPathFilter filter) {
            mFilter = filter;
        }

        @Override
        public NodeParentPathFilter build() {
            return mFilter;
        }

    }

}
