package com.dracoon.sdk.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all filters.
 */
public abstract class Filter<T> {

    private static final String MV_SEPARATOR = ":";
    private static final String MR_SEPARATOR = "|";

    private String mFieldName;

    private Type mType;

    private List<String> mOperators = new ArrayList<>();
    private List<T> mValues = new ArrayList<>();

    Filter(String fieldName, Type type) {
        mFieldName = fieldName;
        mType = type;
    }

    void addValue(String operator, T value) {
        mOperators.add(operator);
        mValues.add(value);
    }

    /**
     * Returns the string representation of the filter.<br>
     * <br>
     * String syntax:<br>
     * - Multi value filter:       FIELD_NAME:OPERATOR:VALUE_1[:...:VALUE_N]<br>
     * - Multi restriction filter: FIELD_NAME:OPERATOR_1:VALUE_1[|...|FIELD_NAME:OPERATOR_N:VALUE_N]
     *
     * @return the string representation of the filter
     */
    @Override
    public String toString() {
        switch (mType) {
            case MULTI_VALUE:
                return buildMultiValueString();
            case MULTI_RESTRICTION:
                return buildMultiRestrictionString();
            default:
                return "";
        }
    }

    private String buildMultiValueString() {
        StringBuilder sb = new StringBuilder();

        sb.append(mFieldName);
        sb.append(MV_SEPARATOR);

        sb.append(mOperators.get(0));
        sb.append(MV_SEPARATOR);

        for (int i = 0; i < mValues.size(); i++) {
            sb.append(mValues.get(i));
            sb.append(i < mValues.size() - 1 ? MV_SEPARATOR : "");
        }

        return sb.toString();
    }

    private String buildMultiRestrictionString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mValues.size(); i++) {
            sb.append(mFieldName);
            sb.append(MV_SEPARATOR);
            sb.append(mOperators.get(i));
            sb.append(MV_SEPARATOR);
            sb.append(mValues.get(i));
            sb.append(i < mValues.size() - 1 ? MR_SEPARATOR : "");
        }

        return sb.toString();
    }

    /**
     * Enum for filter type.<br>
     * <br>
     * Multi value and multi restriction filters are constructed differently.
     * See {@link Filter#toString()}.
     */
    protected enum Type {
        MULTI_VALUE,
        MULTI_RESTRICTION
    }

    /**
     * The base class for all filter builders.
     *
     * @param <T> The type of the filter builder.
     */
    protected static abstract class Builder<T> {

        protected static final String EQ = "eq";
        protected static final String CN = "cn";
        protected static final String GE = "ge";
        protected static final String LE = "le";

        /**
         * Adds an "equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater eq(T value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds an "contains" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater cn(T value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds a "greater equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater ge(T value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds a "lower equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater le(T value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Validates a restriction value.
         *
         * @param value The restriction value.
         */
        protected void validateRestrictionValue(T value) {
            if (value == null) {
                throw new IllegalArgumentException("Restriction value cannot be null.");
            }
        }

    }

    /**
     * The base class for all filter concater.<br>
     * <br>
     * A concater can be used to supply further filter values and/or build the corresponding filter.
     */
    protected static abstract class Concater {

        /**
         * Allows concatenation with "and" operator.
         *
         * @return a new builder which can be used to supply further filter values
         */
        protected Builder and() {
            throw new UnsupportedOperationException();
        }

        /**
         * Allows concatenation with "or" operator.
         *
         * @return a new builder which can be used to supply further filter values
         */
        protected Builder or() {
            throw new UnsupportedOperationException();
        }

        /**
         * Creates a new {@link Filter} instance with the supplied configuration.
         *
         * @return a new {@link Filter} instance
         */
        public abstract Filter build();

    }

}
