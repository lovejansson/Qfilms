package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import java.util.Collections;


import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TestDummyData.GENRES_ONE;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.IMAGES_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_BACKDROP_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_POSTER_ONE;
import static utils.TestDummyData.IMDB_ID_ONE;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.ORG_TITLE_ONE;
import static utils.TestDummyData.OVERVIEW_ONE;
import static utils.TestDummyData.RATING_ONE;
import static utils.TestDummyData.RELEASE_DATE_ONE;
import static utils.TestDummyData.RUNTIME_ONE;
import static utils.TestDummyData.TIMESTAMP_ONE;
import static utils.TestDummyData.TITLE_ONE;
import static utils.TestDummyData.VIDEOS_ONE;
import static utils.TestDummyData.VIDEOS_TWO;
import static utils.TestDummyData.VIDEO_ONE;

public class MovieDetailsTest {


    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(MOVIE_DETAILS_ONE, MOVIE_DETAILS_ONE);

    }


    @Test
    void equals_differentIds_returnFalse(){

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "es");

        movieDetails.setId(4);

       assertNotEquals(MOVIE_DETAILS_ONE, movieDetails);
    }


    @Test
    void equals_differentTitles_returnFalse(){

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "es");

        movieDetails.setTitle("other title");

        assertNotEquals(MOVIE_DETAILS_ONE, movieDetails);
    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){


        MovieDetails correctDetails = MOVIE_DETAILS_ONE;

        MovieDetails details = new MovieDetails();

        details.setId(correctDetails.getId());
        details.setImdbId(correctDetails.getImdbId());
        details.setTitle(correctDetails.getTitle());
        details.setOriginalTitle(correctDetails.getOriginalTitle());
        details.setGenres(correctDetails.getGenres());
        details.setOverview(correctDetails.getOverview());
        details.setPosterPath(correctDetails.getPosterPath());
        details.setBackdropPath(correctDetails.getBackdropPath());
        details.setReleaseDate(correctDetails.getReleaseDate());
        details.setRuntime(correctDetails.getRuntime());
        details.setTimestamp(correctDetails.getTimestamp());
        details.setVideos(correctDetails.getVideos());
        details.setImages(correctDetails.getImages());
        details.setRatings(correctDetails.getRatings());
        details.setLanguage(correctDetails.getLanguage());

        assertEquals(correctDetails , details);

    }

    /**
     *
     *  methods for providing values to display
     *
     * **/

    @Test
    void genresToString_returnCorrectValue(){

        String correctGenreString = "Comedy, Drama";

        assertEquals(correctGenreString, MOVIE_DETAILS_ONE.genresToString());

    }

    @Test
    void genresReleaseYear_returnCorrectValue(){

        String correctYearString = "2017";

        assertEquals(correctYearString, MOVIE_DETAILS_ONE.getReleaseYear());

    }


    @Test
    void hasTrailer_returnTrue(){

        MovieDetails movieDetails = new MovieDetails();

        movieDetails.setVideos(VIDEOS_ONE);

        assertEquals(true, movieDetails.hasTrailer());

    }


    @Test
    void hasTrailer_returnFalse(){

        MovieDetails movieDetails = new MovieDetails();

        movieDetails.setVideos(VIDEOS_TWO);

        assertEquals(false, movieDetails.hasTrailer());

    }


    @Test
    void getTrailer_returnFalse(){

        assertEquals(VIDEO_ONE, MOVIE_DETAILS_ONE.getTrailer());

    }


}
