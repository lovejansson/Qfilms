package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Movies and series details comes with Videos property
 * **/
public class Videos {

    @SerializedName("results")
    @Expose
    private List<Video> videos = null;

    public List<Video> getResults() {
        return videos;
    }


    public Videos(){

    }

    public Videos(List<Video> videos){
        this.videos = videos;
    }

    public void setResults(List<Video> videos) {
        this.videos = videos;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Videos other = (Videos) obj;

        return videos.equals(other.getResults());
    }

}
