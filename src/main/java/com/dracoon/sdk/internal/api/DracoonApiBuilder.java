package com.dracoon.sdk.internal.api;

import java.net.URL;

import com.dracoon.sdk.internal.auth.AuthInterceptor;
import com.dracoon.sdk.internal.util.GsonCharArrayTypeAdapter;
import com.dracoon.sdk.internal.util.GsonDateTypeAdapter;
import com.dracoon.sdk.internal.util.GsonVoidTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DracoonApiBuilder {

    public DracoonApi build(URL serverUrl, OkHttpClient httpClient, AuthInterceptor authInterceptor) {
        OkHttpClient extendedHttpClient = httpClient.newBuilder()
                .followRedirects(false)
                .addInterceptor(authInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GsonVoidTypeAdapter.TYPE, new GsonVoidTypeAdapter())
                .registerTypeAdapter(GsonDateTypeAdapter.TYPE, new GsonDateTypeAdapter())
                .registerTypeAdapter(GsonCharArrayTypeAdapter.TYPE, new GsonCharArrayTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl.toString())
                .client(extendedHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(DracoonApi.class);
    }

}
