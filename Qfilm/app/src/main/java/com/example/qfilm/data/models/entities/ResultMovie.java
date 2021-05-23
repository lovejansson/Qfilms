package com.example.qfilm.data.models.entities;

import androidx.room.Entity;

/**
 * Since id:s of movies and series can overlap I choose to store them in different
 * tables in Room, and therefore created two subclasses of Result.
 *
 **/

@Entity(tableName="results_movies")
public class ResultMovie extends Result{


    public ResultMovie(Result result){

        super(result.getResultId(), result.getTitle(), result.getOriginalTitle(), result.getPopularity(),
                result.getPosterPath(), result.getBackdropPath(),
                result.getOverview(), result.getTimestamp(), MediaType.MOVIE);

     
    }

    public ResultMovie(){

    }

}
