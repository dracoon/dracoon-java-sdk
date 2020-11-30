package com.dracoon.sdk.model;

/**
 * Request to update a node comment.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class UpdateNodeCommentRequest {

    private Long mId;
    private String mText;

    private UpdateNodeCommentRequest() {

    }

    /**
     * Returns the ID of the node comment.
     *
     * @return the ID of the node comment
     */
    public Long getId() {
        return mId;
    }

    /**
     * Returns the changed text of the node comment.
     *
     * @return the changed text
     */
    public String getText() {
        return mText;
    }

    /**
     * This builder creates new instances of {@link UpdateNodeCommentRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Comment ID (mandatory): {@link #Builder(Long, String)}<br>
     * - Text (mandatory):       {@link #Builder(Long, String)}
     */
    public static class Builder {

        private final UpdateNodeCommentRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param id   The ID of the node comment. (ID must be positive.)
         * @param text The changed text of the node comment. (Text must not be empty.)
         */
        public Builder(Long id, String text) {
            mRequest = new UpdateNodeCommentRequest();
            mRequest.mId = id;
            mRequest.mText = text;
        }

        /**
         * Creates a new {@link UpdateNodeCommentRequest} instance with the supplied configuration.
         *
         * @return a new {@link UpdateNodeCommentRequest} instance
         */
        public UpdateNodeCommentRequest build() {
            return mRequest;
        }

    }

}
