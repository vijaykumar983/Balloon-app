package com.balloon.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
    public static String BASE_URL = "https://www.blueappsoftware.com/";


    public static RestApi getAPIService() {
        /*------------Use For Retrofit----------------------*/
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        client.readTimeoutMillis();
        // Change base URL to your upload server URL.
        RestApi uploadService = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestApi.class);
        return uploadService;
    }
}
