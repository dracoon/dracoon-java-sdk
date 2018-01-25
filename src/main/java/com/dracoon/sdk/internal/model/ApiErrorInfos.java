package com.dracoon.sdk.internal.model;

public class ApiErrorInfos {

    public ApiConflictNode[] conflictNodes;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorInfos{conclictNodes=");
        if (conflictNodes != null && conflictNodes.length != 0) {
            sb.append("[");
            for (int i = 0; i < conflictNodes.length; i++) {
                sb.append(conflictNodes[i]);
                if (i < conflictNodes.length-1) {
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
