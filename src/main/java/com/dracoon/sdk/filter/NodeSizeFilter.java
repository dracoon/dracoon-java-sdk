package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by node size.
 */
public class NodeSizeFilter extends Filter<String> {

    private static final String NAME = "size";
    private static final Type TYPE = Type.MULTI_RESTRICTION;

    private NodeSizeFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link NodeSizeFilter}.
     */
    public static class Builder extends Filter.Builder<Long> {

        private final NodeSizeFilter mFilter;

        public Builder() {
            mFilter = new NodeSizeFilter();
        }

        Builder(NodeSizeFilter filter) {
            mFilter = filter;
        }

        @Override
        public Concater ge(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(GE, Long.toString(value));
            return new Concater(mFilter);
        }

        @Override
        public Concater le(Long value) {
            validateRestrictionValue(value);
            mFilter.addValue(LE, Long.toString(value));
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater {

        private final NodeSizeFilter mFilter;

        Concater(NodeSizeFilter filter) {
            mFilter = filter;
        }

        @Override
        public Builder and() {
            return new Builder(mFilter);
        }

        @Override
        public NodeSizeFilter build() {
            return mFilter;
        }

    }

}
