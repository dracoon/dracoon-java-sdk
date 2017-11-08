package com.dracoon.sdk.model;

import java.util.Date;

public class UploadRequest {

    private Long mParentId;
    private String mName;
    private ResolutionStrategy mResolutionStrategy;
    private Date mExpiration;
    private Classification mClassification;
    private String mNotes;

    private UploadRequest() {

    }

    public Long getParentId() {
        return mParentId;
    }

    public String getName() {
        return mName;
    }

    public ResolutionStrategy getResolutionStrategy() {
        return mResolutionStrategy;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public Classification getClassification() {
        return mClassification;
    }

    public String getNotes() {
        return mNotes;
    }

    public static class Builder {

        private UploadRequest mRequest;

        public Builder(long parentId, String name) {
            mRequest = new UploadRequest();
            mRequest.mParentId = parentId;
            mRequest.mName = name;
            mRequest.mResolutionStrategy = ResolutionStrategy.AUTO_RENAME;
            mRequest.mClassification = Classification.PUBLIC;
        }

        public Builder resolutionStrategy(ResolutionStrategy resolutionStrategy) {
            mRequest.mResolutionStrategy = resolutionStrategy;
            return this;
        }

        public Builder expiration(Date expiration) {
            mRequest.mExpiration = expiration;
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

        public UploadRequest build() {
            return mRequest;
        }

    }

}
