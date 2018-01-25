package com.dracoon.sdk.internal.model;

public class ApiCustomerAccount {
    public Long id;
    public String name;

    public Integer accountsUsed;
    public Integer accountsLimit;
    public Long spaceUsed;
    public Long spaceLimit;
    public Long cntRooms;
    public Long cntFolders;
    public Long cntFiles;

    public Boolean customerEncryptionEnabled;

    public Boolean isProviderCustomer;
}
