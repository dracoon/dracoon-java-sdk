package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by node ID.
 */
public class NodeIdFilter extends Filter<String> {

    private static final String NAME = "nodeId";
    private static final Type TYPE = Type.MULTI_VALUE;

    private NodeIdFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link NodeIdFilter}.
     */
    public static class Builder extends Filter.Builder<Long, String> {

        private final NodeIdFilter mFilter;

        public Builder() {
            mFilter = new NodeIdFilter();
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

        private final NodeIdFilter mFilter;

        Concater(NodeIdFilter filter) {
            mFilter = filter;
        }

        @Override
        public NodeIdFilter build() {
            return mFilter;
        }

    }

}
