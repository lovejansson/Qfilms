package com.example.qfilm.data.sources.remote;

import androidx.lifecycle.LiveData;

import com.example.qfilm.data.models.entities.GenreListResponse;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.repositories.utils.ApiResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbInterface {


    @GET("movie/{movie_id}")
    LiveData<ApiResponse<MovieDetails>> getMovieDetails(@Path("movie_id") int id,
                                                        @Query("language") String language,
                                                        @Query("include_image_language") String imageLanguage,
                                                        @Query("append_to_response") String appendToResponse);

    @GET("tv/{tv_id}")
    LiveData<ApiResponse<SeriesDetails>> getSeriesDetails(@Path("tv_id") int id,
                                                          @Query("language") String language,
                                                          @Query("include_image_language") String imageLanguage,
                                                          @Query("append_to_response") String appendToResponse);

    @GET("genre/movie/list")
    LiveData<ApiResponse<GenreListResponse>> getMovieGenresList(@Query("language") String language);


    @GET("genre/tv/list")
    LiveData<ApiResponse<GenreListResponse>> getSeriesGenresList(@Query("language") String language);

}
