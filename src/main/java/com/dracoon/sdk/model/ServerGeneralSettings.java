package com.dracoon.sdk.model;

/**
 * Server general settings model.<br>
 * <br>
 * This model stores information about the server general settings.
 */
@SuppressWarnings("unused")
public class ServerGeneralSettings {

    private Boolean mSharePasswordSmsEnabled;
    private Boolean mCryptoEnabled;
    private Boolean mMediaServerEnabled;
    private Boolean mWeakPasswordEnabled;

    /**
     * Returns <code>true</code> if share passwords can be send via SMS.
     *
     * @return <code>true</code> if share passwords can be send via SMS; <code>false</code>
     * otherwise
     */
    public Boolean isSharePasswordSmsEnabled() {
        return mSharePasswordSmsEnabled;
    }

    /**
     * Sets if share passwords can be send via SMS.
     *
     * @param sharePasswordSmsEnabled <code>true</code> if share passwords can be send via SMS;
     *                                <code>false</code> otherwise.
     */
    public void setSharePasswordSmsEnabled(Boolean sharePasswordSmsEnabled) {
        mSharePasswordSmsEnabled = sharePasswordSmsEnabled;
    }

    /**
     * Returns <code>true</code> if client-side cryptography is enabled for rooms.
     *
     * @return <code>true</code> if client-side cryptography is enabled for rooms;
     *         <code>false</code> otherwise
     */
    public Boolean isCryptoEnabled() {
        return mCryptoEnabled;
    }

    /**
     * Sets if client-side cryptography is enabled for rooms.
     *
     * @param cryptoEnabled <code>true</code> if client-side cryptography is enabled for rooms;
     *                      <code>false</code> otherwise.
     */
    public void setCryptoEnabled(Boolean cryptoEnabled) {
        mCryptoEnabled = cryptoEnabled;
    }

    /**
     * Returns <code>true</code> if media server is enabled.
     *
     * @return <code>true</code> if media server is enabled; <code>false</code> otherwise
     */
    public Boolean isMediaServerEnabled() {
        return mMediaServerEnabled;
    }

    /**
     * Sets if media server is enabled.
     *
     * @param mediaServerEnabled <code>true</code> if media server is enabled; <code>false</code>
     *                           otherwise.
     */
    public void setMediaServerEnabled(Boolean mediaServerEnabled) {
        mMediaServerEnabled = mediaServerEnabled;
    }

    /**
     * Returns <code>true</code> if weak passwords are allowed.<br>
     * <br>
     * A weak password has to fulfill the following criteria:<br>
     * - is at least 8 characters long<br>
     * - contain letters<br>
     * A strong password has to fulfill the following criteria in addition:<br>
     * - contain upper and lower case characters<br>
     * - contain at least one digit<br>
     * - contain at least one special character
     *
     * @return <code>true</code> if weak passwords are allowed; <code>false</code> otherwise
     */
    @Deprecated
    public Boolean isWeakPasswordEnabled() {
        return mWeakPasswordEnabled;
    }

    /**
     * Sets if weak passwords are allowed.
     *
     * @param weakPasswordEnabled <code>true</code> if weak passwords are allowed;
     *                            <code>false</code> otherwise.
     */
    @Deprecated
    public void setWeakPasswordEnabled(Boolean weakPasswordEnabled) {
        mWeakPasswordEnabled = weakPasswordEnabled;
    }

}
