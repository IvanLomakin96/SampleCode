/*
 * Copyright (c) $2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.retrofit;

import com.airwatch.gateway.clients.AWOkHttpClient;
import com.airwatch.util.Logger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Creates Retrofit instance by using AWOkHttpClient.
 * tested: "https://reqres.in/"
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //Create OkHttpClient instance using AWOkHttpClient.
        // Here, it will use Android default trust store. To customize the SSL trust management,use
        //copyWithDefaults(okHttpClient,X509TrustManager) and implement X509TrustManager
        OkHttpClient awOkClient = AWOkHttpClient.copyWithDefaults(client);

        //Retrofit.Builder() expects a Client implementation.
        //Need to pass an OkHttpClient object as its parameter.

        if(!url.substring(url.length()-1).equals("/")){
            Logger.e("Retrofit", "baseUrl must end in /"+url);
            url = url+"/";
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(awOkClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
