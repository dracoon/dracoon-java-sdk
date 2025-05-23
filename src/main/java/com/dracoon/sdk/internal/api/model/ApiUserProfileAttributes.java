package com.dracoon.sdk.internal.api.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiUserProfileAttributes {

    public static class Item {
        public String key;
        public String value;
    }

    public List<Item> items;

}
