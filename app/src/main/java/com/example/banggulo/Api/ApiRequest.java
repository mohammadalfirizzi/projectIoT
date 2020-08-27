package com.example.banggulo.Api;

import com.example.banggulo.Model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRequest {
    @GET("cek.php")
    Call<ResponseModel> lihat();
}
