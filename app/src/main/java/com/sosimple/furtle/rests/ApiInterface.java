package com.sosimple.furtle.rests;

import com.sosimple.furtle.callbacks.CallBackSignup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: Furtle Backend";

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("user/signin")
    Call<CallBackSignup> signIn(
            @Body String body
    );

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("user/signup")
    Call<CallBackSignup> signUp(
            @Body String body
    );

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("user/google")
    Call<CallBackSignup> signInGoogle(
            @Body String body
    );

}
