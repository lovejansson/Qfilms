package com.example.qfilm.data.models.relations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 *
 * used to store information about movies and series per genre in Room.
 *
 * this table is queried for the specific genre Id and then results can be queried based on
 * resultId.
 *
 * i used the many-to-many relations annotations that Android provides first, but
 * they didn't let me sort och paginate the data so i chose to do this instead.
 *
 * **/
public class GenreResultJoin {

    @NonNull
    @PrimaryKey
    long primaryKey;

    @NonNull
    private Integer genreId;

    @NonNull
    private Integer resultId;

    @NonNull
    private Double popularity;

    @Ignore
    public GenreResultJoin(@NotNull Integer genreId, @NotNull Integer resultId,
                           @NonNull Double popularity, @NonNull long primaryKey){
        this.genreId = genreId;
        this.resultId = resultId;
        this.popularity = popularity;
        this.primaryKey = primaryKey;
    }

    public GenreResultJoin(){

    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    @NonNull
    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId){
        this.genreId = genreId;
    }

    @NonNull
    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId){
        this.resultId = resultId;
    }


    @NonNull
    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(@NonNull Double popularity) {
        this.popularity = popularity;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        GenreResultJoin other = (GenreResultJoin) obj;

        return genreId.equals(other.getGenreId()) && resultId.equals(other.getResultId());

    }
}
