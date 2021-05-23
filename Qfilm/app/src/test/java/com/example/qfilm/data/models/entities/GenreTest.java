package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.DRAMA;


public class GenreTest {


    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(DRAMA, DRAMA);
    }


    @Test
    void equals_differentId_returnFalse(){

      Genre genre = new Genre(DRAMA.getGenreId(), DRAMA.getName());

      genre.setGenreId(4);

      assertNotEquals(genre, DRAMA);

    }


    @Test
    void equals_null_returnFalse(){

        assertNotEquals(DRAMA, null);

    }


    @Test
    void equals_otherClass_returnFalse(){

        assertNotEquals(DRAMA, 4);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Genre correctGenre = DRAMA;

        Genre genre = new Genre();

        genre.setGenreId(correctGenre.getGenreId());
        genre.setName(correctGenre.getName());
        genre.setTimestamp(correctGenre.getTimestamp());
        genre.setSeriesGenre(correctGenre.getSeriesGenre());
        genre.setMovieGenre(correctGenre.getMovieGenre());

        assertEquals(correctGenre, genre);

    }

}
