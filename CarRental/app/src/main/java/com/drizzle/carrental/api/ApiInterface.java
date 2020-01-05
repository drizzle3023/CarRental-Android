package com.drizzle.carrental.api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("api/sign-up")
    Call<ResponseBody> signUp(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/sign-in")
    Call<ResponseBody> signIn(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/sign-verify")
    Call<ResponseBody> signVerify(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-payment-methods")
    Call<ResponseBody> getPaymentMethods(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/do-payment")
    Call<ResponseBody> doPayment(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-active-coverage")
    Call<ResponseBody> getActiveCoverage(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-history-list")
    Call<ResponseBody> getHistoryList(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-user-profile")
    Call<ResponseBody> getUserProfile(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-car-type-list")
    Call<ResponseBody> getCarTypeList(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-near-company-list")
    Call<ResponseBody> getNearCompanyList(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/add-coverage")
    Call<ResponseBody> addCoverage(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/add-claim")
    Call<ResponseBody> addClaim(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/get-claim-list")
    Call<ResponseBody> getClaimList(@Body JsonObject body);
}
