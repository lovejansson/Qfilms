package com.example.qfilm.data.models.relations;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.GENRE_ID_ACTION;
import static utils.TestDummyData.GENRE_ID_DRAMA;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.ID_TWO;

public class GenreResultJoinTest {


    @Test
    void equals_equalProperties_returnTrue(){

        GenreResultJoin one = new GenreResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4, 44);
        GenreResultJoin two = new GenreResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4, 44);

        assertEquals(one, two);

    }


    @Test
    void equals_differentGenreId_returnFalse(){

        GenreResultJoin one = new GenreResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4, 44);
        GenreResultJoin two = new GenreResultJoin(GENRE_ID_ACTION, ID_ONE, 4.4, 44);

        assertNotEquals(one, two);

    }


    @Test
    void equals_differentResultId_returnFalse(){

        GenreResultJoin one = new GenreResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4, 44);
        GenreResultJoin two = new GenreResultJoin(GENRE_ID_DRAMA, ID_TWO, 4.4, 44);

        assertNotEquals(one, two);

    }


    @Test
    void getters_allProperties_returnsCorrectValues(){
        GenreResultJoin correct = new GenreResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4, 44);

        GenreResultJoin genreResultJoin = new GenreResultJoin();

        genreResultJoin.setPopularity(correct.getPopularity());
        genreResultJoin.setPrimaryKey(correct.getPrimaryKey());
        genreResultJoin.setGenreId(correct.getGenreId());
        genreResultJoin.setResultId(correct.getResultId());

        assertEquals(correct.getGenreId(), genreResultJoin.getGenreId());
        assertEquals(correct.getResultId(), genreResultJoin.getResultId());
        assertEquals(correct.getPrimaryKey(), genreResultJoin.getPrimaryKey());
        assertEquals(correct.getPopularity(), genreResultJoin.getPopularity());

    }

}
