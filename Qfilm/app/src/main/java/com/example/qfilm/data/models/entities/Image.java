package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("aspect_ratio")
    private double aspectRatio;


    public Image(String filePath, double aspectRatio) {
        this.filePath = filePath;
        this.aspectRatio = aspectRatio;
    }

    public Image(){

    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }


    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Image other = (Image) obj;

       return filePath.equals(other.getFilePath());
    }

}
