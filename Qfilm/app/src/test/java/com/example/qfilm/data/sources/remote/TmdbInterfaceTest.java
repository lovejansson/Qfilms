package com.example.qfilm.data.sources.remote;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.qfilm.data.models.entities.GenreListResponse;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.SeriesDetails;
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
import static utils.TestDummyData.GENRE_LIST_RESPONSE_ONE;
import static utils.TestDummyData.GENRE_LIST_RESPONSE_TWO;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.SERIES_DETAILS;


public class TmdbInterfaceTest {

    // this is tested
    TmdbInterface tmdbInterface;

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

        tmdbInterface = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(TmdbInterface.class);

    }


    @After
    public void teardown() throws IOException {

        mockWebServer.shutdown();

    }


    @Test
    public void getMovieGenres_SuccessResponse() throws IOException, InterruptedException {

        // setup

        String MOVIE_GENRES_200 =  "movieGenresSuccess.json";

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonToString(MOVIE_GENRES_200));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<GenreListResponse> result = LiveDataTestUtil.getOrAwaitValue(
                tmdbInterface.getMovieGenresList("language"));

        // assert
        assertEquals(ApiResponse.ApiSuccessResponse.class, result.getClass());
        assertEquals(GenreListResponse.class, ((ApiResponse.ApiSuccessResponse)result).getBody().getClass());
        GenreListResponse genreListResponse = (GenreListResponse)((ApiResponse.ApiSuccessResponse)result).getBody();

        assertEquals(GENRE_LIST_RESPONSE_ONE, genreListResponse);

    }


    @Test
    public void getSeriesGenres_SuccessResponse() throws IOException, InterruptedException {

        // setup

        String SERIES_GENRES_200 = "seriesGenresSuccess.json";

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonToString(SERIES_GENRES_200));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<GenreListResponse> result = LiveDataTestUtil.getOrAwaitValue(
                tmdbInterface.getSeriesGenresList("language"));

        // assert

        assertEquals(ApiResponse.ApiSuccessResponse.class, result.getClass());

        assertEquals(GenreListResponse.class, ((ApiResponse.ApiSuccessResponse)result).getBody().getClass());

        GenreListResponse genreListResponse = (GenreListResponse)((ApiResponse.ApiSuccessResponse)result).getBody();

        assertEquals(GENRE_LIST_RESPONSE_TWO, genreListResponse);
    }


    @Test
    public void getMovieDetails_SuccessResponse() throws InterruptedException, IOException {

        // setup

        String MOVIE_DETAILS_200 =  "movieDetailsSuccess.json";

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonToString(MOVIE_DETAILS_200));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<MovieDetails> result = LiveDataTestUtil.getOrAwaitValue(
                tmdbInterface.getMovieDetails(4, "language",
                        "image language", "images,videos"));

        // assert

        assertEquals(ApiResponse.ApiSuccessResponse.class, result.getClass());

        assertEquals(MOVIE_DETAILS_ONE, ((ApiResponse.ApiSuccessResponse)result).getBody());

    }


    @Test
    public void getSeriesDetails_SuccessResponse() throws InterruptedException, IOException {

        // setup

        String SERIES_DETAILS_200 = "seriesDetailsSuccess.json";
        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonToString(SERIES_DETAILS_200));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<SeriesDetails> result = LiveDataTestUtil.getOrAwaitValue(
                tmdbInterface.getSeriesDetails(4, "language",
                        "image language", "images,videos"));

        // assert

        assertEquals(ApiResponse.ApiSuccessResponse.class, result.getClass());

        assertEquals(SERIES_DETAILS, ((ApiResponse.ApiSuccessResponse)result).getBody());

    }


    @Test
    public void getMovieDetails_404Response() throws InterruptedException, IOException {

        // setup

        String MOVIE_DETAILS_404 = "movieDetails404.json";

        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                .setBody(jsonToString(MOVIE_DETAILS_404));

        mockWebServer.enqueue(response);

        // act
        ApiResponse<MovieDetails> result = LiveDataTestUtil.getOrAwaitValue(
                tmdbInterface.getMovieDetails(4, "dummy", "dummy",
                        "dummy"));

        // assert
        assertEquals(ApiResponse.ApiErrorResponse.class, result.getClass());
        assertEquals(jsonToString(MOVIE_DETAILS_404), ((ApiResponse.ApiErrorResponse)result).getMessage());

    }


    private String jsonToString(String file) throws IOException {

        final String BASE_PATH = "src\\test\\java\\com\\example\\qfilm\\data\\sources\\fakeResponses\\";

        return new String(Files.readAllBytes(Paths.get(BASE_PATH, file)));
    }

}


