package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static utils.TestDummyData.VIDEO_KEY_TWO;
import static utils.TestDummyData.VIDEO_ONE;
import static utils.TestDummyData.VIDEO_SITE_TWO;


public class VideoTest {

    @Test
    void equals_equalProperties_returnTrue(){

        assertEquals(VIDEO_ONE, VIDEO_ONE);
    }


    @Test
    void equals_differentKey_returnFalse(){

        Video video = new Video(VIDEO_ONE.getKey(), VIDEO_ONE.getSite(), VIDEO_ONE.getType());

        video.setKey(VIDEO_KEY_TWO);

        assertNotEquals(video, VIDEO_ONE);

    }


    @Test
    void equals_differentSite_returnFalse(){

        Video video = new Video(VIDEO_ONE.getKey(), VIDEO_ONE.getSite(), VIDEO_ONE.getType());

        video.setSite(VIDEO_SITE_TWO);

        assertNotEquals(video, VIDEO_ONE);

    }


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Video correctVideo = VIDEO_ONE;

        Video video = new Video();

        video.setType(correctVideo.getType());
        video.setKey(correctVideo.getKey());
        video.setSite(correctVideo.getSite());

        assertEquals(correctVideo, video);

    }

}
