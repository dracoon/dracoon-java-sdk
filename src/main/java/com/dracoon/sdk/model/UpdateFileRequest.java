package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Request to update a file.<br>
 * <br>
 * A new instance can be created with {@link UpdateFileRequest.Builder Builder}.
 */
@SuppressWarnings("unused")
public class UpdateFileRequest {

    private Long mId;
    private String mName;
    private Classification mClassification;
    private String mNotes;
    private Date mExpirationDate;

    private UpdateFileRequest() {

    }

    /**
     * Returns the node ID of the file which should be updated.
     *
     * @return the node ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Returns the new name of the file.
     *
     * @return the new name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the new classification of the file.
     *
     * @return the new classification
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * Returns the new notes of the file.
     *
     * @return the new notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Returns the new expiration date of the file.
     *
     * @return the new expiration date
     */
    public Date getExpirationDate() {
        return mExpirationDate;
    }

    /**
     * This builder creates new instances of {@link UpdateFileRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node ID (mandatory): {@link Builder#Builder(Long)}<br>
     * - Name:                {@link Builder#name(String)}<br>
     * - Classification:      {@link Builder#classification(Classification)}<br>
     * - Notes:               {@link Builder#notes(String)}<br>
     * - Expiration date:     {@link Builder#expirationDate(Date)}
     */
    public static class Builder {

        private UpdateFileRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param id The node ID of the file which should be updated. (ID must be positive.)
         */
        public Builder(Long id) {
            mRequest = new UpdateFileRequest();
            mRequest.mId = id;
        }

        /**
         * Sets the new name of the file.
         *
         * @param name The new name. (Name must not be empty.)
         *
         * @return a reference to this object
         */
        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        /**
         * Sets the new classification of the file.
         *
         * @param classification The new classification.
         *
         * @return a reference to this object
         */
        public Builder classification(Classification classification) {
            mRequest.mClassification = classification;
            return this;
        }

        /**
         * Sets the new notes of the file.<br>
         * <br>
         * A empty string removes existing notes.
         *
         * @param notes The new notes.
         *
         * @return a reference to this object
         */
        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        /**
         * Sets the new expiration date of the file.<br>
         * <br>
         * A date with time 0 removes the existing expiration date.
         *
         * @param expirationDate The new expiration date.
         *
         * @return a reference to this object
         */
        public Builder expirationDate(Date expirationDate) {
            mRequest.mExpirationDate = expirationDate;
            return this;
        }

        /**
         * Creates a new {@link UpdateFileRequest} instance with the supplied configuration.
         *
         * @return a new {@link UpdateFileRequest} instance
         */
        public UpdateFileRequest build() {
            return mRequest;
        }

    }

}
