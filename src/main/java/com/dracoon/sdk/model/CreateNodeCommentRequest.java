package com.dracoon.sdk.model;

/**
 * Request to create a new node comment.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class CreateNodeCommentRequest {

    private Long mNodeId;
    private String mText;

    private CreateNodeCommentRequest() {

    }

    /**
     * Returns the target node ID of the new node comment.
     *
     * @return the target node ID
     */
    public Long getNodeId() {
        return mNodeId;
    }

    /**
     * Returns the text of the new node comment.
     *
     * @return the text
     */
    public String getText() {
        return mText;
    }

    /**
     * This builder creates new instances of {@link CreateNodeCommentRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node ID (mandatory): {@link #Builder(Long, String)}<br>
     * - Text (mandatory):    {@link #Builder(Long, String)}
     */
    public static class Builder {

        private final CreateNodeCommentRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param nodeId The target node ID of the new node comment. (ID must be positive.)
         * @param text   The text of the new node comment. (Text must not be empty.)
         */
        public Builder(Long nodeId, String text) {
            mRequest = new CreateNodeCommentRequest();
            mRequest.mNodeId = nodeId;
            mRequest.mText = text;
        }

        /**
         * Creates a new {@link CreateNodeCommentRequest} instance with the supplied configuration.
         *
         * @return a new {@link CreateNodeCommentRequest} instance
         */
        public CreateNodeCommentRequest build() {
            return mRequest;
        }

    }

}
