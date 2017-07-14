package com.neoquest.voting.service;

import com.neoquest.voting.model.entity.Response;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiMethods {
    public static final String BASE_URL = "https://neovote.herokuapp.com/";

    @Headers("Content-Type: application/json")
    @POST("getReportsList/")
    Single<String> getReports(@Body String jsonString);

    @Headers("Content-Type: application/json")
    @POST("vote/")
    Single<String> vote(@Body String jsonString);
}
