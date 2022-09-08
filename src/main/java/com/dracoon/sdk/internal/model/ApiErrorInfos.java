package com.dracoon.sdk.internal.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "WeakerAccess", // Weaker access is not possible (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiErrorInfos {

    public List<ApiConflictNode> conflictNodes;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorInfos{conflictNodes=");
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
