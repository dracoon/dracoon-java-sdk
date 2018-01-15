package com.dracoon.sdk.model;

import java.util.List;

/**
 * Request to delete nodes.<br>
 * <br>
 * A new instance can be created with {@link DeleteNodesRequest.Builder Builder}.
 */
@SuppressWarnings("unused")
public class DeleteNodesRequest {

    private List<Long> mIds;

    private DeleteNodesRequest() {

    }

    /**
     * Returns IDs of nodes which should be deleted.
     *
     * @return The node IDs.
     */
    public List<Long> getIds() {
        return mIds;
    }

    /**
     * This builder creates new instances of {@link DeleteNodesRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node IDs (mandatory): {@link Builder#Builder(List)}<br>
     */
    public static class Builder {

        private DeleteNodesRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param ids IDs of the nodes which should be deleted.
         */
        public Builder(List<Long> ids) {
            mRequest = new DeleteNodesRequest();
            mRequest.mIds = ids;
        }

        /**
         * Creates a new {@link DeleteNodesRequest} instance with the supplied configuration.
         *
         * @return a new {@link DeleteNodesRequest} instance
         */
        public DeleteNodesRequest build() {
            return mRequest;
        }

    }

}
