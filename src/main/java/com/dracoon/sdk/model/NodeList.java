package com.dracoon.sdk.model;

import java.util.List;

public class NodeList {

    private Integer mOffset;
    private Integer mLimit;
    private Long mTotal;
    private List<Node> mItems;

    public NodeList() {

    }

    public Integer getOffset() {
        return mOffset;
    }

    public void setOffset(Integer offset) {
        mOffset = offset;
    }

    public Integer getLimit() {
        return mLimit;
    }

    public void setLimit(Integer limit) {
        mLimit = limit;
    }

    public Long getTotal() {
        return mTotal;
    }

    public void setTotal(Long total) {
        mTotal = total;
    }

    public List<Node> getItems() {
        return mItems;
    }

    public void setItems(List<Node> items) {
        mItems = items;
    }

}
