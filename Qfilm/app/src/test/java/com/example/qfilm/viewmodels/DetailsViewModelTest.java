package com.example.qfilm.viewmodels;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.repositories.utils.DataResource;
import utils.Mocks;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.RATINGS_ONE;
import static utils.TestDummyData.RATING_VALUE_ONE;
import static utils.TestDummyData.SERIES_DETAILS;


public class DetailsViewModelTest {

    // tested
    DetailsViewModel detailsViewModel;

    // Observer and observables
    private Observer observer;
    private LiveData<DataResource<MovieDetails>> movieDetailsLiveData;
    private LiveData<DataResource<SeriesDetails>> seriesDetailsLiveData;

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

        detailsViewModel  = new DetailsViewModel(Mocks.moviesRepositoryMock);

        observer = Mockito.mock(Observer.class);

        movieDetailsLiveData = detailsViewModel.getMovieDetailsLiveData();
        seriesDetailsLiveData = detailsViewModel.getSeriesDetailsLiveData();

        movieDetailsLiveData.observeForever(observer);
        seriesDetailsLiveData.observeForever(observer);

    }


    @Test
    public void getMovieDetails_SuccessResponseBothMovieDetailsAndRatings(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieDetails( 4,"en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(MOVIE_DETAILS_ONE)));

        when(Mocks.moviesRepositoryMock.fetchRatings(MOVIE_DETAILS_ONE.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.success(RATINGS_ONE)
        ));


        // act

        detailsViewModel.getMovieDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchMovieDetails(4, "en");

        inOrder.verify(Mocks.moviesRepositoryMock).fetchRatings(MOVIE_DETAILS_ONE.getTitle());

        inOrder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_ONE));

    }


    @Test
    public void getMovieDetails_SuccessResponseMovieDetailsErrorResponseRatings(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(MOVIE_DETAILS_ONE)));

        when(Mocks.moviesRepositoryMock.fetchRatings(MOVIE_DETAILS_ONE.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.error(null, "unknown error")
        ));

        // act

        detailsViewModel.getMovieDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchMovieDetails(4, "en");

        inOrder.verify(Mocks.moviesRepositoryMock).fetchRatings(MOVIE_DETAILS_ONE.getTitle());

        inOrder.verify(observer).onChanged(DataResource.success(MOVIE_DETAILS_ONE));

    }


    @Test
    public void getMovieDetails_LoadingResponseMovieDetails(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.loading(null)));


        // act

        detailsViewModel.getMovieDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchMovieDetails(4, "en");

        inOrder.verify(observer).onChanged(DataResource.loading(null));

    }


    @Test
    public void getMovieDetails_ErrorResponseMovieDetails(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.error(null, "Unknown error")));


        // act

        detailsViewModel.getMovieDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchMovieDetails(4, "en");

        inOrder.verify(observer).onChanged(DataResource.error(null, "Unknown error"));

    }


    @Test
    public void getSeriesDetails_SuccessResponseBothMovieDetailsAndRatings(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchSeriesDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(SERIES_DETAILS)));

        when(Mocks.moviesRepositoryMock.fetchRatings(SERIES_DETAILS.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.success(RATINGS_ONE)
        ));


        // act

        detailsViewModel.getSeriesDetails(4, "en");


        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchSeriesDetails(4, "en");

        inOrder.verify(Mocks.moviesRepositoryMock).fetchRatings(SERIES_DETAILS.getTitle());

        inOrder.verify(observer).onChanged(DataResource.success(SERIES_DETAILS));

    }


    @Test
    public void getSeriesDetails_SuccessResponseMovieDetailsErrorResponseRatings(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchSeriesDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(SERIES_DETAILS)));

        when(Mocks.moviesRepositoryMock.fetchRatings(SERIES_DETAILS.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.error(null, "unknown error")
        ));


        // act

        detailsViewModel.getSeriesDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchSeriesDetails(4, "en");

        inOrder.verify(Mocks.moviesRepositoryMock).fetchRatings(SERIES_DETAILS.getTitle());

        inOrder.verify(observer).onChanged(DataResource.success(SERIES_DETAILS));

    }


    @Test
    public void getSeriesDetails_LoadingResponseMovieDetails(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchSeriesDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.loading(null)));

        // act

        detailsViewModel.getSeriesDetails(4, "en");


        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchSeriesDetails(4, "en");

        inOrder.verify(observer).onChanged(DataResource.loading(null));

    }


    @Test
    public void getSeriesDetails_ErrorResponseMovieDetails(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchSeriesDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.error(null, "Unknown error")));


        // act

        detailsViewModel.getSeriesDetails(4, "en");

        // verify

        InOrder inOrder = Mockito.inOrder(observer, Mocks.moviesRepositoryMock);

        inOrder.verify(Mocks.moviesRepositoryMock).fetchSeriesDetails(4, "en");

        inOrder.verify(observer).onChanged(DataResource.error(null, "Unknown error"));

    }


    @Test
    public void getSeriesDetails_checkCorrectRatingsAreSet(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchSeriesDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(SERIES_DETAILS)));

        when(Mocks.moviesRepositoryMock.fetchRatings(SERIES_DETAILS.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.success(RATINGS_ONE)
        ));


        LiveData<DataResource<SeriesDetails>> seriesDetailsLiveData = detailsViewModel.getSeriesDetailsLiveData();

        Observer<DataResource<SeriesDetails>> observer = new Observer<DataResource<SeriesDetails>>() {
            @Override
            public void onChanged(DataResource<SeriesDetails> seriesDetailsDataResource) {

                // assert

                Assert.assertEquals(RATING_VALUE_ONE, seriesDetailsDataResource.getData().getImdbRating());

            }
        };

        seriesDetailsLiveData.observeForever(observer);

        // act

        detailsViewModel.getSeriesDetails(4, "en");


    }


    @Test
    public void getMovieDetails_checkCorrectRatingsAreSet(){

        // setup

        when(Mocks.moviesRepositoryMock.fetchMovieDetails(4, "en"))
                .thenReturn(new MutableLiveData<>(DataResource.success(SERIES_DETAILS)));

        when(Mocks.moviesRepositoryMock.fetchRatings(SERIES_DETAILS.getTitle())).thenReturn(new MutableLiveData<>(
                DataResource.success(RATINGS_ONE)
        ));


        LiveData<DataResource<MovieDetails>> movieDetailsLiveData = detailsViewModel.getMovieDetailsLiveData();

        Observer<DataResource<MovieDetails>> observer = new Observer<DataResource<MovieDetails>>() {
            @Override
            public void onChanged(DataResource<MovieDetails> movieDetailsDataResource) {

                // assert

                Assert.assertEquals(RATING_VALUE_ONE, movieDetailsDataResource.getData().getImdbRating());

            }
        };

        movieDetailsLiveData.observeForever(observer);

        // act

        detailsViewModel.getMovieDetails(4, "en");

    }

}

