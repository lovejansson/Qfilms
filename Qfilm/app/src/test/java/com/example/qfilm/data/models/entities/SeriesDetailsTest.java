package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TestDummyData.EPISODE_RUNTIME;
import static utils.TestDummyData.SEASONS;

public class SeriesDetailsTest {


    @Test
    void gettersAndSetters_episodeRuntimeAndSeasons_returnTrue(){

        SeriesDetails details = new SeriesDetails();

        details.setSeasons(SEASONS);

        details.setEpisodeRuntime(EPISODE_RUNTIME);

        assertEquals(details.getEpisodeRuntime(), EPISODE_RUNTIME);
        assertEquals(details.getRuntime(), EPISODE_RUNTIME[0]);
        assertEquals(details.getSeasons(), SEASONS);

    }
}
