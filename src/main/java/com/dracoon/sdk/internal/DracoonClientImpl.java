package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
import com.dracoon.sdk.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class DracoonClientImpl extends DracoonClient {

    private Log mLog = new NullLog();

    private OkHttpClient mOkHttpClient;
    private Gson mGson;
    private Retrofit mRetrofit;
    private DracoonService mDracoonService;
    private DracoonHttpHelper mDracoonHttpHelper;
    private DracoonErrorParser mDracoonErrorParser;

    private DracoonServerImpl mServer;
    private DracoonAccountImpl mAccount;
    private Users mUsers;
    private Groups mGroups;
    private DracoonNodesImpl mNodes;
    private Shares mShares;

    public DracoonClientImpl(String serverUrl) {
        super(serverUrl);
    }

    public void setLog(Log log) {
        mLog = log != null ? log : new NullLog();
    }

    public Log getLog() {
        return mLog;
    }

    public DracoonService getDracoonService() {
        return mDracoonService;
    }

    public DracoonHttpHelper getDracoonHttpHelper() {
        return mDracoonHttpHelper;
    }

    public DracoonErrorParser getDracoonErrorParser() {
        return mDracoonErrorParser;
    }

    // --- Initialization methods ---

    public void init() {
        initOkHttp();
        initGson();
        initRetrofit();
        initDracoonService();
        initDracoonHttpHelper();
        initDracoonErrorParser();

        mServer = new DracoonServerImpl(this);
        mAccount = new DracoonAccountImpl(this);
        mNodes = new DracoonNodesImpl(this);
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        mOkHttpClient = builder.build();
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Void.class, new JsonDeserializer<Void>() {
            @Override
            public Void deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                return null;
            }
        });
        mGson = gsonBuilder.create();
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
    }

    private void initDracoonService() {
        mDracoonService = mRetrofit.create(DracoonService.class);
    }

    private void initDracoonHttpHelper() {
        mDracoonHttpHelper = new DracoonHttpHelper(mLog);
    }

    private void initDracoonErrorParser() {
        mDracoonErrorParser = new DracoonErrorParser(mLog);
    }

    // --- Methods to get public handlers ---

    @Override
    public Server server() {
        return mServer;
    }

    @Override
    public Account account() {
        return mAccount;
    }

    @Override
    public Users users() {
        return mUsers;
    }

    @Override
    public Groups groups() {
        return mGroups;
    }

    @Override
    public Nodes nodes() {
        return mNodes;
    }

    @Override
    public Shares shares() {
        return mShares;
    }

    /// --- Methods to get internal handlers ---

    public DracoonServerImpl getServerImpl() {
        return mServer;
    }

    public DracoonAccountImpl getAccountImpl() {
        return mAccount;
    }

    public DracoonNodesImpl getNodesImpl() {
        return mNodes;
    }

    // --- Helper methods ---

    public String buildApiUrl(String... pathSegments) {
        StringBuilder sb = new StringBuilder();
        sb.append(mServerUrl);
        sb.append(DracoonService.API_PATH);
        for (String pathSegment : pathSegments) {
            sb.append("/").append(pathSegment);
        }
        return sb.toString();
    }

}
