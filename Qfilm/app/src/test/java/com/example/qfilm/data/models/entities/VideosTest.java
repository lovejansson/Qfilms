package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static utils.TestDummyData.VIDEOS_ONE;
import static utils.TestDummyData.VIDEO_TWO;


public class VideosTest {

    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(VIDEOS_ONE, VIDEOS_ONE);
    }


    @Test
    void equals_differentVideos_returnFalse(){

        Videos videos = new Videos(VIDEOS_ONE.getResults());

        videos.setResults(Collections.singletonList(VIDEO_TWO));

        assertNotEquals(videos, VIDEOS_ONE);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Videos correctVideos = VIDEOS_ONE;

        Videos videos = new Videos();

        videos.setResults(correctVideos.getResults());

        assertEquals(correctVideos, videos);
    }

}
