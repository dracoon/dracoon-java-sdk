package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiEncryptionPasswordPolicies {
    public ApiCharacterRules characterRules;
    public Integer minLength;
    public Boolean rejectUserInfo;
    public Boolean rejectKeyboardPatterns;
}
