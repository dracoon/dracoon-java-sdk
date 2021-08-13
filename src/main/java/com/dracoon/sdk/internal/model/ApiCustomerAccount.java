package com.dracoon.sdk.internal.model;

@SuppressWarnings("unused")
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
