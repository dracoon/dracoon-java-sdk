package com.dracoon.sdk.model;

import java.util.List;

public class DeleteNodesRequest {

    private List<Long> mIds;

    private DeleteNodesRequest() {

    }

    public List<Long> getIds() {
        return mIds;
    }

    public static class Builder {

        private DeleteNodesRequest mRequest;

        public Builder(List<Long> ids) {
            mRequest = new DeleteNodesRequest();
            mRequest.mIds = ids;
        }

        public DeleteNodesRequest build() {
            return mRequest;
        }

    }

}
