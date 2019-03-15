package com.dracoon.sdk.model;

/**
 * Server defaults model.<br>
 * <br>
 * This model stores information about the server defaults.
 */
@SuppressWarnings("unused")
public class ServerDefaults {

    private Integer mDownloadShareExpirationPeriod;
    private Integer mUploadShareExpirationPeriod;
    private Integer mFileExpirationPeriod;

    /**
     * Returns the default expiration of download shares.
     *
     * @return the default expiration of download shares
     */
    public Integer getDownloadShareExpirationPeriod() {
        return mDownloadShareExpirationPeriod;
    }

    /**
     * Sets the default expiration of download shares.
     *
     * @param downloadShareExpirationPeriod The default expiration of download shares.
     */
    public void setDownloadShareExpirationPeriod(Integer downloadShareExpirationPeriod) {
        mDownloadShareExpirationPeriod = downloadShareExpirationPeriod;
    }

    /**
     * Returns the default expiration of upload shares.
     *
     * @return the default expiration of upload shares
     */
    public Integer getUploadShareExpirationPeriod() {
        return mUploadShareExpirationPeriod;
    }

    /**
     * Sets the default expiration of upload shares.
     *
     * @param uploadShareExpirationPeriod The default expiration of download shares.
     */
    public void setUploadShareExpirationPeriod(Integer uploadShareExpirationPeriod) {
        mUploadShareExpirationPeriod = uploadShareExpirationPeriod;
    }

    /**
     * Returns the default expiration of files.
     *
     * @return the default expiration of files
     */
    public Integer getFileExpirationPeriod() {
        return mFileExpirationPeriod;
    }

    /**
     * Sets the default expiration of files.
     *
     * @param fileExpirationPeriod The default expiration of files.
     */
    public void setFileExpirationPeriod(Integer fileExpirationPeriod) {
        mFileExpirationPeriod = fileExpirationPeriod;
    }

}
