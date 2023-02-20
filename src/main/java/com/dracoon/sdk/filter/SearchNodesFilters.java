package com.dracoon.sdk.filter;

public class SearchNodesFilters extends Filters {

    public void addNodeTypeFilter(NodeTypeFilter filter) {
        mFilters.add(filter);
    }

    public void addFileTypeFilter(FileTypeFilter filter) {
        mFilters.add(filter);
    }

    public void addClassificationFilter(ClassificationFilter filter) {
        mFilters.add(filter);
    }

    public void addCreatedByFilter(CreatedByFilter filter) {
        mFilters.add(filter);
    }

    public void addCreatedByIdFilter(CreatedByIdFilter filter) {
        mFilters.add(filter);
    }

    public void addCreatedAtFilter(CreatedAtFilter filter) {
        mFilters.add(filter);
    }

    public void addUpdatedByFilter(UpdatedByFilter filter) {
        mFilters.add(filter);
    }

    public void addUpdatedByIdFilter(UpdatedByIdFilter filter) {
        mFilters.add(filter);
    }

    public void addUpdatedAtFilter(UpdatedAtFilter filter) {
        mFilters.add(filter);
    }

    public void addExpireAtFilter(ExpireAtFilter filter) {
        mFilters.add(filter);
    }

    public void addNodeSizeFilter(NodeSizeFilter filter) {
        mFilters.add(filter);
    }

    public void addFavoriteStatusFilter(FavoriteStatusFilter filter) {
        mFilters.add(filter);
    }

    public void addBranchVersionFilter(BranchVersionFilter filter) {
        mFilters.add(filter);
    }

    public void addNodeParentPathFilter(NodeParentPathFilter filter) {
        mFilters.add(filter);
    }

}
