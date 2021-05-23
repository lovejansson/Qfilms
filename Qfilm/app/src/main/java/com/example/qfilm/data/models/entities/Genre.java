package com.example.qfilm.data.models.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "genres")
public class Genre {

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer genreId;

    @SerializedName("name")
    @Expose
    private String name;

    private Integer timestamp;

    private String language;

    /**
     * movies and series has som genres that overlap and some that don't. Therefore
     * booleans are used to mark which belongs to which in Room.
     *
     **/

    private Boolean isSeriesGenre = false;

    private Boolean isMovieGenre = false;

    @Ignore
    private Boolean isSelected; // used to set initial selected item in genres list


    @Ignore
    public Genre(@NonNull Integer genreId, String name, Integer timestamp,
                  Boolean isSeriesGenre, Boolean isMovieGenre) {
        this.genreId = genreId;
        this.name = name;
        this.timestamp = timestamp;
        this.isSeriesGenre = isSeriesGenre;
        this.isMovieGenre = isMovieGenre;
    }


    @Ignore
    public Genre(@NonNull Integer genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }


    public Genre() {

    }

    /**
     * getters and setters
     * **/


    @NonNull
    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(@NonNull Integer genreId) {
        this.genreId = genreId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }


    public Boolean getSeriesGenre() {
        return isSeriesGenre;
    }

    public void setSeriesGenre(Boolean seriesGenre) {
        isSeriesGenre = seriesGenre;
    }


    public Boolean getMovieGenre() {
        return isMovieGenre;
    }

    public void setMovieGenre(@NonNull Boolean movieGenre) {
        isMovieGenre = movieGenre;
    }

    public Boolean isSelected() {
        return isSelected == null ? false : isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * Override equals to be able to compare collection by values
     * **/

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Genre other = (Genre) obj;

        return genreId.equals(other.getGenreId());
    }

}
