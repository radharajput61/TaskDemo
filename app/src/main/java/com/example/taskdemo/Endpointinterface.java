package com.example.taskdemo;

import com.example.taskdemo.dto.APIResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public interface Endpointinterface {
    @GET("users")
    Call<ArrayList<APIResponse>> getAllPhotos();
}