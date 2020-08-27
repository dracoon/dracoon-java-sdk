package com.dracoon.sdk.internal.model;

import java.util.List;

public class ApiErrorInfos {

    public List<ApiConflictNode> conflictNodes;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorInfos{conclictNodes=");
        if (conflictNodes != null && conflictNodes.size() != 0) {
            sb.append("[");
            for (int i = 0; i < conflictNodes.size(); i++) {
                sb.append(conflictNodes.get(i));
                if (i < conflictNodes.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        } else if (conflictNodes != null) {
            sb.append("[]");
        } else {
            sb.append("null");
        }
        sb.append("}");
        return sb.toString();
    }

}
