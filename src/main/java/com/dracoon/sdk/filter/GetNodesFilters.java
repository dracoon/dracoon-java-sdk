package com.dracoon.sdk.filter;

public class GetNodesFilters extends Filters {

    public void addNodeTypeFilter(NodeTypeFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addNodeNameFilter(NodeNameFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addEncryptionStatusFilter(EncryptionStatusFilter filter) {
        validateSingleFilter(filter);
        mFilters.add(filter);
    }

    public void addBranchVersionFilter(BranchVersionFilter filter) {
        validateMultiFilter(filter);
        mFilters.add(filter);
    }

}
