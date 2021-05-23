package com.example.qfilm.data.sources.remote;

import androidx.lifecycle.LiveData;

import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.repositories.utils.ApiResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbInterface {

    @GET("/")
    LiveData<ApiResponse<Ratings>> getRatingsByTitle(@Query("t") String title);

}
