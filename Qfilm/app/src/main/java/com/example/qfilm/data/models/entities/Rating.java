package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class Rating {

    @SerializedName("Source")
    private String source;

    @SerializedName("Value")
    private String value;

    public Rating(){

    }

    public Rating(String source, String value) {
        this.source = source;
        this.value = value;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

       Rating other = (Rating) obj;

        return source.equals(other.getSource()) && value.equals(other.getValue());
    }
}
