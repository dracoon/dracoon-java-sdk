package com.dracoon.sdk.filter;

public class GetUploadSharesFilter extends Filters {

    public void addCreatedByFilter(CreatedByFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addUserIdFilter(UserIdFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addNodeIdFilter(NodeIdFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

}
