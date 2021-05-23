package com.example.qfilm.data.models.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "movie_details")
public class MovieDetails  {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("imdb_id")
    @Exclude
    @Expose
    private String imdbId;

    @SerializedName(value= "title", alternate = {"name"})
    @Expose
    private String title;

    @SerializedName(value = "original_title", alternate = {"original_name"})
    @Expose
    private String originalTitle;

    @SerializedName("genres")
    @Expose

    private List<Genre> genres;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName(value= "release_date", alternate = {"first_air_date"})
    @Expose

    private String releaseDate;

    @SerializedName("runtime")
    @Expose

    private Integer runtime;

    @SerializedName("videos")
    private Videos videos;

    private Integer timestamp;

    private Images images;

    private List<Rating> ratings;

    private String imdbRating;

    private String language;


    public MovieDetails() {

    }

    @Ignore
    public MovieDetails(Integer id, String imdbId, String title, String originalTitle,
                        List<Genre> genres, String overview, String posterPath, String backdropPath,
                        String releaseDate, Integer runtime, Videos videos, Images images,
                        List<Rating> ratings, Integer timestamp, String language) {
        this.id = id;
        this.imdbId = imdbId;
        this.title = title;
        this.originalTitle = originalTitle;
        this.genres = genres;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.videos = videos;
        this.images = images;
        this.ratings = ratings;
        this.timestamp = timestamp;
        this.language = language;
    }


    /**
     *
     *  getters and setters
     *
     * **/

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }


    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId( String imdbId) {
        this.imdbId = imdbId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres( List<Genre> genres) {
        this.genres = genres;
    }


    public String getOverview() {

        return overview;

    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate( String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime( Integer runtime) {
        this.runtime = runtime;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     *
     *  override equals for comparison of values
     *
     * **/

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        MovieDetails other = (MovieDetails) obj;

        return id.equals(other.getId()) && title.equals(other.getTitle());
    }

    /**
     *
     *  methods for providing values to display
     *
     * **/

    public String genresToString(){

        StringBuilder res = new StringBuilder();

        for(Genre genre : genres){

            res.append(genre.getName()).append(", ");
        }

        if(res.length() > 0) {
            res.delete(res.length() - 2, res.length());
        }else{
            return "-";
        }

        return res.toString();

    }

    public String getReleaseYear(){

        if(releaseDate != null && !releaseDate.isEmpty()) {
            return releaseDate.substring(0, 4);
        }else{
            return "";
        }
    }


    public Boolean hasTrailer(){

        for(Video video: videos.getResults()){

            if(video.getType().equals("Trailer")){
                return true;
            }
        }

        return false;
    }


    public Video getTrailer(){

        for(Video video: videos.getResults()){

            if(video.getType().equals("Trailer")){
                return video;
            }

        }

        return null;
    }

}



