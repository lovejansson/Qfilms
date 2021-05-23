package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.RATING_ONE;

public class RatingTest {


    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(RATING_ONE, RATING_ONE);
    }


    @Test
    void equals_differentSource_returnFalse(){

        Rating rating = new Rating(RATING_ONE.getSource(), RATING_ONE.getValue());

        rating.setSource("rotten tomatoes");

        assertNotEquals(RATING_ONE, rating);

    }


    @Test
    void equals_differentValue_returnFalse(){

        Rating rating = new Rating(RATING_ONE.getSource(), RATING_ONE.getValue());

        rating.setValue("4/10");

        assertNotEquals(RATING_ONE, rating);


    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Rating correctRating = RATING_ONE;

        Rating rating = new Rating();

        rating.setSource(correctRating.getSource());
        rating.setValue(correctRating.getValue());

        assertEquals(correctRating, rating);

    }

}
