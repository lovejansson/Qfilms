package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("type")
    @Expose
    private String type;


    public Video(){

    }

    public Video(String key, String site, String type){
        this.key = key;
        this.site = site;
        this.type = type;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Video other = (Video) obj;

        return key.equals(other.getKey()) && site.equals(other.getSite());
    }
}
