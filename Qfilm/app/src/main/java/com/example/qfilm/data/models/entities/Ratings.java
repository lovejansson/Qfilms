package com.example.qfilm.data.models.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@Entity(tableName="ratings")
public class Ratings {

    @PrimaryKey
    @NonNull
    private String title;

    @SerializedName("Ratings")
    private List<Rating> ratings;

    private Integer timestamp;


    public Ratings(){

    }

    @Ignore
    public Ratings(@NonNull String title, List<Rating> ratings) {
        this.title = title;
        this.ratings = ratings;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Ratings other = (Ratings) obj;

        return title.equals(other.getTitle()) && ratings.equals(other.getRatings());
    }

}
