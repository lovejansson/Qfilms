package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.IMAGE_BACKDROP_ONE;
import static utils.TestDummyData.IMAGE_BACKDROP_TWO;
import static utils.TestDummyData.IMAGE_POSTER_ONE;

public class ImageTest {

    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(IMAGE_BACKDROP_ONE, IMAGE_BACKDROP_ONE);
    }


    @Test
    void equals_differentFilePath_returnFalse(){

     assertNotEquals(IMAGE_BACKDROP_ONE, IMAGE_BACKDROP_TWO);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Image correctImage = IMAGE_POSTER_ONE;

        Image image = new Image();

        image.setAspectRatio(correctImage.getAspectRatio());
        image.setFilePath(correctImage.getFilePath());

        assertEquals(correctImage, image);

    }

}
