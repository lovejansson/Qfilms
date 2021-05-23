package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data class for response from TMDB Api
 * **/
public class GenreListResponse {

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;

    public GenreListResponse(){

    }

    public GenreListResponse(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        GenreListResponse other = (GenreListResponse) obj;

        return genres.equals(other.getGenres());
    }

}
