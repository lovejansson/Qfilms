package com.example.qfilm.data.models.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName="series_details")
public class SeriesDetails extends MovieDetails  {

    @SerializedName("number_of_seasons")
    @Expose
    private Integer seasons;

    @SerializedName("episode_run_time")
    @Expose
    private int[] episodeRuntime;

    @Ignore
    public SeriesDetails(@NonNull Integer id, @NonNull String imdbId, String title, String originalTitle,
                         List<Genre> genres, String overview, String posterPath, String backdropPath,
                         String releaseDate, Integer runtime, Videos videos, Images images,
                         List<Rating> ratings, Integer seasons, int[] episodeRuntime, String language) {
        super(id, imdbId, title, originalTitle, genres, overview, posterPath, backdropPath, releaseDate,
                runtime, videos, images, ratings, 0, language);

        this.seasons = seasons;
        this.episodeRuntime = episodeRuntime;
    }

    public SeriesDetails(){

    }

    public void setEpisodeRuntime(int[] episodeRuntime) {
        this.episodeRuntime = episodeRuntime;
    }

    public int[] getEpisodeRuntime() {
        return episodeRuntime;
    }

    public Integer getSeasons() {
        return seasons;
    }

    public void setSeasons(Integer seasons) {
        this.seasons = seasons;
    }

    @Override
    public Integer getRuntime() {

        if(episodeRuntime != null && episodeRuntime.length > 0){
            return episodeRuntime[0];
        }else{
            return null;
        }

    }

}
