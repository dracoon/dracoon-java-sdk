package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.ServerMapper;
import com.dracoon.sdk.internal.model.ApiServerClassificationPolicies;
import com.dracoon.sdk.internal.model.ApiServerPasswordPolicies;
import com.dracoon.sdk.model.ClassificationPolicies;
import com.dracoon.sdk.model.PasswordPolicies;
import retrofit2.Call;
import retrofit2.Response;

class DracoonServerPoliciesImpl extends DracoonRequestHandler
        implements DracoonClient.ServerPolicies {

    private static final String LOG_TAG = DracoonServerPoliciesImpl.class.getSimpleName();

    DracoonServerPoliciesImpl(DracoonClientImpl client) {
        super(client);
    }

    @Override
    public PasswordPolicies getEncryptionPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiEncryptionPasswordPolicies(
                passwordPolicies.encryptionPasswordPolicies);
    }

    @Override
    public PasswordPolicies getSharesPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiSharesPasswordPolicies(passwordPolicies.sharesPasswordPolicies);
    }

    private ApiServerPasswordPolicies getPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerPasswordPolicies> call = mService.getServerPasswordPolicies();
        Response<ApiServerPasswordPolicies> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server password policies failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    @Override
    public ClassificationPolicies getClassificationPolicies() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiServerClassificationPolicies> call = mService.getServerClassificationPolicies();
        Response<ApiServerClassificationPolicies> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server classification policies failed " +
                    "with '%s'!", errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerClassificationPolicies data = response.body();

        return ServerMapper.fromApiClassificationPolicies(data);
    }

}
