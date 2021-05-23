package com.example.qfilm.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;

import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.repositories.MoviesRepository;
import com.example.qfilm.utils.Constants;

import java.util.List;

import static com.example.qfilm.data.models.entities.MediaType.MOVIE;


public class ResultWithGenreViewModel extends ViewModel {

    private static final String TAG = "ResultWithGenreViewModel";

    public MoviesRepository moviesRepository;

    private MediatorLiveData<DataResource<List<Genre>>> genres;

    private MediatorLiveData<DataResource<List<Result>>> results;

    private Boolean reachedEndOfList;

    private Integer currentPage;


    public ResultWithGenreViewModel(MoviesRepository moviesRepository){

        this.moviesRepository = moviesRepository;

        genres = new MediatorLiveData<>();

        results = new MediatorLiveData<>();

        reachedEndOfList = false;
    }


    /**
     *
     * getters
     *
     * **/


    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public LiveData<DataResource<List<Result>>> getResultsObservable(){
       return results;
    }

    public LiveData<DataResource<List<Genre>>> getGenresObservable(){

        return genres;
    }


    public Boolean getReachedEndOfList() {

        return reachedEndOfList;
    }


    /**
     *
     * public fetch methods
     *
     * **/


    public Void fetchResultsWithGenre(MediaType mediaType, Genre genre, String language){

        reachedEndOfList = false;

        currentPage = 1;

        LiveData<DataResource<List<Result>>> results;

        if(mediaType  == MOVIE){

            results = moviesRepository.fetchMovies(currentPage, genre.getGenreId(), language);


        }else{

            results = moviesRepository.fetchSeries(currentPage, genre.getGenreId(), language);

        }

        observeResults(results);

        return null;

    }



    public Void fetchGenres(MediaType mediaType, String language){

        LiveData<DataResource<List<Genre>>> genres;

        if(mediaType == MOVIE){

            genres = moviesRepository.fetchMovieGenres(language);

        }else{

            genres = moviesRepository.fetchSeriesGenres(language);
        }

        observeGenres(genres);

        return null;
    }


    public Void fetchNextPage(MediaType  mediaType, Genre genre, String language){

        if(!reachedEndOfList){

            LiveData<DataResource<List<Result>>> results;

            if(mediaType == MOVIE){

                results = moviesRepository.fetchMovies(++currentPage, genre.getGenreId(), language);


            }else{

                results = moviesRepository.fetchSeries(++currentPage, genre.getGenreId(), language);
            }

            observeResults(results);
        }



        return null;
    }


    /**
     *
     * observe methods
     *
     * **/


    private void observeResults(LiveData<DataResource<List<Result>>> resultsLiveData){

        results.addSource(resultsLiveData, new Observer<DataResource<List<Result>>>() {
            @Override
            public void onChanged(DataResource<List<Result>> res) {

                switch(res.getStatus()) {
                    case SUCCESS:

                        if(res.getData().size() <= Constants.PAGE_SIZE){

                            reachedEndOfList = true;

                        }else{
                            res.getData().remove(res.getData().size() - 1);
                        }

                        results.setValue(DataResource.success(res.getData()));

                        results.removeSource(resultsLiveData);

                        break;

                    case LOADING:
                        results.setValue(res);
                        break;

                    case ERROR:

                        results.removeSource(resultsLiveData);

                        results.setValue(DataResource.error(
                                res.getData() == null || res.getData().isEmpty()?
                                        null : res.getData(), res.getMessage()));
                        break;
                }
            }
        });
    }


    private void observeGenres(LiveData<DataResource<List<Genre>>> genresLiveData){
        genres.addSource(genresLiveData, new Observer<DataResource<List<Genre>>>() {
            @Override
            public void onChanged(DataResource<List<Genre>> res) {

                switch(res.getStatus()){

                    case SUCCESS:
                    case ERROR:

                        genres.removeSource(genresLiveData);

                        genres.setValue(res);

                        break;

                    case LOADING:

                        genres.setValue(res);

                        break;

                }
            }

        });

    }

}

