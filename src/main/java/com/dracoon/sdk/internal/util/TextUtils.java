package com.dracoon.sdk.internal.util;

import java.util.List;

public class TextUtils {

    public static String join(List<String> strings) {
        if (strings == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i < strings.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
