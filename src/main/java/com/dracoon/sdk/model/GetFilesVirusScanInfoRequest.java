package com.dracoon.sdk.model;

import java.util.List;

/**
 * Request to retrieve virus scan information of files.<br>
 * <br>
 * A new instance can be created with {@link Builder}.
 */
@SuppressWarnings("unused")
public class GetFilesVirusScanInfoRequest {

    private List<Long> mIds;

    private GetFilesVirusScanInfoRequest() {

    }

    /**
     * Returns node IDs of files for which virus scan information should be retrieved.
     *
     * @return The node IDs.
     */
    public List<Long> getIds() {
        return mIds;
    }

    /**
     * This builder creates new instances of {@link GetFilesVirusScanInfoRequest}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Node IDs (mandatory): {@link #Builder(List)}<br>
     */
    public static class Builder {

        private final GetFilesVirusScanInfoRequest mRequest;

        /**
         * Constructs a new builder.
         *
         * @param ids The node IDs of files for which virus scan information should be retrieved.
         */
        public Builder(List<Long> ids) {
            mRequest = new GetFilesVirusScanInfoRequest();
            mRequest.mIds = ids;
        }

        /**
         * Creates a new {@link GetFilesVirusScanInfoRequest} instance with the supplied
         * configuration.
         *
         * @return a new {@link GetFilesVirusScanInfoRequest} instance
         */
        public GetFilesVirusScanInfoRequest build() {
            return mRequest;
        }

    }

}
