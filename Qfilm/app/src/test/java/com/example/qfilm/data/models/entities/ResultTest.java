package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_BACKDROP_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_POSTER_ONE;
import static utils.TestDummyData.ORG_TITLE_ONE;
import static utils.TestDummyData.OVERVIEW_ONE;
import static utils.TestDummyData.POPULARITY_ONE;
import static utils.TestDummyData.RESULT_ONE;
import static utils.TestDummyData.TIMESTAMP_ONE;
import static utils.TestDummyData.TITLE_ONE;

public class ResultTest {


    @Test
    void equals_equalProperties_returnTrue(){

       assertEquals(RESULT_ONE, RESULT_ONE);

    }


    @Test
    void equals_differentIds_returnFalse(){

        Result result = new Result(ID_ONE, TITLE_ONE, ORG_TITLE_ONE, POPULARITY_ONE,
                IMAGE_FILE_PATH_POSTER_ONE, IMAGE_FILE_PATH_BACKDROP_ONE, OVERVIEW_ONE, TIMESTAMP_ONE, MediaType.MOVIE);

        result.setResultId(4);

        Assertions.assertNotEquals(RESULT_ONE, result);

    }


    @Test
    void equals_differentTitles_returnFalse(){

        Result result = new Result(ID_ONE, TITLE_ONE, ORG_TITLE_ONE, POPULARITY_ONE,
                IMAGE_FILE_PATH_POSTER_ONE, IMAGE_FILE_PATH_BACKDROP_ONE, OVERVIEW_ONE, TIMESTAMP_ONE, MediaType.MOVIE);

        result.setTitle("other title");

        Assertions.assertNotEquals(RESULT_ONE, result);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Result correctResult = RESULT_ONE;

        correctResult.setLanguage("es");

        Result result = new Result();

        result.setResultId(correctResult.getResultId());
        result.setTitle(correctResult.getTitle());
        result.setOriginalTitle(correctResult.getOriginalTitle());
        result.setPopularity(correctResult.getPopularity());
        result.setPosterPath(correctResult.getPosterPath());
        result.setBackdropPath(correctResult.getBackdropPath());
        result.setOverview(correctResult.getOverview());
        result.setTimestamp(correctResult.getTimestamp());
        result.setTimestamp(correctResult.getTimestamp());
        result.setLanguage(correctResult.getLanguage());

        assertEquals(correctResult, result);

    }

}
