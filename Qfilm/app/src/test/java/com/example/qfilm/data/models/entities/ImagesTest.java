package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.IMAGES_ONE;
import static utils.TestDummyData.IMAGE_BACKDROP_TWO;
import static utils.TestDummyData.IMAGE_POSTER_TWO;


public class ImagesTest {

    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(IMAGES_ONE, IMAGES_ONE);
    }


    @Test
    void equals_differentPosters_returnFalse(){

        Images images = new Images(IMAGES_ONE.getBackdrops(), IMAGES_ONE.getPosters());

        images.setPosters(Collections.singletonList(IMAGE_POSTER_TWO));

        assertNotEquals(IMAGES_ONE, images);

    }


    @Test
    void equals_differentBackdrops_returnFalse(){

        Images images = new Images(IMAGES_ONE.getBackdrops(), IMAGES_ONE.getPosters());

        images.setBackdrops(Collections.singletonList(IMAGE_BACKDROP_TWO));

        assertNotEquals(IMAGES_ONE, images);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Images correctImages = IMAGES_ONE;

        Images images = new Images();

        images.setPosters(correctImages.getPosters());
        images.setBackdrops(correctImages.getBackdrops());

        assertEquals(correctImages, images);

    }


}
