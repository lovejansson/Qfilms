package com.example.qfilm.repositories;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.GenreListResponse;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.ResultMovie;
import com.example.qfilm.data.models.relations.GenreMovieResultJoin;
import com.example.qfilm.data.sources.remote.OmdbInterface;
import com.example.qfilm.data.sources.remote.TmdbInterface;
import com.example.qfilm.data.sources.local.MovieDao;
import com.example.qfilm.repositories.utils.ApiResponse;
import com.example.qfilm.repositories.utils.AppExecutors;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

import retrofit2.Response;
import utils.Mocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static utils.TestDummyData.GENRE_ID_DRAMA;
import static utils.TestDummyData.GENRE_NAME_DRAMA;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.ID_TWO;
import static utils.TestDummyData.MOVIE_DETAILS_EXPIRED_TIMESTAMP;
import static utils.TestDummyData.MOVIE_DETAILS_FRESH_TIMESTAMP;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.RATINGS_ONE;
import static utils.TestDummyData.RATINGS_TWO;
import static utils.TestDummyData.RESULT_TWO;
import static utils.TestDummyData.TITLE_ONE;


public class MoviesRepositoryTest {

    // this will be tested
    MoviesRepository moviesRepository;

    // dependencies
    TmdbInterface tmdbInterface;
    OmdbInterface omdbInterface;
    MovieDao movieDao;
    AppExecutors appExecutors;

    // file paths for mock error response
    String MOVIE_DETAILS_404 = "C:\\Users\\lovej\\AndroidStudioProjects\\tdp028-android-app\\Qfilm\\" +
            "app\\src\\test\\java\\com\\example\\qfilm\\data\\sources\\fakeResponses\\movieDetails404.json";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();


    @Before
    public void setup()  {
        Mocks.reset();
        appExecutors = new InstantAppExecutors();
        tmdbInterface = Mockito.mock(TmdbInterface.class);
        omdbInterface = Mockito.mock(OmdbInterface.class);
        movieDao = Mockito.mock(MovieDao.class);
        moviesRepository = new MoviesRepository(movieDao, tmdbInterface, omdbInterface, appExecutors,
                Mocks.firestoreMock);

    }


    @Test
    public void fetchMovieDetails_noFetchFromNetwork() throws InterruptedException {

        // setup

        when(movieDao.getMovieDetails(4))
                .thenReturn(new MutableLiveData<>(MOVIE_DETAILS_FRESH_TIMESTAMP));

        // act

        LiveData<DataResource<MovieDetails>> result = moviesRepository.fetchMovieDetails(4,
                "en");

        Observer<DataResource<MovieDetails>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);


        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao);

        inorder.verify(movieDao).getMovieDetails(4);

        inorder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_FRESH_TIMESTAMP));


    }


    @Test
    public void fetchMovieDetails_expiredTimeStamp_ApiSuccessResponse() {

        // setup

        when(movieDao.getMovieDetails(4)).thenReturn(new MutableLiveData<>(MOVIE_DETAILS_EXPIRED_TIMESTAMP))
                .thenReturn(new MutableLiveData<>(MOVIE_DETAILS_ONE));

        doNothing().when(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);

        ApiResponse apiResponse = new ApiResponse();

        when(tmdbInterface.getMovieDetails(4, "es", "es,null",
                "videos,images"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.success(MOVIE_DETAILS_ONE))));


        // act

        Observer<DataResource<MovieDetails>> observer = Mockito.mock(Observer.class);

        LiveData<DataResource<MovieDetails>> result = moviesRepository.fetchMovieDetails(4, "es");

        result.observeForever(observer);


        // verify

        InOrder inOrder = Mockito.inOrder(observer, tmdbInterface, movieDao);

        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.loading(null));
        inOrder.verify(tmdbInterface).getMovieDetails(4, "es", "es,null",
                "videos,images");
        inOrder.verify(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);
        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_ONE));

    }


    @Test
    public void fetchMovieDetails_changeLanguage_ApiSuccessResponse() {

        // setup

        // movie details with fresh time stamp has language "en"
        when(movieDao.getMovieDetails(4)).thenReturn(new MutableLiveData<>(MOVIE_DETAILS_FRESH_TIMESTAMP))
                .thenReturn(new MutableLiveData<>(MOVIE_DETAILS_ONE));

        doNothing().when(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);

        ApiResponse apiResponse = new ApiResponse();

        when(tmdbInterface.getMovieDetails(4, "es", "es,null",
                "videos,images"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.success(MOVIE_DETAILS_ONE))));


        // act

        Observer<DataResource<MovieDetails>> observer = Mockito.mock(Observer.class);

        LiveData<DataResource<MovieDetails>> result = moviesRepository.fetchMovieDetails(4, "es");

        result.observeForever(observer);


        // verify

        InOrder inOrder = Mockito.inOrder(observer, tmdbInterface, movieDao);

        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.loading(null));
        inOrder.verify(tmdbInterface).getMovieDetails(4, "es", "es,null",
                "videos,images");
        inOrder.verify(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);
        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_ONE));

    }

    @Test
    public void fetchMovieDetails_null_ApiSuccessResponse() {

        // setup

        when(movieDao.getMovieDetails(4)).thenReturn(new MutableLiveData<>(null))
                .thenReturn(new MutableLiveData<>(MOVIE_DETAILS_ONE));

        doNothing().when(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);

        ApiResponse apiResponse = new ApiResponse();

        when(tmdbInterface.getMovieDetails(4, "es", "es,null",
                "videos,images"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.success(MOVIE_DETAILS_ONE))));


        // act

        Observer<DataResource<MovieDetails>> observer = Mockito.mock(Observer.class);

        moviesRepository.fetchMovieDetails(4, "es").observeForever(observer);

        // verify

        InOrder inOrder = Mockito.inOrder(observer, tmdbInterface, movieDao);

        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.loading(null));
        inOrder.verify(tmdbInterface).getMovieDetails(4, "es", "es,null",
                "videos,images");
        inOrder.verify(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);
        inOrder.verify(movieDao).getMovieDetails(4);
        inOrder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_ONE));

    }



    @Test
    public void fetchMovieDetails_ApiErrorResponse() throws IOException, InterruptedException {

        // setup

        when(movieDao.getMovieDetails(4)).thenReturn(new MutableLiveData<>(MOVIE_DETAILS_EXPIRED_TIMESTAMP));

        doNothing().when(movieDao).insertMovieDetails(MOVIE_DETAILS_ONE);

        Observer<DataResource<MovieDetails>> observer = Mockito.mock(Observer.class);

        ApiResponse apiResponse = new ApiResponse();

        when(tmdbInterface.getMovieDetails(4, "en","en,null",
                "videos,images"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.error(HttpURLConnection.HTTP_NOT_FOUND,
                                ResponseBody.create(jsonToString(MOVIE_DETAILS_404),
                                        MediaType.parse(jsonToString(MOVIE_DETAILS_404)))))));

        // act

       LiveData<DataResource<MovieDetails>> result = moviesRepository.fetchMovieDetails(4, "en");

       result.observeForever(observer);


       // verify

        InOrder inOrder = Mockito.inOrder(observer, tmdbInterface, movieDao);

        inOrder.verify(movieDao).getMovieDetails(any(int.class));
        inOrder.verify(observer).onChanged(DataResource.loading(null));
        inOrder.verify(tmdbInterface).getMovieDetails(4,"en", "en,null",
                "videos,images");
        inOrder.verify(movieDao).getMovieDetails(any(int.class));
        inOrder.verify(observer).onChanged(DataResource.error(MOVIE_DETAILS_EXPIRED_TIMESTAMP,
                jsonToString(MOVIE_DETAILS_404)));

    }


    @Test
    public void fetchRatings_noFetchFromNetwork(){

        RATINGS_TWO.setTimestamp(((int) (System.currentTimeMillis() / 1000)));

        when(movieDao.getRatings(TITLE_ONE)).thenReturn(new MutableLiveData<>(RATINGS_TWO));

        // act

        LiveData<DataResource<Ratings>> result = moviesRepository.fetchRatings(TITLE_ONE);

        Observer<DataResource<Ratings>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);


        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao);

        inorder.verify(movieDao).getRatings(TITLE_ONE);

        inorder.verify(observer).onChanged(DataResource.success(RATINGS_TWO));


    }


    @Test
    public void fetchRatings_timestampExpired_apiSuccessResponse(){

        RATINGS_TWO.setTimestamp(((int) (System.currentTimeMillis() / 1000)) -
                Constants.TIME_LIMIT_DETAILS);

        when(movieDao.getRatings(TITLE_ONE)).thenReturn(new MutableLiveData<>(RATINGS_TWO))
                .thenReturn(new MutableLiveData<>(RATINGS_ONE));

        doNothing().when(movieDao).insertRating(RATINGS_ONE);

        ApiResponse apiResponse = new ApiResponse();

        when(omdbInterface.getRatingsByTitle(TITLE_ONE))
                .thenReturn(
                        new MutableLiveData<>(apiResponse.make(Response.success(RATINGS_ONE))));


        // act

        LiveData<DataResource<Ratings>> result = moviesRepository.fetchRatings(TITLE_ONE);

        Observer<DataResource<Ratings>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);


        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao, omdbInterface);

        inorder.verify(movieDao).getRatings(TITLE_ONE);
        inorder.verify(observer).onChanged(DataResource.loading(null));
        inorder.verify(omdbInterface).getRatingsByTitle(TITLE_ONE);
        inorder.verify(movieDao).insertRating(RATINGS_ONE);
        inorder.verify(movieDao).getRatings(TITLE_ONE);
        inorder.verify(observer).onChanged(DataResource.success(RATINGS_ONE));


    }


    @Test
    public void fetchMovieGenres_noFetchFromNetwork(){

        Genre genre = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA
                ,((int) (System.currentTimeMillis() / 1000)), false, false);

        genre.setLanguage("en");

        when(movieDao.getMovieGenres()).thenReturn(new MutableLiveData<>(
                new ArrayList<>(Collections.singletonList(genre))));

        // act

        LiveData<DataResource<List<Genre>>> result = moviesRepository.fetchMovieGenres("en");

        Observer<DataResource<List<Genre>>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);

        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao);

        inorder.verify(movieDao).getMovieGenres();

        inorder.verify(observer).onChanged(DataResource.success(new ArrayList<>(Collections.singletonList(genre))));


    }


    @Test
    public void fetchMovieGenres_changeLanguage_apiSuccessResponse(){

        Genre genre = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA
                ,((int) (System.currentTimeMillis() / 1000)), false, false);

        genre.setLanguage("en");

        when(movieDao.getMovieGenres()).thenReturn(new MutableLiveData<>(
                new ArrayList<>(Collections.singletonList(genre))));

        when(movieDao.insertGenre(genre)).thenReturn((long)0);

        ApiResponse apiResponse = new ApiResponse();

        GenreListResponse genreListResponse = new GenreListResponse();
        genreListResponse.setGenres(new ArrayList<>(Collections.singletonList(genre)));

        when(tmdbInterface.getMovieGenresList("es"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.success(
                     genreListResponse))));

        // act

        LiveData<DataResource<List<Genre>>> result = moviesRepository.fetchMovieGenres("es");

        Observer<DataResource<List<Genre>>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);

        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao, tmdbInterface);

        inorder.verify(movieDao).getMovieGenres();
        inorder.verify(observer).onChanged(DataResource.loading(null));
        inorder.verify(tmdbInterface).getMovieGenresList("es");
        inorder.verify(movieDao).insertGenre(genre);
        inorder.verify(movieDao).getMovieGenres();
        inorder.verify(observer).onChanged(DataResource.success(new ArrayList<>(Collections.singletonList(genre))));


    }


    @Test
    public void fetchMovieGenres_timestampExpired_apiSuccessResponse(){

        Genre genre = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA
                ,((int) (System.currentTimeMillis() / 1000)) - Constants.TIME_LIMIT_GENRE,
                false, false);

        genre.setLanguage("en");

        when(movieDao.getMovieGenres()).thenReturn(new MutableLiveData<>(
                new ArrayList<>(Collections.singletonList(genre))));

        when(movieDao.insertGenre(genre)).thenReturn((long)0);

        ApiResponse apiResponse = new ApiResponse();

        GenreListResponse genreListResponse = new GenreListResponse();
        genreListResponse.setGenres(new ArrayList<>(Collections.singletonList(genre)));

        when(tmdbInterface.getMovieGenresList("en"))
                .thenReturn(new MutableLiveData<>(apiResponse.make(Response.success(
                        genreListResponse))));

        // act

        LiveData<DataResource<List<Genre>>> result = moviesRepository.fetchMovieGenres("en");

        Observer<DataResource<List<Genre>>>  observer = Mockito.mock(Observer.class);

        result.observeForever(observer);

        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao, tmdbInterface);

        inorder.verify(movieDao).getMovieGenres();
        inorder.verify(observer).onChanged(DataResource.loading(null));
        inorder.verify(tmdbInterface).getMovieGenresList("en");
        inorder.verify(movieDao).insertGenre(genre);
        inorder.verify(movieDao).getMovieGenres();
        inorder.verify(observer).onChanged(DataResource.success(new ArrayList<>(Collections.singletonList(genre))));


    }


    @Test
    public void fetchMovies_noFetchFromNetwork(){

        // setup

        List<Result> results = new ArrayList<>();

        Result result  = RESULT_TWO;
        result.setLanguage("en");
        result.setTimestamp(((int) (System.currentTimeMillis() / 1000)));

        List<GenreMovieResultJoin> genreMovieResultJoins = new ArrayList<>();

        for(int i = 0; i < 20; ++i){

            genreMovieResultJoins.add(new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4,
                    (long)4));

        }

        for(int i = 0; i < 20; ++i){

            results.add(result);
        }

        when(movieDao.getGenreAndMovieJoins(GENRE_ID_DRAMA,
                1, Constants.PAGE_SIZE)).thenReturn(genreMovieResultJoins);

        when(movieDao.getMovieResult(ID_ONE)).thenReturn(result);

        // act

        LiveData<DataResource<List<Result>>> resultsLiveData = moviesRepository.fetchMovies
                (1, GENRE_ID_DRAMA, "en");

        Observer<DataResource<List<Result>>>  observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);


        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao);

        inorder.verify(movieDao).getGenreAndMovieJoins(GENRE_ID_DRAMA, 1, Constants.PAGE_SIZE);
        inorder.verify(movieDao, times(20)).getMovieResult(ID_ONE);
        inorder.verify(observer).onChanged(DataResource.success(results));


    }


    @Test
    public void fetchMovies_timestampExpired_apiSuccessResponse(){

        // setup

        // result and genreMovieResultJoin in Room

        Result result  = RESULT_TWO;
        result.setLanguage("en");
        result.setTimestamp(((int) (System.currentTimeMillis() / 1000)) - Constants.TIME_LIMIT_RESULT);

        List<Result> results = new ArrayList<>();
        results.add(result);
        results.add(result);
        results.add(result);

        List<GenreMovieResultJoin> genreMovieResultJoins = new ArrayList<>();
        genreMovieResultJoins.add(new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4,
                (long)4));
        genreMovieResultJoins.add(new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4,
                (long)4));
        genreMovieResultJoins.add(new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_ONE, 4.4,
                (long)4));

        when(movieDao.getGenreAndMovieJoins(GENRE_ID_DRAMA,
                1, Constants.PAGE_SIZE)).thenReturn(genreMovieResultJoins);

        when(movieDao.getMovieResult(ID_ONE)).thenReturn(result);

        doNothing().when(movieDao).insertGenreAndMovieJoin(any(GenreMovieResultJoin.class));
        doNothing().when(movieDao).insertMovieResult(any(ResultMovie.class));

        // firestore

        CollectionReference collectionReference = Mockito.mock(CollectionReference.class);
        Query query = Mockito.mock(Query.class);


        when(Mocks.firestoreMock.collection("movies_en")).thenReturn(collectionReference);
        when(collectionReference.orderBy("popularity", Query.Direction.DESCENDING)).thenReturn(query);
        when(query.limit(Constants.PAGE_SIZE + 1)).thenReturn(query);
        when(query.whereArrayContains("genres", GENRE_ID_DRAMA)).thenReturn(query);
        when(query.get()).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.addOnCompleteListener(any(OnCompleteListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((OnCompleteListener) invocation.getArgument(0)).onComplete(Mocks.taskMock);
                return null;
            }
        });

        when(Mocks.taskMock.isSuccessful()).thenReturn(true);
        QuerySnapshot res = Mockito.mock(QuerySnapshot.class);
        when(Mocks.taskMock.getResult()).thenReturn(res);

        when(res.toObjects(Result.class)).thenReturn(results);

        // act

        LiveData<DataResource<List<Result>>> resultsLiveData = moviesRepository.fetchMovies
                (1, GENRE_ID_DRAMA, "en");

        Observer<DataResource<List<Result>>>  observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);

        // verify

        InOrder inorder = Mockito.inOrder(observer, movieDao, Mocks.firestoreMock);

        // data from Room

        inorder.verify(movieDao).getGenreAndMovieJoins(GENRE_ID_DRAMA, 1, Constants.PAGE_SIZE);
        inorder.verify(movieDao, times(3)).getMovieResult(ID_ONE);

        // need to fetch new data
        inorder.verify(observer).onChanged(DataResource.loading(null));
        inorder.verify(Mocks.firestoreMock).collection("movies_en");

        // three insertions of joins
        inorder.verify(movieDao).insertGenreAndMovieJoin(
                new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_TWO, result.getPopularity(),
                        (long)18398818));

        inorder.verify(movieDao).insertMovieResult(any(ResultMovie.class));

        inorder.verify(movieDao).insertGenreAndMovieJoin(
                new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_TWO, result.getPopularity(),
                        (long)18398818));

        inorder.verify(movieDao).insertMovieResult(any(ResultMovie.class));

        inorder.verify(movieDao).insertGenreAndMovieJoin(
                new GenreMovieResultJoin(GENRE_ID_DRAMA, ID_TWO, result.getPopularity(),
                        (long)18398818));

        inorder.verify(movieDao).insertMovieResult(any(ResultMovie.class));

        // data from Room again

        inorder.verify(movieDao).getGenreAndMovieJoins(GENRE_ID_DRAMA, 1, Constants.PAGE_SIZE);
        inorder.verify(movieDao, times(3)).getMovieResult(ID_ONE);

        inorder.verify(observer).onChanged(DataResource.success(results));


    }


    private String jsonToString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

}
