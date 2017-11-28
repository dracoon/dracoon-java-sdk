package com.dracoon.sdk.model;

public class UpdateRoomRequest {

    private Long mId;
    private String mName;
    private Long mQuota;
    private String mNotes;

    private UpdateRoomRequest() {

    }

    public Long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Long getQuota() {
        return mQuota;
    }

    public String getNotes() {
        return mNotes;
    }

    public static class Builder {

        private UpdateRoomRequest mRequest;

        public Builder(Long id) {
            mRequest = new UpdateRoomRequest();
            mRequest.mId = id;
        }

        public Builder name(String name) {
            mRequest.mName = name;
            return this;
        }

        public Builder quota(Long quota) {
            mRequest.mQuota = quota;
            return this;
        }

        public Builder notes(String notes) {
            mRequest.mNotes = notes;
            return this;
        }

        public UpdateRoomRequest build() {
            return mRequest;
        }

    }

}
