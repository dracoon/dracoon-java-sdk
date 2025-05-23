package com.dracoon.sdk.internal.service;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.ClientImpl;
import com.dracoon.sdk.internal.ClientMethodImpl;
import com.dracoon.sdk.internal.api.mapper.ServerMapper;
import com.dracoon.sdk.internal.api.model.ApiServerClassificationPolicies;
import com.dracoon.sdk.internal.api.model.ApiServerPasswordPolicies;
import com.dracoon.sdk.model.ClassificationPolicies;
import com.dracoon.sdk.model.PasswordPolicies;
import retrofit2.Call;
import retrofit2.Response;

@ClientImpl(DracoonClient.ServerPolicies.class)
public class ServerPoliciesService extends BaseService {

    private static final String LOG_TAG = ServerPoliciesService.class.getSimpleName();

    public ServerPoliciesService(ServiceLocator locator, ServiceDependencies dependencies) {
        super(locator, dependencies);
    }

    @ClientMethodImpl
    public PasswordPolicies getEncryptionPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiEncryptionPasswordPolicies(
                passwordPolicies.encryptionPasswordPolicies);
    }

    @ClientMethodImpl
    public PasswordPolicies getSharesPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiSharesPasswordPolicies(passwordPolicies.sharesPasswordPolicies);
    }

    private ApiServerPasswordPolicies getPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerPasswordPolicies> call = mApi.getServerPasswordPolicies();
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

    @ClientMethodImpl
    public ClassificationPolicies getClassificationPolicies() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerClassificationPolicies> call = mApi.getServerClassificationPolicies();
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
