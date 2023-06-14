package com.dracoon.sdk.model;

import java.util.List;

/**
 * File virus scan info list model.<br>
 * <br>
 * This model stores information about virus scans of files.
 */
@SuppressWarnings("unused")
public class FileVirusScanInfoList {

    private List<FileVirusScanInfo> mItems;

    /**
     * Returns the items.
     *
     * @return the items
     */
    public List<FileVirusScanInfo> getItems() {
        return mItems;
    }

    /**
     * Sets the items.
     *
     * @param items The items.
     */
    public void setItems(List<FileVirusScanInfo> items) {
        mItems = items;
    }

}
