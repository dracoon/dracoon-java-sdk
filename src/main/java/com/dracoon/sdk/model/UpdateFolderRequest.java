package com.dracoon.sdk.model;

public class UpdateFolderRequest {

    private Long mId;
    private String mName;
    private String mNotes;

    private UpdateFolderRequest() {

    }

    public Long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public static class Builder {

        private UpdateFolderRequest mRequest;

        public Builder(Long id) {
            mRequest = new UpdateFolderRequest();
            mRequest.mId = id;
        }

        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        public UpdateFolderRequest build() {
            return mRequest;
        }

    }

}
