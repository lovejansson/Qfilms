package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.RATINGS_ONE;
import static utils.TestDummyData.RATING_TWO;

public class RatingsTest {

    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(RATINGS_ONE, RATINGS_ONE);
    }


    @Test
    void equals_differentTitle_returnFalse(){

        Ratings ratings = new Ratings(RATINGS_ONE.getTitle(), RATINGS_ONE.getRatings());

        ratings.setTitle("other title");

        assertNotEquals(RATINGS_ONE, ratings);

    }


    @Test
    void equals_differentRatings_returnFalse(){

        Ratings ratings = new Ratings(RATINGS_ONE.getTitle(), RATINGS_ONE.getRatings());

        ratings.setRatings(Collections.singletonList(RATING_TWO));

        assertNotEquals(RATINGS_ONE, ratings);


    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Ratings correctRatings = RATINGS_ONE;

        Ratings ratings = new Ratings();

        ratings.setRatings(correctRatings.getRatings());
        ratings.setTitle(correctRatings.getTitle());

        assertEquals(correctRatings, ratings);

    }


}
