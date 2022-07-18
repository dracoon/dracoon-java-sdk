package com.dracoon.sdk.filter;

import com.dracoon.sdk.model.NodeType;

/**
 * Filter for restricting API requests by node type.
 */
public class NodeTypeFilter extends Filter<String> {

    private static final String NAME = "type";
    private static final Type TYPE = Type.MULTI_VALUE;

    private NodeTypeFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link NodeTypeFilter}.
     */
    public static class Builder extends Filter.Builder<NodeType, String> {

        private final NodeTypeFilter mFilter;

        public Builder() {
            mFilter = new NodeTypeFilter();
        }

        Builder(NodeTypeFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater eq(NodeType value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_EQ, value.getValue());
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<NodeType, String> {

        private final NodeTypeFilter mFilter;

        Concater(NodeTypeFilter filter) {
            mFilter = filter;
        }

        @Override
        public Builder or() {
            return new Builder(mFilter);
        }

        @Override
        public NodeTypeFilter build() {
            return mFilter;
        }

    }

}
