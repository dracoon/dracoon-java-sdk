package com.dracoon.sdk.model;

import java.util.Date;

public class UpdateFileRequest {

    private Long mId;
    private String mName;
    private Classification mClassification;
    private String mNotes;
    private Date mExpiration;

    private UpdateFileRequest() {

    }

    public Long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Classification getClassification() {
        return mClassification;
    }

    public String getNotes() {
        return mNotes;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public static class Builder {

        private UpdateFileRequest mRequest;

        public Builder(Long id) {
            mRequest = new UpdateFileRequest();
            mRequest.mId = id;
        }

        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        public Builder classification(Classification classification) {
            mRequest.mClassification = classification;
            return this;
        }

        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        public Builder expiration(Date expiration) {
            mRequest.mExpiration = expiration;
            return this;
        }

        public UpdateFileRequest build() {
            return mRequest;
        }

    }

}
