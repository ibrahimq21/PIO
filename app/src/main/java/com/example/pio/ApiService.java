package com.example.pio;

import com.example.ptsdblibrary.PointProfileBean;

import java.util.List;

import retrofit2.Call;


import retrofit2.http.GET;


public interface ApiService {


    @GET("fetchPointdet.php")
    Call<List<PointProfileBean>> getPointProfilesDetails();
}
