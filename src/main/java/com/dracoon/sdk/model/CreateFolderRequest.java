package com.dracoon.sdk.model;

public class CreateFolderRequest {

    private Long mParentId;
    private String mName;
    private String mNotes;

    private CreateFolderRequest() {

    }

    public Long getParentId() {
        return mParentId;
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public static class Builder {

        private CreateFolderRequest mRequest;

        public Builder(Long parentId, String name) {
            mRequest = new CreateFolderRequest();
            mRequest.mParentId = parentId;
            mRequest.mName = name;
        }

        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        public CreateFolderRequest build() {
            return mRequest;
        }

    }

}
