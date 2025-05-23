package com.dracoon.sdk.internal.api.model;

import java.util.List;

@SuppressWarnings({"unused", // Unused fields are for future usage (auto-generated)
        "squid:S1104" // SONAR: Creating getter/setter for this data class in an overkill
})
public class ApiServerCryptoAlgorithms {
    public List<ApiUserKeyPairAlgorithm> keyPairAlgorithms;
    public List<ApiFileKeyAlgorithm> fileKeyAlgorithms;
}
