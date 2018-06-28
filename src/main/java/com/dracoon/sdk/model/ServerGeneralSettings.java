package com.dracoon.sdk.model;

/**
 * Server general settings model.<br>
 * <br>
 * This model stores information about the server general settings.
 */
@SuppressWarnings("unused")
public class ServerGeneralSettings {

    private boolean mSharePasswordSmsEnabled;
    private boolean mCryptoEnabled;
    private boolean mMediaServerEnabled;
    private boolean mWeakPasswordEnabled;

    /**
     * Returns <code>true</code> if share passwords can be send via SMS.
     *
     * @return <code>true</code> if share passwords can be send via SMS; <code>false</code> otherwise
     */
    public boolean isSharePasswordSmsEnabled() {
        return mSharePasswordSmsEnabled;
    }

    /**
     * Sets if share passwords can be send via SMS.
     *
     * @param sharePasswordSmsEnabled <code>true</code> if share passwords can be send via SMS;
     *                                <code>false</code> otherwise.
     */
    public void setSharePasswordSmsEnabled(boolean sharePasswordSmsEnabled) {
        mSharePasswordSmsEnabled = sharePasswordSmsEnabled;
    }

    /**
     * Returns <code>true</code> if client-side cryptography is enabled for rooms.
     *
     * @return <code>true</code> if client-side cryptography is enabled for rooms;
     *         <code>false</code> otherwise
     */
    public boolean isCryptoEnabled() {
        return mCryptoEnabled;
    }

    /**
     * Sets if client-side cryptography is enabled for rooms.
     *
     * @param cryptoEnabled <code>true</code> if client-side cryptography is enabled for rooms;
     *                      <code>false</code> otherwise.
     */
    public void setCryptoEnabled(boolean cryptoEnabled) {
        mCryptoEnabled = cryptoEnabled;
    }

    /**
     * Returns <code>true</code> if media server is enabled.
     *
     * @return <code>true</code> if media server is enabled; <code>false</code> otherwise
     */
    public boolean isMediaServerEnabled() {
        return mMediaServerEnabled;
    }

    /**
     * Sets if media server is enabled.
     *
     * @param mediaServerEnabled <code>true</code> if media server is enabled; <code>false</code>
     *                           otherwise.
     */
    public void setMediaServerEnabled(boolean mediaServerEnabled) {
        mMediaServerEnabled = mediaServerEnabled;
    }

    /**
     * Returns <code>true</code> if weak passwords are allowed.<br>
     * <br>
     * A weak password has to fulfill the following criteria:<br>
     * - is at least 8 characters long<br>
     * - contain letters and numbers<br>
     * A strong password has to fulfill the following criteria in addition:<br>
     * - contain at least one special character<br>
     * - contain upper and lower case characters
     *
     * @return <code>true</code> if weak passwords are allowed; <code>false</code> otherwise
     */
    public boolean isWeakPasswordEnabled() {
        return mWeakPasswordEnabled;
    }

    /**
     * Sets if weak passwords are allowed.
     *
     * @param weakPasswordEnabled <code>true</code> if weak passwords are allowed; <code>false</code>
     *                            otherwise.
     */
    public void setWeakPasswordEnabled(boolean weakPasswordEnabled) {
        mWeakPasswordEnabled = weakPasswordEnabled;
    }

}
