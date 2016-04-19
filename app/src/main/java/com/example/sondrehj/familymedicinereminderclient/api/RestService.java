package com.example.sondrehj.familymedicinereminderclient.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nikolai on 05/04/16.
 */
public class RestService {
    private static final String BASE_URL = "http://10.22.47.44:1337";

    /**
     * Create an instance of the retrofit class which is instantiated with
     * the MyCyFAPPServiceAPI. This serviceApi is then returned.
     *
     * @return MyCyFAPPServiceAPI
     */
    public static MyCyFAPPServiceAPI createRestService() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyCyFAPPServiceAPI serviceAPI = retrofit.create(MyCyFAPPServiceAPI.class);

        return serviceAPI;
    }
}