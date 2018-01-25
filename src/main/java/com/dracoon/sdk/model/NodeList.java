package com.dracoon.sdk.model;

import java.util.List;

/**
 * Node list model.<br>
 * <br>
 * This model stores a list of nodes. The list may be a paginated response.
 * {@link NodeList#getOffset() Offset} and {@link NodeList#getLimit() limit} can be used to get the
 * start and length of the page. {@link NodeList#getTotal() Total} can be used to get the total
 * number of items.
 *
 * @see com.dracoon.sdk.model.Node
 */
@SuppressWarnings("unused")
public class NodeList {

    private Integer mOffset;
    private Integer mLimit;
    private Long mTotal;
    private List<Node> mItems;

    /**
     * Returns the page offset.
     *
     * @return the page offset
     */
    public Integer getOffset() {
        return mOffset;
    }

    /**
     * Sets the page offset.
     *
     * @param offset The page offset.
     */
    public void setOffset(Integer offset) {
        mOffset = offset;
    }

    /**
     * Returns the page limit.
     *
     * @return the page limit
     */
    public Integer getLimit() {
        return mLimit;
    }

    /**
     * Sets the page limit.
     *
     * @param limit The page limit.
     */
    public void setLimit(Integer limit) {
        mLimit = limit;
    }

    /**
     * Returns the total number of items.
     *
     * @return the total number of items
     */
    public Long getTotal() {
        return mTotal;
    }

    /**
     * Sets the total number of items.
     *
     * @param total The total number of items.
     */
    public void setTotal(Long total) {
        mTotal = total;
    }

    /**
     * Returns the items of the page.
     *
     * @return the items
     */
    public List<Node> getItems() {
        return mItems;
    }

    /**
     * Sets the items of the page.
     *
     * @param items The items.
     */
    public void setItems(List<Node> items) {
        mItems = items;
    }

}
