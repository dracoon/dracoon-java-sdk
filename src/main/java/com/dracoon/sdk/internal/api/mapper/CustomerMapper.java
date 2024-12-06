package com.dracoon.sdk.internal.api.mapper;

import com.dracoon.sdk.internal.api.model.ApiCustomerAccount;
import com.dracoon.sdk.model.CustomerAccount;

public class CustomerMapper extends BaseMapper {

    private CustomerMapper() {
        super();
    }

    public static CustomerAccount fromApiCustomerAccount(ApiCustomerAccount apiCustomerAccount) {
        if (apiCustomerAccount == null) {
            return null;
        }

        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(apiCustomerAccount.id);
        customerAccount.setName(apiCustomerAccount.name);
        customerAccount.setUserAccountsUsed(apiCustomerAccount.accountsUsed);
        customerAccount.setUserAccountsLimit(apiCustomerAccount.accountsLimit);
        customerAccount.setSpaceUsed(apiCustomerAccount.spaceUsed);
        customerAccount.setSpaceLimit(apiCustomerAccount.spaceLimit);
        customerAccount.setHasEncryptionEnabled(toBoolean(
                apiCustomerAccount.customerEncryptionEnabled));
        return customerAccount;
    }

}
