package com.bioproj.service.executor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitFactory {
//    private String baseUrl; // = "https://api.example.com/";


    public static Retrofit createRetrofitInstance(String baseUrl, Interceptor interceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

//    public  static Retrofit createRetrofitInstance() {
//        return createRetrofitInstance(baseUrl, chain -> {
//            Request original = chain.request();
//            Request request = original.newBuilder()
//                    .method(original.method(), original.body())
//                    .build();
//            return chain.proceed(request);
//        });
//    }

    public static <T> T createService(String baseUrl, Interceptor interceptor,Class<T> serviceClass) {
        Retrofit retrofit = createRetrofitInstance(baseUrl, interceptor);
        return retrofit.create(serviceClass);
    }
}