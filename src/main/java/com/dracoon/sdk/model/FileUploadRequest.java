package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Request to upload a file.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class FileUploadRequest {

    private Long mParentId;
    private String mName;
    private ResolutionStrategy mResolutionStrategy;
    private Classification mClassification;
    private String mNotes;
    private Date mExpirationDate;

    private FileUploadRequest() {

    }

    /**
     * Returns the parent node ID of the new file.
     *
     * @return the parent node ID
     */
    public Long getParentId() {
        return mParentId;
    }

    /**
     * Returns the name of the new file.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the conflict resolution strategy for the upload.
     *
     * @return the conflict resolution strategy
     */
    public ResolutionStrategy getResolutionStrategy() {
        return mResolutionStrategy;
    }

    /**
     * Returns the classification of the new file.
     *
     * @return the classification
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * Returns the notes of the new file.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * Returns the expiration date of the new file.
     *
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return mExpirationDate;
    }

    /**
     * This builder creates new instances of {@link FileUploadRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Parent node ID (mandatory):   {@link #Builder(Long, String)}<br>
     * - Name (mandatory):             {@link #Builder(Long, String)}<br>
     * - Conflict resolution strategy: {@link #resolutionStrategy(ResolutionStrategy)}<br>
     * (Default: AUTO_RENAME)<br>
     * - Classification:               {@link #classification(Classification)}<br>
     * (Default: PUBLIC)<br>
     * - Notes:                        {@link #notes(String)}<br>
     * - Expiration date:              {@link #expirationDate(Date)}
     */
    public static class Builder {

        private FileUploadRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param parentId The ID of the parent node of the new file. (ID must be positive.)
         * @param name     The name of the new file. (Name must not be empty.)
         */
        public Builder(Long parentId, String name) {
            mRequest = new FileUploadRequest();
            mRequest.mParentId = parentId;
            mRequest.mName = name;
            mRequest.mResolutionStrategy = ResolutionStrategy.AUTO_RENAME;
            mRequest.mClassification = Classification.PUBLIC;
        }

        /**
         * Sets the conflict resolution strategy for the file upload.
         *
         * @param resolutionStrategy The conflict resolution strategy. (Default:
         *                           {@link ResolutionStrategy#AUTO_RENAME AUTO_RENAME})
         *
         * @return a reference to this object
         */
        public Builder resolutionStrategy(ResolutionStrategy resolutionStrategy) {
            mRequest.mResolutionStrategy = resolutionStrategy;
            return this;
        }

        /**
         * Sets the classification of the new file.
         *
         * @param classification The classification. (Default: {@link Classification#PUBLIC PUBLIC})
         *
         * @return a reference to this object
         */
        public Builder classification(Classification classification) {
            mRequest.mClassification = classification;
            return this;
        }

        /**
         * Sets the notes of the new file.
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
         * Sets the expiration date of the new file.
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
         * Creates a new {@link FileUploadRequest} instance with the supplied configuration.
         *
         * @return a new {@link FileUploadRequest} instance
         */
        public FileUploadRequest build() {
            return mRequest;
        }

    }

}
