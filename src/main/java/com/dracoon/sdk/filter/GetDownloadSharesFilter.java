package com.dracoon.sdk.filter;

public class GetDownloadSharesFilter extends Filters {

    public void addCreatedByFilter(CreatedByFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addCreatedByIdFilter(CreatedByIdFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addNodeIdFilter(NodeIdFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

}
