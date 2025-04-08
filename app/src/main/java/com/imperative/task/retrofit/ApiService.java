package com.imperative.task.retrofit;

import com.imperative.task.data.request.LoginRequest;
import com.imperative.task.data.response.LoginResponse;
import com.imperative.task.data.response.Transaction;
import com.imperative.task.data.response.TransactionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("transactions")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String authHeader);
}
