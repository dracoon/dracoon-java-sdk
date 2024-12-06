package com.dracoon.sdk.internal.service;

import java.util.Date;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.ClientImpl;
import com.dracoon.sdk.internal.ClientMethodImpl;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.DracoonConstants;
import com.dracoon.sdk.internal.api.model.ApiServerInfo;
import com.dracoon.sdk.internal.api.model.ApiServerTime;
import com.dracoon.sdk.internal.util.VersionUtils;
import retrofit2.Call;
import retrofit2.Response;

@ClientImpl(DracoonClient.Server.class)
public class ServerInfoService extends BaseService {

    private static final String LOG_TAG = ServerInfoService.class.getSimpleName();

    private ApiServerInfo mCachedServerInfo;

    public ServerInfoService(DracoonClientImpl client) {
        super(client);
    }

    public void checkVersionSupported() throws DracoonNetIOException,
            DracoonApiException {
        if (!isVersionGreaterEqual(DracoonConstants.API_MIN_VERSION)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUPPORTED);
        }
    }

    public void checkVersionGreaterEqual(String version) throws DracoonNetIOException,
            DracoonApiException {
        if (!isVersionGreaterEqual(version)) {
            throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUFFICIENT);
        }
    }

    private boolean isVersionGreaterEqual(String version) throws DracoonNetIOException,
            DracoonApiException {
        String actualVersion = getVersion();
        return VersionUtils.isVersionGreaterEqual(actualVersion, version);
    }

    @ClientMethodImpl
    public String getVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().restApiVersion;
    }

    @ClientMethodImpl
    public Boolean isDracoonCloud() throws DracoonNetIOException, DracoonApiException {
        return getServerInfo().isDracoonCloud;
    }

    private ApiServerInfo getServerInfo() throws DracoonNetIOException, DracoonApiException {
        if (mCachedServerInfo != null) {
            return mCachedServerInfo;
        }

        Call<ApiServerInfo> call = mApi.getServerInfo();
        Response<ApiServerInfo> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseServerInfoQueryError(response);
            String errorText = String.format("Query of server info failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        mCachedServerInfo = response.body();

        return mCachedServerInfo;
    }

    @ClientMethodImpl
    public Date getTime() throws DracoonNetIOException, DracoonApiException {
        Call<ApiServerTime> call = mApi.getServerTime();
        Response<ApiServerTime> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server time failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body().time;
    }

}
