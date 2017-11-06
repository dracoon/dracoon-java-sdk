package com.dracoon.sdk.internal;

import com.dracoon.sdk.DracoonClient;
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

    private String mServerUrl;

    private OkHttpClient mOkHttpClient;
    private Gson mGson;
    private Retrofit mRetrofit;
    private DracoonService mDracoonService;

    private String mAccessToken;

    private Server mServer;
    private Account mAccount;
    private Users mUsers;
    private Groups mGroups;
    private Roles mRoles;
    private Permissions mPermissions;
    private Nodes mNodes;
    private Shares mShares;
    private Events mEvents;

    public DracoonClientImpl(String serverUrl) {
        mServerUrl = serverUrl;
    }

    public String getServerUrl() {
        return mServerUrl;
    }

    public void setServerUrl(String serverUrl) {
        mServerUrl = serverUrl;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public void init() {
        initOkHttp();
        initGson();
        initRetrofit();
        initDracoonService();

        mServer = new DracoonServerImpl(this);
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

    public DracoonService getDracoonService() {
        return mDracoonService;
    }

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
    public Roles roles() {
        return mRoles;
    }

    @Override
    public Permissions permissions() {
        return mPermissions;
    }

    @Override
    public Nodes nodes() {
        return mNodes;
    }

    @Override
    public Shares shares() {
        return mShares;
    }

    @Override
    public Events events() {
        return mEvents;
    }

}
