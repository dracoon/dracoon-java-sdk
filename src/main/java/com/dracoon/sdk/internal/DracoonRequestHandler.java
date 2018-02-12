package com.dracoon.sdk.internal;

import java.util.Date;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiServerTime;
import com.dracoon.sdk.internal.model.ApiServerVersion;
import retrofit2.Call;
import retrofit2.Response;

public abstract class DracoonRequestHandler {

    private static final String LOG_TAG = DracoonRequestHandler.class.getSimpleName();

    protected final DracoonClientImpl mClient;
    protected final Log mLog;
    protected final DracoonService mService;
    protected final DracoonErrorParser mErrorParser;
    protected final HttpHelper mHttpHelper;

    private boolean mWasServerVersionChecked = false;

    public DracoonRequestHandler(DracoonClientImpl client) {
        mClient = client;
        mLog = client.getLog();
        mService = client.getDracoonService();
        mErrorParser = client.getDracoonErrorParser();
        mHttpHelper = client.getHttpHelper();
    }

    protected String getServerVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerVersionInternally().sdsServerVersion;
    }

    protected String getServerApiVersion() throws DracoonNetIOException, DracoonApiException {
        return getServerVersionInternally().restApiVersion;
    }

    protected void assertServerApiVersion() throws DracoonNetIOException, DracoonApiException {
        if (mWasServerVersionChecked) {
            return;
        }

        String ApiVersion = getServerApiVersion();
        String minApiVersion = DracoonConstants.API_MIN_VERSION;

        String[] av = ApiVersion.split("\\.");
        String[] mav = minApiVersion.split("\\.");

        for (int i = 0; i < 3; i++) {
            int v;
            int mv;

            try {
                v = Integer.valueOf(av[i]);
                mv = Integer.valueOf(mav[i]);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Can't parse server API version.", e);
            }

            if (v > mv) {
                break;
            } else if (v < mv) {
                throw new DracoonApiException(DracoonApiCode.API_VERSION_NOT_SUPPORTED);
            }
        }

        mWasServerVersionChecked = true;
    }

    protected Date getServerTime() throws DracoonNetIOException, DracoonApiException {
        return getServerTimeInternally().time;
    }

    // --- Helper methods ---

    private ApiServerVersion getServerVersionInternally() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerVersion> call = mService.getServerVersion();
        Response<ApiServerVersion> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server version failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

    private ApiServerTime getServerTimeInternally() throws DracoonNetIOException,
            DracoonApiException {
        Call<ApiServerTime> call = mService.getServerTime();
        Response<ApiServerTime> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseStandardError(response);
            String errorText = String.format("Query of server time failed with '%s'!",
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return response.body();
    }

}
