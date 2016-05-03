package com.example.sondrehj.familymedicinereminderclient.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nikolai on 05/04/16.
 */
public class RestService {
    private static final String BASE_URL = "http://10.0.0.178:1337";

    private static OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
        .readTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS);

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());


    /**
     * Create an instance of the retrofit class which is instantiated with
     * the MyCyFAPPServiceAPI. This serviceApi is then returned.
     *
     * @return MyCyFAPPServiceAPI
     */
    public static MyCyFAPPServiceAPI createRestService(final String authToken) {

        if (authToken != null) {
            httpBuilder.addInterceptor((Interceptor.Chain chain) -> {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("access_token", authToken)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }

        OkHttpClient httpClient = httpBuilder.build();
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(MyCyFAPPServiceAPI.class);
    }

    public static MyCyFAPPServiceAPI createRestService() {
        return createRestService(null);
    }
}