package com.example.qfilm.data.models.typeconverters;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Rating;
import com.example.qfilm.data.models.entities.Videos;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

import static utils.TestDummyData.EPISODE_RUNTIME;
import static utils.TestDummyData.GENRES_ONE;
import static utils.TestDummyData.RATINGS_ONE;
import static utils.TestDummyData.RATING_ONE;
import static utils.TestDummyData.VIDEOS_ONE;

public class TypeConvertersTest {

    private TypeConverters typeConverters = new TypeConverters();


    @Test
    public void toGenreString_returnCorrectString(){

        String correctResult = "[{\"id\":35,\"name\":\"Comedy\",\"isSeriesGenre\":false," +
                "\"isMovieGenre\":false},{\"id\":18,\"name\":\"Drama\",\"timestamp\":44444444,\"isSeriesGenre\":true," +
                "\"isMovieGenre\":true}]";

        String result = typeConverters.toGenreString(GENRES_ONE);

        assertEquals(correctResult, result);
    }


    @Test
    public void toGenreList_returnCorrectGenreList(){

        String genreString = "[{\"id\":35,\"name\":\"Comedy\",\"timestamp\":0,\"isSeriesGenre\":false," +
                "\"isMovieGenre\":false},{\"id\":18,\"name\":\"Drama\",\"timestamp\":44444444,\"isSeriesGenre\":true," +
                "\"isMovieGenre\":true}]";

        List<Genre> result = typeConverters.toGenreList(genreString);

        assertEquals(GENRES_ONE, result);
    }


    @Test
    public void toVideosString_returnCorrectString(){

        String correctResult = "[{\"key\":\"Z9AYPxH5NTM\",\"site\":\"Youtube\",\"type\":\"Trailer\"}]";

        String result = typeConverters.toVideosString(VIDEOS_ONE);

        assertEquals(correctResult, result);

    }


    @Test
    public void toVideos_returnCorrectVideos(){

        String videosString = "[{\"key\":\"Z9AYPxH5NTM\",\"site\":\"Youtube\",\"type\":\"Trailer\"}]";

        Videos result = typeConverters.toVideos(videosString);

        assertEquals(VIDEOS_ONE, result);

    }


    @Test
    public void toRatingString_returnCorrectString(){

        String correctResult = "[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.9/10\"}]";

        String result = typeConverters.toRatingsString(Collections.singletonList(RATING_ONE));

        assertEquals(correctResult, result);

    }


    @Test
    public void toRatingList_returnCorrectRatingList(){

        String ratingString = "[{\"Source\":\"Internet Movie Database\", \"Value\":\"7.9/10\"}]";

        List<Rating> result = typeConverters.toRatingsList(ratingString);

        assertEquals(RATINGS_ONE.getRatings(), result);

    }


    @Test
    public void toMediaTypeString_returnCorrectString(){

        String correctResult = "\"MOVIE\"";

        String result = typeConverters.toMediaTypeString(MediaType.MOVIE);

        assertEquals(correctResult, result);

    }


    @Test
    public void toMediaTypeList_returnCorrectList(){

        String mediaTypeString = "\"MOVIE\"";

        MediaType result = typeConverters.toMediaTypeEnum(mediaTypeString);

        assertEquals(MediaType.MOVIE, result);

    }


    @Test
    public void toEpisodeRuntimeString_returnCorrectString(){

        String result = typeConverters.toEpisodeRunTimeString(EPISODE_RUNTIME);

        String correctResult = "[60]";

        assertEquals(correctResult, result);
    }


    @Test
    public void toEpisodeRuntimeList_returnCorrectList(){

        String episodeRuntimeString = "[60]";

        int[] result = typeConverters.toEpisodeRunTimeList(episodeRuntimeString);

        assertEquals(EPISODE_RUNTIME.getClass(), result.getClass());

        assertEquals(EPISODE_RUNTIME[0], result[0]);

    }

}
