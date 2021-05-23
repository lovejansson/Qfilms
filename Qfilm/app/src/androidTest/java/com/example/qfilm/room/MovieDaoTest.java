package com.example.qfilm.room;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.ResultMovie;
import com.example.qfilm.data.models.entities.ResultSeries;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.data.models.relations.GenreMovieResultJoin;
import com.example.qfilm.data.sources.local.MovieDao;
import com.example.qfilm.data.sources.local.MoviesRoomDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import utils.LiveDataTestUtil;

import static junit.framework.TestCase.assertEquals;
import static utils.TestDummyData.GENRE_ID_COMEDY;
import static utils.TestDummyData.GENRE_ID_DRAMA;
import static utils.TestDummyData.GENRE_NAME_COMEDY;
import static utils.TestDummyData.GENRE_NAME_DRAMA;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.RATINGS_ONE;
import static utils.TestDummyData.RESULT_ONE;
import static utils.TestDummyData.SERIES_DETAILS;


@RunWith(AndroidJUnit4ClassRunner.class)
public class MovieDaoTest {

    private MoviesRoomDatabase moviesRoomDatabase;

    private MovieDao movieDao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();


    @Before
    public void createDatabase(){
        moviesRoomDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MoviesRoomDatabase.class).allowMainThreadQueries().build();

        movieDao = moviesRoomDatabase.movieDao();
    }


    @After
    public void closeDatabase() throws IOException {
        moviesRoomDatabase.close();
    }


    @Test
    public void insertAndGetMovieResult(){

        movieDao.insertMovieResult(new ResultMovie(RESULT_ONE));

        Result insertedResult = movieDao.getMovieResult(RESULT_ONE.getResultId());

        assertEquals(RESULT_ONE, insertedResult);

    }


    @Test
    public void insertAndGetSeriesResult(){

        movieDao.insertSeriesResult(new ResultSeries(RESULT_ONE));

        Result insertedResult = movieDao.getSeriesResult(RESULT_ONE.getResultId());

        assertEquals(RESULT_ONE, insertedResult);

    }


    @Test
    public void insertAndGetGenres() throws Exception{


        Genre genreOne = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA, 1, true, true);
        Genre genreTwo = new Genre(GENRE_ID_COMEDY, GENRE_NAME_COMEDY, 1, true,
                false);

        movieDao.insertGenre(genreOne);

        movieDao.insertGenre(genreTwo);

        // should only return genreOne

        List<Genre> movieGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getMovieGenres());

        assertEquals(1, movieGenres.size());
        assertEquals(genreOne, movieGenres.get(0));

        // should return both genres with comedy first since its alphabetical order

        List<Genre> seriesGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getSeriesGenres());

        assertEquals(2, seriesGenres.size());
        assertEquals(genreTwo, seriesGenres.get(0));
        assertEquals(genreOne, seriesGenres.get(1));

    }


    @Test
    public void updateGenres() throws Exception{

        Genre genreOne = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA, 1, false, false);

        movieDao.insertGenre(genreOne);

        // should not return any genres

        List<Genre> movieGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getMovieGenres());
        List<Genre> seriesGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getSeriesGenres());

        assertEquals(0, movieGenres.size());
        assertEquals(0, seriesGenres.size());

        // update

        movieDao.updateIsMovieGenre(true, GENRE_ID_DRAMA);
        movieDao.updateIsSeriesGenre(true, GENRE_ID_DRAMA);

        // now the genre should be returned since its updated

        movieGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getMovieGenres());
        seriesGenres = LiveDataTestUtil.getOrAwaitValue(movieDao.getSeriesGenres());

        assertEquals(genreOne, movieGenres.get(0));
        assertEquals(genreOne, seriesGenres.get(0));

    }


    @Test
    public void insertAndGetGenreResultJoins() throws Exception {

        // insertions

        for (int i = 0; i < 10; ++i) {

            // two different genres
            int genreId = i % 2 == 0 ? 1 : 2;

            GenreMovieResultJoin genreMovieResultJoin =
                    new GenreMovieResultJoin(genreId, i, (double)i, (long)i * i);

            movieDao.insertGenreAndMovieJoin(genreMovieResultJoin);

        }

        // getting first page

        List<GenreMovieResultJoin> pageOne = movieDao.getGenreAndMovieJoins(1,
                1, 2);


        // should have returned a list of 3 genreMovieResultJoins with properties:
        // genreId: 1, result and popularity the same number from highest to lowest
        // since it is sorted based on popularity descending.

        assertEquals(3, pageOne.size());
        assertEquals((double)8, pageOne.get(0).getPopularity());
        assertEquals((double)6, pageOne.get(1).getPopularity());
        assertEquals((double)4, pageOne.get(2).getPopularity());


        // getting second page

        List<GenreMovieResultJoin> pageTwo = movieDao.getGenreAndMovieJoins(1,
                2, 2);


        // should have returned a list of 3 genreMovieResultJoins with properties:
        // genreId: 1, result and popularity the same number from highest to lowest
        // since it is sorted based on popularity descending.

        assertEquals(3, pageTwo.size());
        assertEquals((double)4, pageTwo.get(0).getPopularity());
        assertEquals((double)2, pageTwo.get(1).getPopularity());
        assertEquals((double)0, pageTwo.get(2).getPopularity());



        // getting third page

        List<GenreMovieResultJoin> pageThree = movieDao.getGenreAndMovieJoins(1,
                3, 2);


        // should have returned a list of 1 genreMovieResultJoins

        assertEquals(1, pageThree.size());
        assertEquals((double)0, pageThree.get(0).getPopularity());

    }


    @Test
    public void insertAndGetMovieDetails() throws Exception{

        movieDao.insertMovieDetails(MOVIE_DETAILS_ONE);

        MovieDetails insertedMovieDetails =
                LiveDataTestUtil.getOrAwaitValue(movieDao.getMovieDetails(MOVIE_DETAILS_ONE.getId()));

        assertEquals(MOVIE_DETAILS_ONE, insertedMovieDetails);

    }


    @Test
    public void insertAndGetSeriesDetails() throws Exception{

       movieDao.insertSeriesDetails(SERIES_DETAILS);

        SeriesDetails insertedSeriesDetails =
                LiveDataTestUtil.getOrAwaitValue(movieDao.getSeriesDetails(SERIES_DETAILS.getId()));

        assertEquals(SERIES_DETAILS, insertedSeriesDetails);

    }


    @Test
    public void insertAndGetRatings() throws Exception{

        movieDao.insertRating(RATINGS_ONE);

        Ratings ratings = LiveDataTestUtil.getOrAwaitValue(movieDao.getRatings(RATINGS_ONE.getTitle()));

        assertEquals(RATINGS_ONE, ratings);

    }

}


