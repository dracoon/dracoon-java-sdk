package com.dracoon.sdk.model;

import java.util.Date;
import java.util.List;

/**
 * Request to create a new Upload share.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class CreateUploadShareRequest {

    private Long mTargetNodeId;
    private String mName;
    private String mNotes;
    private Date mExpirationDate;
    private Integer mFilesExpirationPeriod;
    private Boolean mShowUploadedFiles;
    private Boolean mNotifyCreator;
    private Integer mMaxUploads;
    private Long mMaxQuota;
    private String mAccessPassword;
    private Boolean mSendEmail;
    private List<String> mEmailRecipients;
    private String mEmailSubject;
    private String mEmailBody;
    private Boolean mSendSms;
    private List<String> mSmsRecipients;

    private CreateUploadShareRequest() {

    }

    /**
     * Returns the ID of the target node to which files should be uploaded.
     *
     * @return the ID of the target node
     */
    public Long getTargetNodeId() {
        return mTargetNodeId;
    }

    /**
     * Returns the name of the new upload share.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the notes of the new upload share.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Returns the expiration date of the new upload share.
     *
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return mExpirationDate;
    }

    /**
     * Returns the expiration period of uploaded files of the new upload share.
     *
     * @return the expiration period of uploaded files
     */
    public Integer getFilesExpirationPeriod() {
        return mFilesExpirationPeriod;
    }

    /**
     * Returns <code>true</code> if already uploaded files will be shown for the new upload share.
     *
     * @return <code>true</code> if already uploaded files will be shown; <code>false</code>
     * otherwise
     */
    public Boolean showUploadedFiles() {
        return mShowUploadedFiles;
    }

    /**
     * Returns <code>true</code> if creator will be notified at uploads to the new upload share.
     *
     * @return <code>true</code> if creator will be notified; <code>false</code> otherwise
     */
    public Boolean notifyCreator() {
        return mNotifyCreator;
    }

    /**
     * Returns the maximum number of uploads for the new upload share.
     *
     * @return the maximum number of uploads
     */
    public Integer getMaxUploads() {return mMaxUploads;}

    /**
     * Returns the maximum number of bytes which can be uploaded with the new upload share.
     *
     * @return the maximum number of bytes
     */
    public Long getMaxQuota() {return mMaxQuota;}

    /**
     * Returns the access password of the new upload share.
     *
     * @return the access password
     */
    public String getAccessPassword() {
        return mAccessPassword;
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
     * This builder creates new instances of {@link CreateUploadShareRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Target Node ID (mandatory): {@link #Builder(Long)}<br>
     * - Name:                       {@link #name(String)}<br>
     * - Notes:                      {@link #notes(String)}<br>
     * - Expiration date:            {@link #expirationDate(Date)}<br>
     * - Files expiration period:    {@link #filesExpirationPeriod(Integer)}<br>
     * - Maximum uploads:            {@link #maxUploads(Integer)}<br>
     * - Maximum quota:              {@link #maxQuota(Long)}<br>
     * - Show uploaded files:        {@link #showUploadedFiles(Boolean)}<br>
     * - Notify creator:             {@link #notifyCreator(Boolean)}<br>
     * - Access Password:            {@link #accessPassword(String)}<br>
     * - Email notification data:    {@link #sendEmail(List, String, String)}<br>
     * - SMS notification data:      {@link #sendSms(List)}
     */
    public static class Builder {

        private CreateUploadShareRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param targetNodeId The ID of the target node of the new upload share. (ID must be
         *                     positive.)
         */
        public Builder(Long targetNodeId) {
            mRequest = new CreateUploadShareRequest();
            mRequest.mTargetNodeId = targetNodeId;
            mRequest.mSendEmail = false;
            mRequest.mSendSms = false;
        }

        /**
         * Sets the name of the new upload share.
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
         * Sets the notes of the new upload share.
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
         * Sets the expiration date of the new upload share.
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
         * Sets the expiration period of uploaded files of the new upload share.
         *
         * @param filesExpirationPeriod The expiration period of uploaded files.
         *
         * @return a reference to this object
         */
        public Builder filesExpirationPeriod(Integer filesExpirationPeriod) {
            mRequest.mFilesExpirationPeriod = filesExpirationPeriod;
            return this;
        }

        /**
         * Enables/disables if already uploaded files will be shown for the new upload share.
         *
         * @param showUploadedFiles <code>true</code> to enable that already uploaded files will be
         *                          shown; otherwise <code>false</code>.
         *
         * @return a reference to this object
         */
        public Builder showUploadedFiles(Boolean showUploadedFiles) {
            mRequest.mShowUploadedFiles = showUploadedFiles;
            return this;
        }

        /**
         * Enables/disables notification of the creator at uploads to the new upload share.
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
         * Sets the access password of the new upload share.
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
         * Sets the maximum number of uploads for the new upload share.
         *
         * @param maxUploads The maximum number of uploads. (Number must be positive.)
         *
         * @return a reference to this object
         */
        public Builder maxUploads(Integer maxUploads) {
            mRequest.mMaxUploads = maxUploads;
            return this;
        }

        /**
         * Sets the maximum number of bytes which can be uploaded with the new upload share.
         *
         * @param maxQuota The maximum number of bytes. (Number must be positive.)
         *
         * @return a reference to this object
         */
        public Builder maxQuota(Long maxQuota) {
            mRequest.mMaxQuota = maxQuota;
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
         * Creates a new {@link CreateUploadShareRequest} instance with the supplied configuration.
         *
         * @return a new {@link CreateUploadShareRequest} instance
         */
        public CreateUploadShareRequest build() {
            return mRequest;
        }

    }

}
