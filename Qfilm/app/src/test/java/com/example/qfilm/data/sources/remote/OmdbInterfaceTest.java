package com.example.qfilm.data.sources.remote;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.repositories.utils.ApiResponse;
import com.example.qfilm.repositories.utils.LiveDataCallAdapterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.LiveDataTestUtil;

import static org.junit.Assert.assertEquals;
import static utils.TestDummyData.RATING_ONE;
import static utils.TestDummyData.RATING_THREE;
import static utils.TestDummyData.RATING_TWO;

public class OmdbInterfaceTest {

    // this is tested
    OmdbInterface omdbInterface;

    // this is used to not make real network requests
    MockWebServer mockWebServer;

    /**
     A JUnit Test Rule that swaps the background executor used by the Architecture Components
     with a different one which executes each task synchronously. Used because you can't invoke
     observeForever on a background thread
     **/

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    @Before
    public void setup() {

        mockWebServer = new MockWebServer();

       omdbInterface = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(OmdbInterface.class);

    }


    @After
    public void teardown() throws IOException {

        mockWebServer.shutdown();

    }


    @Test
    public void getMovieList_SuccessResponse() throws IOException, InterruptedException {

        // setup

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonToString("omdbResponseSuccess.json"));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<Ratings> result = LiveDataTestUtil.getOrAwaitValue(
                omdbInterface.getRatingsByTitle("title"));

        // assert
        assertEquals(ApiResponse.ApiSuccessResponse.class, result.getClass());
        assertEquals(Ratings.class, ((ApiResponse.ApiSuccessResponse)result).getBody().getClass());

        Ratings ratings = (Ratings)(((ApiResponse.ApiSuccessResponse)result).getBody());

        assertEquals(RATING_ONE, ratings.getRatings().get(0));
        assertEquals(RATING_TWO, ratings.getRatings().get(1));
        assertEquals(RATING_THREE, ratings.getRatings().get(2));

    }


    @Test
    public void getRatings_404Response() throws InterruptedException, IOException {

        // setup

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                .setBody(jsonToString("omdbResponse404.json"));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<Ratings> result = LiveDataTestUtil.getOrAwaitValue(
                omdbInterface.getRatingsByTitle("title"));

        // assert
        assertEquals(ApiResponse.ApiErrorResponse.class, result.getClass());
        assertEquals(jsonToString("omdbResponse404.json"), ((ApiResponse.ApiErrorResponse)result).getMessage());

    }


    private String jsonToString(String file) throws IOException {

        final String BASE_PATH = "src\\test\\java\\com\\example\\qfilm\\data\\sources\\fakeResponses\\";

        return new String(Files.readAllBytes(Paths.get(BASE_PATH, file)));
    }

}
