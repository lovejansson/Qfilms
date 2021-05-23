package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TestDummyData.GENRES_ONE;
import static utils.TestDummyData.GENRE_LIST_RESPONSE_ONE;
import static utils.TestDummyData.GENRE_LIST_RESPONSE_TWO;


public class GenreListResponseTest {


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        GenreListResponse correctGenreListResponse = GENRE_LIST_RESPONSE_ONE;

        GenreListResponse genreListResponse = new GenreListResponse();

        genreListResponse.setGenres(correctGenreListResponse.getGenres());

        assertEquals(correctGenreListResponse, genreListResponse);

    }


    @Test

    public void equals_differentGenresList_returnFalse(){

        assertNotEquals(GENRE_LIST_RESPONSE_ONE, GENRE_LIST_RESPONSE_TWO);

    }


    @Test
    public void equals_equalGenreLists_returnTrue(){

        GenreListResponse genreListResponseOne = new GenreListResponse();

        genreListResponseOne.setGenres(GENRES_ONE);

        assertEquals(GENRE_LIST_RESPONSE_ONE, genreListResponseOne);

    }

}
