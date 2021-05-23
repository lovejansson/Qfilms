package com.example.qfilm.data.models.typeconverters;

import androidx.room.TypeConverter;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.Image;
import com.example.qfilm.data.models.entities.Images;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Rating;
import com.example.qfilm.data.models.entities.Video;
import com.example.qfilm.data.models.entities.Videos;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


/**
 *
 * RoomDataBase needs these to convert custom data types and Lists
 *
 * **/

public class TypeConverters {


    @TypeConverter
    public String toGenreString(List<Genre> genreList){

        Gson gson = new Gson();

        return gson.toJson(genreList);
    }


    @TypeConverter
    public List<Genre> toGenreList(String genreString){
        Type listType = new TypeToken<List<Genre>>(){}.getType();

        return new Gson().fromJson(genreString, listType);

    }


    @TypeConverter
    public String toVideosString(Videos videos){
        Gson gson = new Gson();

        List<Video> videoList = videos.getResults();

        return gson.toJson(videoList);
    }


    @TypeConverter
    public Videos toVideos(String videoString){
        Type listType = new TypeToken<List<Video>>(){}.getType();
        List<Video> videoList = new Gson().fromJson(videoString, listType);

        Videos videos = new Videos(videoList);

        return videos;

    }


    @TypeConverter
    public String toRatingsString(List<Rating> ratingsList){
        Gson gson = new Gson();
        return gson.toJson(ratingsList);
    }


    @TypeConverter
    public List<Rating> toRatingsList(String ratingsString){
        Type listType = new TypeToken<List<Rating>>(){}.getType();
        List<Rating> ratingsList = new Gson().fromJson(ratingsString, listType);

        return ratingsList;

    }


    @TypeConverter
    public String toMediaTypeString(MediaType mediaType){
        Gson gson = new Gson();
        String json = gson.toJson(mediaType);
        return json;
    }


    @TypeConverter
    public MediaType toMediaTypeEnum(String mediaTypeString){
        Type enumType = new TypeToken<MediaType>(){}.getType();

        return new Gson().fromJson(mediaTypeString, enumType);

    }


    @TypeConverter
    public String toImagesString(Images images){
        Gson gson = new Gson();

        return gson.toJson(images);
    }


    @TypeConverter
    public Images toImages(String imagesString){
        Gson gson = new Gson();
        Type imagesType = new TypeToken<Images>(){}.getType();

        return gson.fromJson(imagesString, imagesType);
    }



    @TypeConverter
    public String toImageString(List<Image> images){
        Gson gson = new Gson();
        return gson.toJson(images);

    }


    @TypeConverter
    public List<Image> toImageList(String imageString){
        Type listType = new TypeToken<List<Image>>(){}.getType();

        return new Gson().fromJson(imageString, listType);

    }


    @TypeConverter
    public String toEpisodeRunTimeString(int[] episodesList){
        Gson gson = new Gson();
        String json = gson.toJson(episodesList);
        return json;
    }


    @TypeConverter
    public int[] toEpisodeRunTimeList(String episodesString){
        Type listType = new TypeToken<int[]>(){}.getType();

        return new Gson().fromJson(episodesString, listType);

    }

}
