package com.dracoon.sdk.model;

/**
 * Request to create a new folder.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class CreateFolderRequest {

    private Long mParentId;
    private String mName;
    private Classification mClassification;
    private String mNotes;

    private CreateFolderRequest() {

    }

    /**
     * Returns the parent node ID of the new folder.
     *
     * @return the parent node ID
     */
    public Long getParentId() {
        return mParentId;
    }

    /**
     * Returns the name of the new folder.
     *
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the classification of the new folder.
     *
     * @return the classification
     */
    public Classification getClassification() {
        return mClassification;
    }

    /**
     * Returns the notes of the new folder.
     *
     * @return the notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * This builder creates new instances of {@link CreateFolderRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Parent node ID (mandatory): {@link #Builder(Long, String)}<br>
     * - Name (mandatory):           {@link #Builder(Long, String)}<br>
     * - Classification:             {@link #classification(Classification)}<br>
     * - Notes:                      {@link #notes(String)}
     */
    public static class Builder {

        private final CreateFolderRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param parentId The ID of the parent node of the new folder.
         * @param name     The name of the new folder. (Name must not be empty and cannot contain
         *                 '<', '>', ':', '"', '|', '?', '*', '/', '\'.)
         */
        public Builder(Long parentId, String name) {
            mRequest = new CreateFolderRequest();
            mRequest.mParentId = parentId;
            mRequest.mName = name;
        }

        /**
         * Sets the classification of the new folder.
         *
         * @param classification The classification.
         *
         * @return a reference to this object
         */
        public Builder classification(Classification classification) {
            mRequest.mClassification = classification;
            return this;
        }

        /**
         * Sets the nodes of the new folder.
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
         * Creates a new {@link CreateFolderRequest} instance with the supplied configuration.
         *
         * @return a new {@link CreateFolderRequest} instance
         */
        public CreateFolderRequest build() {
            return mRequest;
        }

    }

}
