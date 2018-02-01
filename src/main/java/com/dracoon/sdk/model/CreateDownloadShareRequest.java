package com.dracoon.sdk.model;

import java.util.Date;
import java.util.List;

/**
 * Request to create a new download share.<br>
 * <br>
 * A new instance can be created with {@link CreateDownloadShareRequest.Builder Builder}.
 */
@SuppressWarnings("unused")
public class CreateDownloadShareRequest {

    private Long mNodeId;
    private String mName;
    private String mNotes;
    private Date mExpirationDate;
    private Boolean mShowCreatorName;
    private Boolean mShowCreatorUserName;
    private Boolean mNotifyCreator;
    private Integer mMaxDownloads;
    private String mAccessPassword;
    private String mEncryptionPassword;
    private Boolean mSendEmail;
    private List<String> mEmailRecipients;
    private String mEmailSubject;
    private String mEmailBody;
    private Boolean mSendSms;
    private List<String> mSmsRecipients;

    private CreateDownloadShareRequest() {

    }

    /**
     * Returns the node ID of the new download share.
     *
     * @return the node ID
     */
    public Long getNodeId() {
        return mNodeId;
    }

    /**
     * Returns the name of the new download share.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the notes of the new download share.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Returns the expiration date of the new download share.
     *
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return mExpirationDate;
    }

    /**
     * Returns <code>true</code> if creator's name will be shown for the new download share.
     *
     * @return <code>true</code> if creator's name will be shown; <code>false</code> otherwise
     */
    public Boolean showCreatorName() {
        return mShowCreatorName;
    }

    /**
     * Returns <code>true</code> if creator's user name will be shown for the new download share.
     *
     * @return <code>true</code> if creator's user name will be shown; <code>false</code> otherwise
     */
    public Boolean showCreatorUserName() {
        return mShowCreatorUserName;
    }

    /**
     * Returns <code>true</code> if creator will be notified at downloads of the new download share.
     *
     * @return <code>true</code> if creator will be notified; <code>false</code> otherwise
     */
    public Boolean notifyCreator() {
        return mNotifyCreator;
    }

    /**
     * Returns the maximum number of downloads for the new download share.
     *
     * @return the maximum number of downloads
     */
    public Integer getMaxDownloads() {
        return mMaxDownloads;
    }

    /**
     * Returns the access password of the new download share.
     *
     * @return the access password
     */
    public String getAccessPassword() {
        return mAccessPassword;
    }

    /**
     * Returns the encryption password of the new download share.
     *
     * @return the encryption password
     */
    public String getEncryptionPassword() {
        return mEncryptionPassword;
    }

    /**
     * Returns <code>true</code> if recipients are notified via email.
     *
     * @return <code>true</code> if recipients are notified; <code>false</code> otherwise
     */
    public Boolean sendEmail() {
        return mSendEmail;
    }

    /**
     * Returns the email addresses of the email notification.
     *
     * @return the email addresses
     */
    public List<String> getEmailRecipients() {
        return mEmailRecipients;
    }

    /**
     * Returns the email subject of the email notification.
     *
     * @return the email subject
     */
    public String getEmailSubject() {
        return mEmailSubject;
    }

    /**
     * Returns the email body of the email notification.
     *
     * @return the email body
     */
    public String getEmailBody() {
        return mEmailBody;
    }

    /**
     * Returns <code>true</code> if recipients are notified via SMS.
     *
     * @return <code>true</code> if recipients are notified; <code>false</code> otherwise
     */
    public Boolean sendSms() {
        return mSendSms;
    }

    /**
     * Returns the phone numbers of the SMS notification.
     *
     * @return the phone numbers
     */
    public List<String> getSmsRecipients() {
        return mSmsRecipients;
    }

    /**
     * This builder creates new instances of {@link CreateDownloadShareRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node ID (mandatory):     {@link Builder#Builder(Long)}<br>
     * - Name:                    {@link Builder#name(String)}<br>
     * - Notes:                   {@link Builder#notes(String)}<br>
     * - Expiration date:         {@link Builder#expirationDate(Date)}<br>
     * - Show creator name:       {@link Builder#showCreatorName(Boolean)}<br>
     * - Show creator user name:  {@link Builder#showCreatorUserName(Boolean)}<br>
     * - Notify creator:          {@link Builder#notifyCreator(Boolean)}<br>
     * - Max downloads:           {@link Builder#maxDownloads(Integer)}<br>
     * - Access Password:         {@link Builder#accessPassword(String)}<br>
     * - Encryption Password:     {@link Builder#encryptionPassword(String)}<br>
     * - Email notification data: {@link Builder#sendEmail(List, String, String)}<br>
     * - SMS notification data:   {@link Builder#sendSms(List)}
     */
    public static class Builder {

        private CreateDownloadShareRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param nodeId The ID of the node of the new download share. (ID must be positive.)
         */
        public Builder(Long nodeId) {
            mRequest = new CreateDownloadShareRequest();
            mRequest.mNodeId = nodeId;
            mRequest.mSendEmail = false;
            mRequest.mSendSms = false;
        }

        /**
         * Sets the name of the new download share.
         *
         * @param name The name. (Name must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        /**
         * Sets the notes of the new download share.
         *
         * @param notes The notes.
         *
         * @return a reference to this object
         */
        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        /**
         * Sets the expiration date of the new download share.
         *
         * @param expirationDate The expiration date.
         *
         * @return a reference to this object
         */
        public Builder expirationDate(Date expirationDate) {
            mRequest.mExpirationDate = expirationDate;
            return this;
        }

        /**
         * Enables/disables if the creator's name will be shown for the new download share.
         *
         * @param showCreatorName <code>true</code> to enable that creator's name will be shown;
         *                        otherwise <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder showCreatorName(Boolean showCreatorName) {
            mRequest.mShowCreatorName = showCreatorName;
            return this;
        }

        /**
         * Enables/disables if the creator's user name will be shown for the new download share.
         *
         * @param showCreatorUserName <code>true</code> to enable that creator's user name will be
         *                            shown; otherwise <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder showCreatorUserName(Boolean showCreatorUserName) {
            mRequest.mShowCreatorUserName = showCreatorUserName;
            return this;
        }

        /**
         * Enables/disables notification of the creator at downloads of the new download share.
         *
         * @param notifyCreator <code>true</code> to enable notification; otherwise
         *                      <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder notifyCreator(Boolean notifyCreator) {
            mRequest.mNotifyCreator = notifyCreator;
            return this;
        }

        /**
         * Sets the maximum number of downloads for the new download share.
         *
         * @param maxDownloads The maximum number of downloads. (Number must be positive.)
         *
         * @return a reference to this object
         */
        public Builder maxDownloads(Integer maxDownloads) {
            mRequest.mMaxDownloads = maxDownloads;
            return this;
        }

        /**
         * Sets the access password of the new download share.
         *
         * @param accessPassword The access password. (Password must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder accessPassword(String accessPassword) {
            mRequest.mAccessPassword = accessPassword;
            return this;
        }

        /**
         * Sets the encryption password of the new download share.
         *
         * @param encryptionPassword The encryption password. (Password must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder encryptionPassword(String encryptionPassword) {
            mRequest.mEncryptionPassword = encryptionPassword;
            return this;
        }

        /**
         * Enables the notification of recipients via email.
         *
         * @param recipients The email addresses of recipients. (A comma separated list of email
         *                   addresses. Must not be empty.)
         * @param subject    The subject of the email. (Subject must not be empty.)
         * @param body       The body of the email. (Body must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder sendEmail(List<String> recipients, String subject, String body) {
            mRequest.mSendEmail = true;
            mRequest.mEmailRecipients = recipients;
            mRequest.mEmailSubject = subject;
            mRequest.mEmailBody = body;
            return this;
        }

        /**
         * Enables the notification of recipients via SMS.
         *
         * @param recipients The phone numbers of recipients. (A comma separated list of phone
         *                   numbers. Must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder sendSms(List<String> recipients) {
            mRequest.mSendSms = true;
            mRequest.mSmsRecipients = recipients;
            return this;
        }

        /**
         * Creates a new {@link CreateDownloadShareRequest} instance with the supplied configuration.
         *
         * @return a new {@link CreateDownloadShareRequest} instance
         */
        public CreateDownloadShareRequest build() {
            return mRequest;
        }

    }

}
