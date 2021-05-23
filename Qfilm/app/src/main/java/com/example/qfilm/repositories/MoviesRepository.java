package com.example.qfilm.repositories;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.GenreListResponse;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.ResultMovie;
import com.example.qfilm.data.models.entities.ResultSeries;
import com.example.qfilm.data.models.relations.GenreMovieResultJoin;
import com.example.qfilm.data.models.relations.GenreSeriesResultJoin;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.data.sources.local.MovieDao;
import com.example.qfilm.data.sources.remote.OmdbInterface;
import com.example.qfilm.data.sources.remote.TmdbInterface;
import com.example.qfilm.repositories.utils.ApiResponse;
import com.example.qfilm.repositories.utils.AppExecutors;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.repositories.utils.NetworkBoundResource;
import com.example.qfilm.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.qfilm.data.models.entities.MediaType.MOVIE;
import static com.example.qfilm.data.models.entities.MediaType.SERIES;


/**
 *
 * This class has fetch functions that implements the abstract methods in 'NetworkBoundResource': fetchFromDb(),
 * shouldFetch(), createCall() and saveCallResult(), to fetch data from MovieRoomDatabase and under
 * certain conditions update this data by querying the remote Api:s Tmdb API, Omdb Api or Firestore.
 *
 * **/


public class MoviesRepository {

    private static final String TAG = "MoviesRepository";

    private MovieDao movieDao;

    private TmdbInterface tmdbInterface;

    private OmdbInterface omdbInterface;

    private AppExecutors appExecutors;

    private Double lastPopularity;

    private FirebaseFirestore firebaseFirestore;

    public MoviesRepository(MovieDao movieDao, TmdbInterface tmdbInterface, OmdbInterface omdbInterface
            , AppExecutors appExecutors, FirebaseFirestore firebaseFirestore){
        this.movieDao = movieDao;
        this.tmdbInterface = tmdbInterface;
        this.omdbInterface = omdbInterface;
        this.appExecutors = appExecutors;
        this.firebaseFirestore = firebaseFirestore;

    }


    public LiveData<DataResource<List<Genre>>> fetchMovieGenres(String language){
        return new NetworkBoundResource<List<Genre>, GenreListResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GenreListResponse response) {

                for (Genre genre : response.getGenres()) {

                    if (!Constants.GENRES_TO_EXCLUDE.contains(genre.getGenreId())) {

                        genre.setTimestamp((int)(System.currentTimeMillis() / 1000));

                        genre.setMovieGenre(true);

                        genre.setLanguage(language);

                        long res = movieDao.insertGenre(genre);

                        if(res == -1){
                            movieDao.updateIsMovieGenre(true, genre.getGenreId());
                        }
                    }
                }
            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Genre> genres) {

                return shouldFetchNewGenres(genres, language);
            }

            @Override
            protected LiveData<List<Genre>> fetchDataFromDb() {

                return movieDao.getMovieGenres();
            }

            @Override
            protected LiveData<ApiResponse<GenreListResponse>> createCall() {

                return tmdbInterface.getMovieGenresList(language);

            }
        }.getAsLiveData();
    }


    public LiveData<DataResource<List<Genre>>> fetchSeriesGenres(String language){

        return new NetworkBoundResource<List<Genre>, GenreListResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GenreListResponse response) {

                movieDao.deleteAllGenres();

                for (Genre genre : response.getGenres()) {

                    if (!Constants.GENRES_TO_EXCLUDE.contains(genre.getGenreId())) {

                        genre.setSeriesGenre(true);

                        genre.setLanguage(language);

                        genre.setTimestamp((int)(System.currentTimeMillis() / 1000));

                        long res = movieDao.insertGenre(genre);

                        if(res == -1){
                            movieDao.updateIsSeriesGenre(true, genre.getGenreId());
                        }
                    }
                }

            }


            @Override
            protected Boolean shouldFetch(@Nullable List<Genre> genres) {

               return shouldFetchNewGenres(genres, language);
            }

            @Override
            protected LiveData<List<Genre>> fetchDataFromDb() {
                return movieDao.getSeriesGenres();
            }

            @Override
            protected LiveData<ApiResponse<GenreListResponse>> createCall() {

                return tmdbInterface.getSeriesGenresList(language);
            }
        }.getAsLiveData();
    }


    public LiveData<DataResource<List<Result>>> fetchSeries(Integer page, Integer genreId, String language){

        return new NetworkBoundResource<List<Result>, List<Result>>(appExecutors){

            @Override
            protected void saveCallResult(@NonNull List<Result> response) {

                for(Result result : response){

                    Long primaryKey = Long.parseLong(genreId.toString() + result.getResultId().toString());

                    GenreSeriesResultJoin genreSeriesResultJoin = new GenreSeriesResultJoin(genreId,
                            result.getResultId(), result.getPopularity(), primaryKey);

                    movieDao.insertGenreAndSeriesJoin(genreSeriesResultJoin);

                    result.setTimestamp((int) (System.currentTimeMillis() / 1000));

                    result.setMediaType(SERIES);

                    ResultSeries resultSeries = new ResultSeries(result);

                    resultSeries.setLanguage(language);

                    movieDao.insertSeriesResult(resultSeries);
                }

            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Result> results) {

                Boolean shouldFetch = shouldFetchNewResults(results, language);

                if(shouldFetch && results != null && results.size() > 0){

                    lastPopularity = results.get(0).getPopularity(); // so that fireStore fetch knows where to start in createCall()
                }

                return shouldFetch;
            }


            @Override
            protected LiveData<List<Result>> fetchDataFromDb() {

                MutableLiveData<List<Result>> resultsLiveData = new MutableLiveData<>();

                appExecutors.getRepositoryThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        List<Result> results = new ArrayList<>();

                        List<GenreSeriesResultJoin> joins = movieDao.getGenreAndSeriesJoins(genreId,
                                page, Constants.PAGE_SIZE);


                        for(GenreSeriesResultJoin join : joins){

                            Result result =  movieDao.getSeriesResult(join.getResultId());

                            results.add(result);

                        }

                        resultsLiveData.postValue(results);

                    }
                });

                return resultsLiveData;
            }

            @Override
            protected LiveData<ApiResponse<List<Result>>> createCall() {

                MutableLiveData<ApiResponse<List<Result>>> results = new MutableLiveData<>();

                CollectionReference collectionReference =   firebaseFirestore.collection("series_" + language);

                final ApiResponse apiResponse = new ApiResponse();

                Query query = collectionReference
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(Constants.PAGE_SIZE + 1);

                if(page != 1){

                    query = query.startAt(lastPopularity);
                }

                if(genreId != -1){

                    query = query.whereArrayContains("genres", genreId);
                }

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<Result> res = task.getResult().toObjects(Result.class);

                            results.setValue(apiResponse.success(res));

                        }else{

                            results.setValue(apiResponse.error(task.getException()));

                        }
                    }
                });

                return results;
            }
        }.getAsLiveData();

    }


    public LiveData<DataResource<List<Result>>> fetchMovies(Integer page, Integer genreId, String language){
        return new NetworkBoundResource<List<Result>, List<Result>>(appExecutors){

            @Override
            protected void saveCallResult(@NonNull List<Result> response) {

                for(Result result : response){

                    Long primaryKey =
                            Long.parseLong(genreId.toString() + result.getResultId().toString());

                    GenreMovieResultJoin genreMovieResultJoin = new GenreMovieResultJoin(genreId,
                            result.getResultId(), result.getPopularity(), primaryKey);

                    movieDao.insertGenreAndMovieJoin(genreMovieResultJoin);

                    result.setTimestamp((int) (System.currentTimeMillis() / 1000));

                    result.setMediaType(MOVIE);

                    ResultMovie resultMovie = new ResultMovie(result);

                    resultMovie.setLanguage(language);

                    movieDao.insertMovieResult(resultMovie);
                }

            }

            @Override
            protected Boolean shouldFetch(@Nullable List<Result> results) {

                Boolean shouldFetch = shouldFetchNewResults(results, language);

                if(shouldFetch && results != null && results.size() > 0){

                    lastPopularity = results.get(0).getPopularity(); // so that fireStore fetch knows where to start in createCall()
                }

                return shouldFetch;
            }

            @Override
            protected LiveData<List<Result>> fetchDataFromDb() {

                MutableLiveData<List<Result>> resultsLiveData = new MutableLiveData<>();

                appExecutors.getRepositoryThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        List<Result> results = new ArrayList<>();

                        List<GenreMovieResultJoin> joins = movieDao.getGenreAndMovieJoins(genreId,
                                page, Constants.PAGE_SIZE);

                        for(GenreMovieResultJoin join : joins){

                          Result result =  movieDao.getMovieResult(join.getResultId());

                          results.add(result);

                        }

                        resultsLiveData.postValue(results);

                    }
                });

                return resultsLiveData;
            }

            @Override
            protected LiveData<ApiResponse<List<Result>>> createCall() {

                MutableLiveData<ApiResponse<List<Result>>> results = new MutableLiveData<>();

                CollectionReference collectionReference = firebaseFirestore.collection("movies_" + language);

                final ApiResponse apiResponse = new ApiResponse();

                Query query = collectionReference
                        .orderBy("popularity", Query.Direction.DESCENDING)
                        .limit(Constants.PAGE_SIZE + 1);

                if(page  != 1) {
                    query = query.startAt(lastPopularity);
                }

                if(genreId != -1){

                    query = query.whereArrayContains("genres", genreId);
                }

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<Result> res = task.getResult().toObjects(Result.class);

                            results.setValue(apiResponse.success(res));

                        }else{

                            results.setValue(apiResponse.error(task.getException()));

                        }
                    }
                });

                return results;
            }

        }.getAsLiveData();
    }


   public LiveData<DataResource<Ratings>> fetchRatings(String title) {
        return new NetworkBoundResource<Ratings, Ratings>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Ratings ratings) {

                ratings.setTitle(title);

                ratings.setTimestamp((int)(System.currentTimeMillis() / 1000));

                movieDao.insertRating(ratings);

            }

            @Override
            protected Boolean shouldFetch(@Nullable Ratings ratings) {

                return ratings == null ||
                        hasTimeStampExpired(ratings.getTimestamp(), Constants.TIME_LIMIT_DETAILS);

            }

            @Override
            protected LiveData<Ratings> fetchDataFromDb() {

                return movieDao.getRatings(title);

            }

            @Override
            protected LiveData<ApiResponse<Ratings>> createCall() {

                return omdbInterface.getRatingsByTitle(title);

            }
        }.getAsLiveData();
    }


    public LiveData<DataResource<MovieDetails>> fetchMovieDetails(Integer id, String language){
        return new NetworkBoundResource<MovieDetails, MovieDetails>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull MovieDetails movieDetails) {

                    movieDetails.setTimestamp((int)(System.currentTimeMillis() / 1000));

                    movieDetails.setLanguage(language);

                    movieDao.insertMovieDetails(movieDetails);

            }

            @Override
            protected Boolean shouldFetch(@Nullable MovieDetails movieDetails) {

                return movieDetails == null || !movieDetails.getLanguage().equals(language) ||
                        hasTimeStampExpired(movieDetails.getTimestamp(), Constants.TIME_LIMIT_DETAILS);
            }

            @Override
            protected LiveData<MovieDetails> fetchDataFromDb() {

                return movieDao.getMovieDetails(id);
            }

            @Override
            protected LiveData<ApiResponse<MovieDetails>> createCall() {

                return tmdbInterface.getMovieDetails(id, language,
                        language + ",null","videos,images");
            }
        }.getAsLiveData();
    }


    public LiveData<DataResource<SeriesDetails>> fetchSeriesDetails(Integer id, String language) {
        return new NetworkBoundResource<SeriesDetails, SeriesDetails>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull SeriesDetails seriesDetails) {

                seriesDetails.setTimestamp((int) (System.currentTimeMillis() / 1000));

                seriesDetails.setLanguage(language);

                movieDao.insertSeriesDetails(seriesDetails);

            }

            @Override
            protected Boolean shouldFetch(@Nullable SeriesDetails seriesDetails) {

                 return seriesDetails == null || !seriesDetails.getLanguage().equals(language) ||
                        hasTimeStampExpired(seriesDetails.getTimestamp(), Constants.TIME_LIMIT_DETAILS);

            }

            @Override
            protected LiveData<SeriesDetails> fetchDataFromDb() {

                return movieDao.getSeriesDetails(id);
            }

            @Override
            protected LiveData<ApiResponse<SeriesDetails>> createCall() {

                return tmdbInterface.getSeriesDetails(id, language,
                        language + ",null","videos,images");

            }
        }.getAsLiveData();
    }


    private Boolean hasTimeStampExpired(int timestamp, int limit){

        int currentSeconds = (int) (System.currentTimeMillis() / 1000);
        int diff = currentSeconds - timestamp;
        return diff >= limit;
    }


    private Boolean shouldFetchNewResults(List<Result> results, String language){

        if(results == null || results.size() < Constants.PAGE_SIZE){
            return true;
        }else{

            for(Result result : results){
                if(hasTimeStampExpired(result.getTimestamp(), Constants.TIME_LIMIT_RESULT)){
                    return true;
                }else if(!result.getLanguage().equals(language)){
                    return true;
                }
            }
        }

        return false;
    }


    private Boolean shouldFetchNewGenres(List<Genre> genres, String language){

        if(genres == null || genres.isEmpty()){
            return true;
        }else{

            for(Genre genre : genres){

                if(hasTimeStampExpired(genre.getTimestamp(), Constants.TIME_LIMIT_GENRE)){
                    return true;
                }
                else if(!genre.getLanguage().equals(language)){
                    return true;
                }
            }
        }

        return false;

    }


}
