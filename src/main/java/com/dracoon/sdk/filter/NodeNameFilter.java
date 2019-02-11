package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by node name.
 */
public class NodeNameFilter extends Filter<String> {

    private static final String NAME = "name";
    private static final Type TYPE = Type.MULTI_VALUE;

    private NodeNameFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link NodeNameFilter}.
     */
    public static class Builder extends Filter.Builder<String> {

        private NodeNameFilter mFilter;

        public Builder() {
            mFilter = new NodeNameFilter();
        }

        @Override
        public Concater cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(CN, value);
            return new Concater(mFilter);
        }

        @Override
        public Concater eq(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(EQ, value);
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private NodeNameFilter mFilter;

        Concater(NodeNameFilter filter) {
            mFilter = filter;
        }

        @Override
        public NodeNameFilter build() {
            return mFilter;
        }

    }

}
