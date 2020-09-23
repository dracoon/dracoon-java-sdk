package com.dracoon.sdk.internal.oauth;

import java.io.IOException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.internal.HttpStatus;
import com.dracoon.sdk.internal.NullLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Response;

public class OAuthErrorParser {

    private static final String LOG_TAG = OAuthErrorParser.class.getSimpleName();

    private static final String ERR_INVALID_REQUEST = "invalid_request";
    private static final String ERR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String ERR_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    private static final String ERR_INVALID_CLIENT = "invalid_client";
    private static final String ERR_INVALID_GRANT = "invalid_grant";
    private static final String ERR_INVALID_SCOPE = "invalid_scope";
    private static final String ERR_ACCESS_DENIED = "access_denied";

    private static final GsonBuilder sGsonBuilder = new GsonBuilder();

    private Log mLog = new NullLog();

    public OAuthErrorParser() {

    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public DracoonApiException parseAuthorizeError(String error) {
        DracoonApiCode code;

        switch (error) {
            case ERR_UNSUPPORTED_RESPONSE_TYPE:
                code = DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_REQUEST_INVALID;
                break;
            case ERR_INVALID_CLIENT:
                code = DracoonApiCode.AUTH_OAUTH_CLIENT_UNKNOWN;
                break;
            case ERR_INVALID_GRANT:
                code = DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED;
                break;
            case ERR_INVALID_SCOPE:
                code = DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_SCOPE_INVALID;
                break;
            case ERR_ACCESS_DENIED:
                code = DracoonApiCode.AUTH_OAUTH_AUTHORIZATION_ACCESS_DENIED;
                break;
            default:
                code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
        }

        return new DracoonApiException(code);
    }

    public DracoonApiException parseTokenError(Response response) {
        OAuthError errorResponse = getErrorResponse(response);

        String error = errorResponse != null ? errorResponse.error : "";

        DracoonApiCode code;

        switch (HttpStatus.valueOf(response.code())) {
            case BAD_REQUEST:
                switch (error) {
                    case ERR_INVALID_REQUEST:
                    case ERR_UNSUPPORTED_GRANT_TYPE:
                        code = DracoonApiCode.AUTH_OAUTH_TOKEN_REQUEST_INVALID;
                        break;
                    case ERR_INVALID_CLIENT:
                        code = DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED;
                        break;
                    case ERR_INVALID_GRANT:
                        code = DracoonApiCode.AUTH_OAUTH_TOKEN_CODE_INVALID;
                        break;
                    default:
                        code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
                }
                break;
            case UNAUTHORIZED:
                code = DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED;
                break;
            default:
                code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
        }

        return new DracoonApiException(code);
    }

    public DracoonApiException parseOAuthRefreshError(Response response) {
        OAuthError errorResponse = getErrorResponse(response);

        String error = errorResponse != null ? errorResponse.error : "";

        DracoonApiCode code;

        switch (HttpStatus.valueOf(response.code())) {
            case BAD_REQUEST:
                switch (error) {
                    case ERR_INVALID_REQUEST:
                    case ERR_UNSUPPORTED_GRANT_TYPE:
                        code = DracoonApiCode.AUTH_OAUTH_REFRESH_REQUEST_INVALID;
                        break;
                    case ERR_INVALID_CLIENT:
                        code = DracoonApiCode.AUTH_OAUTH_GRANT_TYPE_NOT_ALLOWED;
                        break;
                    case ERR_INVALID_GRANT:
                        code = DracoonApiCode.AUTH_OAUTH_REFRESH_TOKEN_INVALID;
                        break;
                    default:
                        code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
                }
                break;
            case UNAUTHORIZED:
                code = DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED;
                break;
            default:
                code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
        }

        return new DracoonApiException(code);
    }

    public DracoonApiException parseOAuthRevokeError(Response response) {
        DracoonApiCode code;

        switch (HttpStatus.valueOf(response.code())) {
            case BAD_REQUEST:
                code = DracoonApiCode.AUTH_OAUTH_REVOKE_REQUEST_INVALID;
                break;
            case UNAUTHORIZED:
                code = DracoonApiCode.AUTH_OAUTH_CLIENT_UNAUTHORIZED;
                break;
            default:
                code = DracoonApiCode.AUTH_UNKNOWN_ERROR;
        }

        return new DracoonApiException(code);
    }

    private OAuthError getErrorResponse(Response response) {
        if (response.errorBody() == null) {
            return null;
        }

        Gson gson = sGsonBuilder.create();
        try {
            OAuthError er = null;

            String type = response.errorBody().contentType().subtype();
            if (type.equals("json")) {
                er = gson.fromJson(response.errorBody().string(), OAuthError.class);
            }

            if (er != null) {
                mLog.d(LOG_TAG, "OAuth REST error:");
                mLog.d(LOG_TAG, er.toString());
            }

            return er;
        } catch (IOException e) {
            return null;
        }
    }

}
