package com.neoquest.voting.service;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public enum  RetrofitService {
    INSTANCE;

    public static ApiMethods getService() {
        return INSTANCE.method;
    }

    private RxJava2CallAdapterFactory callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

    private ApiMethods method = new Retrofit.Builder()
            .baseUrl(ApiMethods.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(ApiMethods.class);
}
