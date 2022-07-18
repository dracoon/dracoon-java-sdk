package com.dracoon.sdk.internal.model;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiCustomerAccount {
    public Long id;
    public String name;

    public Integer accountsUsed;
    public Integer accountsLimit;
    public Long spaceUsed;
    public Long spaceLimit;

    public Boolean customerEncryptionEnabled;

    public Boolean isProviderCustomer;
}
