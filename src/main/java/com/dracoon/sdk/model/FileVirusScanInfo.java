package com.dracoon.sdk.model;

/**
 * File virus scan info model.<br>
 * <br>
 * This model stores information about a virus scan of a file.
 */
@SuppressWarnings("unused")
public class FileVirusScanInfo {

    private Long mNodeId;
    private VirusScanInfo mVirusScanInfo;

    /**
     * Returns the node ID of the related file.
     *
     * @return the node ID
     */
    public Long getNodeId() {
        return mNodeId;
    }

    /**
     * Sets the node ID of the related file.
     *
     * @param nodeId The node ID.
     */
    public void setNodeId(Long nodeId) {
        mNodeId = nodeId;
    }

    /**
     * Returns information about the virus scan of the related file.
     *
     * @return the information about the virus scan
     */
    public VirusScanInfo getVirusScanInfo() {
        return mVirusScanInfo;
    }

    /**
     * Sets information about the virus scan of the related file.
     *
     * @param virusScanInfo The information about the virus scan.
     */
    public void setVirusScanInfo(VirusScanInfo virusScanInfo) {
        mVirusScanInfo = virusScanInfo;
    }

}
