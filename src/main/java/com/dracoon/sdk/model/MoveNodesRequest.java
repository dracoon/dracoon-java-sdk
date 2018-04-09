package com.dracoon.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Request to move nodes.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class MoveNodesRequest {

    /**
     * This model class stores information about a node which should be moved.
     */
    public static class SourceNode {

        private Long mId;
        private String mName;

        SourceNode(Long id) {
            mId = id;
        }

        SourceNode(Long id, String name) {
            mId = id;
            mName = name;
        }

        /**
         * Returns the ID of the source node.
         *
         * @return the ID of the source node
         */
        public Long getId() {
            return mId;
        }

        /**
         * Returns the new name of the source node.
         *
         * @return the new name of the source node
         */
        public String getName() {
            return mName;
        }

    }

    private Long mTargetNodeId;
    private List<SourceNode> mSourceNodes;
    private ResolutionStrategy mResolutionStrategy;

    private MoveNodesRequest() {

    }

    /**
     * Returns the ID of the target node to which should be moved.
     *
     * @return the ID of the target node
     */
    public Long getTargetNodeId() {
        return mTargetNodeId;
    }

    /**
     * Returns information about the source nodes which should be moved.
     *
     * @return information about the source nodes
     */
    public List<SourceNode> getSourceNodes() {
        return mSourceNodes;
    }

    /**
     * Returns the conflict resolution strategy for the move operation.
     *
     * @return the conflict resolution strategy
     */
    public ResolutionStrategy getResolutionStrategy() {
        return mResolutionStrategy;
    }

    /**
     * This builder creates new instances of {@link MoveNodesRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Target node ID (mandatory):   {@link #Builder(Long)}<br>
     * - Source nodes information:     {@link #addSourceNode(Long)},
     *                                 {@link #addSourceNode(Long, String)}<br>
     * - Conflict resolution strategy: {@link #resolutionStrategy(ResolutionStrategy)}<br>
     * (Default: AUTO_RENAME)
     */
    public static class Builder {

        private MoveNodesRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param targetNodeId The ID of the target node to which should be moved. (ID must be
         *                     positive.)
         */
        public Builder(Long targetNodeId) {
            mRequest = new MoveNodesRequest();
            mRequest.mTargetNodeId = targetNodeId;
            mRequest.mSourceNodes = new ArrayList<>();
            mRequest.mResolutionStrategy = ResolutionStrategy.AUTO_RENAME;
        }

        /**
         * Adds information about a node which should be moved.
         *
         * @param sourceNodeId The ID of the node.
         *
         * @return a reference to this object
         */
        public Builder addSourceNode(Long sourceNodeId) {
            mRequest.mSourceNodes.add(new SourceNode(sourceNodeId));
            return this;
        }

        /**
         * Adds information about a node which should be moved.
         *
         * @param sourceNodeId The ID of the node.
         * @param newNodeName  The new name of the the node.
         *
         * @return a reference to this object
         */
        public Builder addSourceNode(Long sourceNodeId, String newNodeName) {
            mRequest.mSourceNodes.add(new SourceNode(sourceNodeId, newNodeName));
            return this;
        }

        /**
         * Sets the conflict resolution strategy for the move operation.
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
         * Creates a new {@link MoveNodesRequest} instance with the supplied configuration.
         *
         * @return a new {@link MoveNodesRequest} instance
         */
        public MoveNodesRequest build() {
            return mRequest;
        }

    }

}
