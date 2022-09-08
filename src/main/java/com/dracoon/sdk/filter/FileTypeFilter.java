package com.dracoon.sdk.filter;

/**
 * Filter for restricting API requests by file type.
 */
public class FileTypeFilter extends Filter<String> {

    private static final String NAME = "fileType";
    private static final Type TYPE = Type.MULTI_VALUE;

    private FileTypeFilter() {
        super(NAME, TYPE);
    }

    /**
     * Builder for creating new instances of {@link FileTypeFilter}.
     */
    public static class Builder extends Filter.Builder<String, String> {

        private final FileTypeFilter mFilter;

        public Builder() {
            mFilter = new FileTypeFilter();
        }

        @Override
        public Concater cn(String value) {
            validateRestrictionValue(value);
            mFilter.addValue(OPERATOR_CN, value);
            return new Concater(mFilter);
        }

    }

    /**
     * Class for adding further filter restrictions.
     */
    public static class Concater extends Filter.Concater<String, String> {

        private final FileTypeFilter mFilter;

        Concater(FileTypeFilter filter) {
            mFilter = filter;
        }

        @Override
        public FileTypeFilter build() {
            return mFilter;
        }

    }

}
