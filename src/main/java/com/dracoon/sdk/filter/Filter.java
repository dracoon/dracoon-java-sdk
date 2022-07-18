package com.dracoon.sdk.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all filters.
 */
public abstract class Filter<TF> {

    private static final String MV_SEPARATOR = ":";
    private static final String MR_SEPARATOR = "|";

    private final String mFieldName;
    private final Type mType;

    private final List<String> mOperators = new ArrayList<>();
    private final List<TF> mValues = new ArrayList<>();

    Filter(String fieldName, Type type) {
        mFieldName = fieldName;
        mType = type;
    }

    void addValue(String operator, TF value) {
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
     */
    protected static abstract class Builder<T1, T2> {

        protected static final String OPERATOR_EQ = "eq";
        protected static final String OPERATOR_CN = "cn";
        protected static final String OPERATOR_GE = "ge";
        protected static final String OPERATOR_LE = "le";

        /**
         * Adds an "equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater<T1, T2> eq(T1 value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds an "contains" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater<T1, T2> cn(T1 value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds a "greater equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater<T1, T2> ge(T1 value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Adds a "lower equals" restriction to the filter.
         *
         * @param value The restriction value.
         *
         * @return a new {@link Concater}
         */
        protected Concater<T1, T2> le(T1 value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Validates a restriction value.
         *
         * @param value The restriction value.
         */
        protected void validateRestrictionValue(T1 value) {
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
    protected static abstract class Concater<T1, T2> {

        /**
         * Allows concatenation with "and" operator.
         *
         * @return a new builder which can be used to supply further filter values
         */
        protected Builder<T1, T2> and() {
            throw new UnsupportedOperationException();
        }

        /**
         * Allows concatenation with "or" operator.
         *
         * @return a new builder which can be used to supply further filter values
         */
        protected Builder<T1, T2> or() {
            throw new UnsupportedOperationException();
        }

        /**
         * Creates a new {@link Filter} instance with the supplied configuration.
         *
         * @return a new {@link Filter} instance
         */
        public abstract Filter<T2> build();

    }

}
