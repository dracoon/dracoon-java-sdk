package com.dracoon.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Request to copy nodes.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class CopyNodesRequest {

    private Long mTargetNodeId;
    private List<Long> mSourceNodeIds;
    private ResolutionStrategy mResolutionStrategy;

    private CopyNodesRequest() {

    }

    /**
     * Returns the ID of the target node to which should be copied.
     *
     * @return the ID of the target node
     */
    public Long getTargetNodeId() {
        return mTargetNodeId;
    }

    /**
     * Returns the IDs of the source nodes which should be copied.
     *
     * @return the IDs of the source nodes
     */
    public List<Long> getSourceNodeIds() {
        return mSourceNodeIds;
    }

    /**
     * Returns the conflict resolution strategy for the copy operation.
     *
     * @return the conflict resolution strategy
     */
    public ResolutionStrategy getResolutionStrategy() {
        return mResolutionStrategy;
    }

    /**
     * This builder creates new instances of {@link CopyNodesRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Target node ID (mandatory):   {@link #Builder(Long)}<br>
     * - Source node IDs               {@link #addSourceNodeId(Long)}<br>
     * - Conflict resolution strategy: {@link #resolutionStrategy(ResolutionStrategy)}<br>
     * (Default: <code>AUTO_RENAME</code>)
     */
    public static class Builder {

        private CopyNodesRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param targetNodeId The ID of the target node to which should be copied. (ID must be
         *                     positive.)
         */
        public Builder(Long targetNodeId) {
            mRequest = new CopyNodesRequest();
            mRequest.mTargetNodeId = targetNodeId;
            mRequest.mSourceNodeIds = new ArrayList<>();
            mRequest.mResolutionStrategy = ResolutionStrategy.AUTO_RENAME;
        }

        /**
         * Adds the ID of a node which should be copied.
         *
         * @param sourceNodeId The ID of a node.
         *
         * @return a reference to this object
         */
        public Builder addSourceNodeId(Long sourceNodeId) {
            mRequest.mSourceNodeIds.add(sourceNodeId);
            return this;
        }

        /**
         * Sets the conflict resolution strategy for the copy operation.
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
         * Creates a new {@link CopyNodesRequest} instance with the supplied configuration.
         *
         * @return a new {@link CopyNodesRequest} instance
         */
        public CopyNodesRequest build() {
            return mRequest;
        }

    }

}
