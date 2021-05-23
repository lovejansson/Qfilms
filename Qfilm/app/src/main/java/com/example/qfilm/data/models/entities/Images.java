package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import java.util.List;


/**
 * Movie- or SeriesDetails comes with Images property
 * **/
public class Images {

    private List<Image> backdrops;

    private List<Image> posters;

    public Images(List<Image> backdrops, List<Image> posters) {

        this.backdrops = backdrops;
        this.posters = posters;
    }

    public Images(){

    }

    public List<Image> getBackdrops() {

        return backdrops;
    }

    public void setBackdrops(List<Image> backdrops) {
        this.backdrops = backdrops;
    }


    public List<Image> getPosters() {
        return posters;
    }


    public void setPosters(List<Image> posters) {
        this.posters = posters;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Images other = (Images) obj;

        return backdrops.equals(other.getBackdrops()) && posters.equals(other.getPosters());

    }
}
