package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiServerGeneralSettings {
    public Boolean sharePasswordSmsEnabled;
    public Boolean cryptoEnabled;
    public Boolean mediaServerEnabled;
    public Boolean weakPasswordEnabled;
    public Boolean useS3Storage;
}
