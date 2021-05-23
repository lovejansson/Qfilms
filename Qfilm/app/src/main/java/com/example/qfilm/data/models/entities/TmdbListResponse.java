package com.example.qfilm.data.models.entities;

import androidx.annotation.Nullable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TmdbListResponse {

    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    @SerializedName("results")
    @Expose
    private List<Result> results;


    public TmdbListResponse(){

    }


    public TmdbListResponse(Integer page, Integer totalPages, List<Result> results) {
        this.page = page;
        this.totalPages = totalPages;
        this.results = results;
    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }


    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }


    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }



    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        TmdbListResponse other = (TmdbListResponse) obj;

        return results.equals(other.getResults());


    }
}

