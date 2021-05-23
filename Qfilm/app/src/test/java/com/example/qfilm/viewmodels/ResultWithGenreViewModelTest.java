package com.example.qfilm.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils.Mocks;

import static com.example.qfilm.data.models.entities.MediaType.MOVIE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.DRAMA;
import static utils.TestDummyData.GENRES_ONE;
import static utils.TestDummyData.GENRE_ID_DRAMA;
import static utils.TestDummyData.RESULT_ONE;


public class ResultWithGenreViewModelTest {


    // tested
    ResultWithGenreViewModel resultWithGenreViewModel;


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
        resultWithGenreViewModel = new ResultWithGenreViewModel(Mocks.moviesRepositoryMock);
    }


    @Test
    public void fetchGenres_successResponse(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieGenres("en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(GENRES_ONE)));

        LiveData<DataResource<List<Genre>>> genresLiveData = resultWithGenreViewModel.getGenresObservable();

        Observer<DataResource<List<Genre>>> observer = Mockito.mock(Observer.class);

        genresLiveData.observeForever(observer);


        // act

        resultWithGenreViewModel.fetchGenres(MOVIE, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovieGenres("en");

        inorder.verify(observer).onChanged(DataResource.success(GENRES_ONE));

    }


    @Test
    public void fetchGenres_errorResponse(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieGenres("en"))
                .thenReturn(new MutableLiveData<>(DataResource.error(GENRES_ONE, "network error")));

        LiveData<DataResource<List<Genre>>> genresLiveData = resultWithGenreViewModel.getGenresObservable();

        Observer<DataResource<List<Genre>>> observer = Mockito.mock(Observer.class);

        genresLiveData.observeForever(observer);


        // act

        resultWithGenreViewModel.fetchGenres(MOVIE, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovieGenres("en");

        inorder.verify(observer).onChanged(DataResource.error(GENRES_ONE, "network error"));


    }


    @Test
    public void fetchGenres_loadingResponse(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieGenres("en"))
                .thenReturn(new MutableLiveData<>(DataResource.loading(null)));

        LiveData<DataResource<List<Genre>>> genresLiveData = resultWithGenreViewModel.getGenresObservable();

        Observer<DataResource<List<Genre>>> observer = Mockito.mock(Observer.class);

        genresLiveData.observeForever(observer);


        // act

        resultWithGenreViewModel.fetchGenres(MOVIE, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovieGenres("en");

        inorder.verify(observer).onChanged(DataResource.loading(null));


    }


    @Test
    public void fetchResultWithGenre_loadingResponse(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovies(1, GENRE_ID_DRAMA, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.loading(null)));


        LiveData<DataResource<List<Result>>> resultsLiveData = resultWithGenreViewModel.getResultsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);

        // act

        resultWithGenreViewModel.fetchResultsWithGenre(MOVIE, DRAMA, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovies(1, GENRE_ID_DRAMA, "en");

        inorder.verify(observer).onChanged(DataResource.loading(null));

    }


    @Test
    public void fetchResultWithGenre_errorResponse(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovies(1, GENRE_ID_DRAMA, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.error(null, "network error")));

        LiveData<DataResource<List<Result>>> resultsLiveData = resultWithGenreViewModel.getResultsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);

        // act

        resultWithGenreViewModel.fetchResultsWithGenre(MOVIE, DRAMA, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovies(1, GENRE_ID_DRAMA, "en");

        inorder.verify(observer).onChanged(DataResource.error(null, "network error"));

    }


    @Test
    public void fetchResultWithGenre_successResponse(){

        /**
         *
         * page 1
         *
       **/


        // setup

        List<Result> results = new ArrayList<>();

        for(int i = 0; i < 21; ++i){
            results.add(RESULT_ONE);
        }


        when(Mocks.moviesRepositoryMock.fetchMovies(1, GENRE_ID_DRAMA, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(results)));

        when(Mocks.moviesRepositoryMock.fetchMovies(2, GENRE_ID_DRAMA, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(Collections.singletonList(RESULT_ONE))));


        LiveData<DataResource<List<Result>>> resultsLiveData = resultWithGenreViewModel.getResultsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);

        // act

        resultWithGenreViewModel.fetchResultsWithGenre(MOVIE, DRAMA, "en");

        // verify

        InOrder inorder = Mockito.inOrder(Mocks.moviesRepositoryMock, observer);

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovies(1, GENRE_ID_DRAMA, "en");


        results.remove(results.size() - 1);

        inorder.verify(observer).onChanged(DataResource.success(results));

        assertEquals(false, resultWithGenreViewModel.getReachedEndOfList());

        /**
         *
         * page 2 (last page)
         *
      **/

        // setup


        // act

        resultWithGenreViewModel.fetchNextPage(MOVIE, DRAMA, "en");

        // verify

        inorder.verify(Mocks.moviesRepositoryMock).fetchMovies(2, GENRE_ID_DRAMA, "en");

        inorder.verify(observer).onChanged(DataResource.success(Collections.singletonList(RESULT_ONE)));

        assertEquals(true, resultWithGenreViewModel.getReachedEndOfList());


        // fetch next page is not possible now

        resultWithGenreViewModel.fetchNextPage(MOVIE, DRAMA, "en");

        verifyNoMoreInteractions(Mocks.moviesRepositoryMock, observer);

    }


    @After
    public void tearDown(){
        Mocks.reset();
    }


}




