package com.example.qfilm.data.models.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * the result data class from TMDB Api, also used as data that is stored in firestore.
 *
 * this entity is representing movie/series in recyclerviews.
 *
 * **/

@Entity
public class Result {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @Expose
    public Integer resultId;

    @SerializedName(value= "title", alternate = {"name"})
    @Expose
    @NonNull
    private String title;

    @SerializedName(value = "original_title", alternate = {"original_name"})
    @Expose
    private String originalTitle;

    @SerializedName("popularity")
    @Expose
    @NonNull
    private Double popularity;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("overview")
    @Expose
    private String overview;

    private MediaType mediaType;

    private Integer timestamp;

    private String language; // to be able to tell if language has changed when fetching from Room


    @Ignore
    public Result(@NonNull Integer resultId, @NonNull String title, String originalTitle,
                  @NonNull Double popularity, String posterPath, String backdropPath, String overview,
                  Integer timestamp, MediaType mediaType) {
        this.resultId = resultId;
        this.title = title;
        this.originalTitle = originalTitle;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.timestamp = timestamp;
        this.mediaType = mediaType;
    }

    public Result(){

    }

    /**
     *
     * getters and setters
     *
    **/

    @NonNull
    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(@NonNull Integer resultId) {
        this.resultId = resultId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @NonNull
    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(@NonNull Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * override equals to compare objects by values
     *
     **/

    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Result other = (Result) obj;

        return resultId.equals(other.getResultId()) && title.equals(other.getTitle());

    }

}
