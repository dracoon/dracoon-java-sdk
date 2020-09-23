package com.dracoon.sdk.internal;

import java.util.Arrays;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.mapper.ServerMapper;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.model.ApiServerPasswordPolicies;
import com.dracoon.sdk.model.PasswordPolicies;
import com.dracoon.sdk.model.PasswordPoliciesCharacterType;
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
        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_PASSWORD_POLICIES)) {
            return getFallbackPasswordPolicies();
        }

        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiEncryptionPasswordPolicies(
                passwordPolicies.encryptionPasswordPolicies);
    }

    @Override
    public PasswordPolicies getSharesPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        if (!mClient.isApiVersionGreaterEqual(DracoonConstants.API_MIN_PASSWORD_POLICIES)) {
            return getFallbackPasswordPolicies();
        }

        ApiServerPasswordPolicies passwordPolicies = getPasswordPolicies();
        return ServerMapper.fromApiSharesPasswordPolicies(passwordPolicies.sharesPasswordPolicies);
    }

    private ApiServerPasswordPolicies getPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

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

    private PasswordPolicies getFallbackPasswordPolicies() throws DracoonNetIOException,
            DracoonApiException {
        mClient.assertApiVersionSupported();

        Call<ApiServerGeneralSettings> call = mService.getServerGeneralSettings();
        Response<ApiServerGeneralSettings> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server password policies failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiServerGeneralSettings data = response.body();

        if (data.weakPasswordEnabled != null && data.weakPasswordEnabled) {
            return createWeakPasswordPolicies();
        } else {
            return createStrongPasswordPolicies();
        }
    }

    private static PasswordPolicies createWeakPasswordPolicies() {
        PasswordPolicies policies = new PasswordPolicies();
        policies.setMinLength(8);
        policies.setCharacterTypes(Arrays.asList(PasswordPoliciesCharacterType.ALPHA));
        policies.setRejectUserInfo(false);
        policies.setRejectKeyboardPatterns(false);
        policies.setRejectDictionaryWords(false);
        return policies;
    }

    private static PasswordPolicies createStrongPasswordPolicies() {
        PasswordPolicies policies = new PasswordPolicies();
        policies.setMinLength(8);
        policies.setCharacterTypes(Arrays.asList(PasswordPoliciesCharacterType.UPPERCASE,
                PasswordPoliciesCharacterType.LOWERCASE,
                PasswordPoliciesCharacterType.NUMERIC,
                PasswordPoliciesCharacterType.SPECIAL));
        policies.setRejectUserInfo(false);
        policies.setRejectKeyboardPatterns(false);
        policies.setRejectDictionaryWords(false);
        return policies;
    }

}
