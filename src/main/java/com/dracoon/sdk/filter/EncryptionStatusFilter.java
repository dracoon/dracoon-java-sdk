package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by encryption status.
 */
public class EncryptionStatusFilter extends Filter<String> {

    private static final String NAME = "encrypted";
    private static final Type TYPE = Type.MULTI_VALUE;

    private EncryptionStatusFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link EncryptionStatusFilter}.
     */
    public static class Builder extends Filter.Builder<Boolean> {

        private EncryptionStatusFilter mFilter;

        public Builder() {
            mFilter = new EncryptionStatusFilter();
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

        private EncryptionStatusFilter mFilter;

        Concater(EncryptionStatusFilter filter) {
            mFilter = filter;
        }

        @Override
        public EncryptionStatusFilter build() {
            return mFilter;
        }

    }

}
