package com.dracoon.sdk.model;

/**
 * Request to update a folder.<br>
 * <br>
 * A new instance can be created with {@link UpdateFolderRequest.Builder Builder}.
 */
@SuppressWarnings("unused")
public class UpdateFolderRequest {

    private Long mId;
    private String mName;
    private String mNotes;

    private UpdateFolderRequest() {

    }

    /**
     * Returns the ID of the folder which should be updated.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Returns the new name of the folder.
     *
     * @return the new name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the new notes of the folder.
     *
     * @return the new notes
     */
    public String getNotes() {
        return mNotes;
    }

    /**
     * This builder creates new instances of {@link UpdateFolderRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node ID (mandatory): {@link Builder#Builder(Long)}<br>
     * - Name:                {@link Builder#name(String)}<br>
     * - Notes:               {@link Builder#notes(String)}
     */
    public static class Builder {

        private UpdateFolderRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param id The node ID of the folder which should be updated.
         */
        public Builder(Long id) {
            mRequest = new UpdateFolderRequest();
            mRequest.mId = id;
        }

        /**
         * Sets the new name of the folder.
         *
         * @param name The new name.
         *
         * @return a reference to this object
         */
        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        /**
         * Sets the new notes of the folder.
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
         * Creates a new {@link UpdateFolderRequest} instance with the supplied configuration.
         *
         * @return a new {@link UpdateFolderRequest} instance
         */
        public UpdateFolderRequest build() {
            return mRequest;
        }

    }

}
