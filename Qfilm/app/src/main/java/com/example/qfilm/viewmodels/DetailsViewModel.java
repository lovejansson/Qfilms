package com.example.qfilm.viewmodels;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Rating;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.repositories.MoviesRepository;

import static com.example.qfilm.repositories.utils.DataResource.Status.ERROR;
import static com.example.qfilm.repositories.utils.DataResource.Status.SUCCESS;

public class DetailsViewModel extends ViewModel {

    private static final String TAG = "DetailsViewModel";

    public MoviesRepository moviesRepository;

    private MediatorLiveData<DataResource<MovieDetails>> movieDetails;

    private MediatorLiveData<DataResource<SeriesDetails>> seriesDetails;


    public DetailsViewModel(MoviesRepository moviesRepository) {

        this.moviesRepository = moviesRepository;
        movieDetails = new MediatorLiveData<>();
        seriesDetails = new MediatorLiveData<>();
    }


    public LiveData<DataResource<MovieDetails>> getMovieDetailsLiveData() {
        return movieDetails;
    }


    public LiveData<DataResource<SeriesDetails>> getSeriesDetailsLiveData() {
        return seriesDetails;
    }


    public Void getMovieDetails(Integer id, String language){

        /**
         * getting the details of a movie via the repository and then asking
         * for ratings (fetched from another api). The result is set in 'movieDetails' and
         * thereby UI is notified.
         *
         **/

        LiveData<DataResource<MovieDetails>> movieDetailsLiveData =
                moviesRepository.fetchMovieDetails(id, language);

        movieDetails.addSource(movieDetailsLiveData, new Observer<DataResource<MovieDetails>>() {
            @Override
            public void onChanged(DataResource<MovieDetails> movieDetailsDataResource) {

                if(movieDetailsDataResource.getStatus() == SUCCESS){

                LiveData<DataResource<Ratings>> ratings =
                        moviesRepository.fetchRatings(movieDetailsDataResource.getData().getTitle());

                movieDetails.removeSource(movieDetailsLiveData);

                movieDetails.addSource(ratings, new Observer<DataResource<Ratings>>() {
                    @Override
                    public void onChanged(DataResource<Ratings> ratingsDataResource) {

                        if (ratingsDataResource.getStatus() == SUCCESS ||
                                ratingsDataResource.getStatus() == ERROR) {

                                movieDetails.removeSource(ratings);

                                if(hasRatings(ratingsDataResource)) {

                                    Rating imdbRating = ratingsDataResource.getData().getRatings().get(0);

                                    if (imdbRating != null) {

                                        movieDetailsDataResource.getData()
                                                .setImdbRating(imdbRating.getValue());
                                    }
                            }

                            movieDetails.setValue(movieDetailsDataResource);

                            if(ratingsDataResource.getStatus() == ERROR){

                                Log.d(TAG, "onChanged: fetchRatings " +
                                        ratingsDataResource.getMessage());
                            }

                        }
                    }
                });

                }else{

                    // if DataSource status is loading or has error the ui is notified without fetch for ratings
                    movieDetails.setValue(movieDetailsDataResource);
                }

            }
        });

        return null;

    }


    public void getSeriesDetails(Integer id, String language){

        /**
        * getting the details of a series via the repository and then asking
        * for ratings (fetched from another api). The result is set in 'seriesDetails' and
        * thereby UI is notified.
        **/

        LiveData<DataResource<SeriesDetails>> seriesDetailsLiveData =
                moviesRepository.fetchSeriesDetails(id, language);

        seriesDetails.addSource(seriesDetailsLiveData, new Observer<DataResource<SeriesDetails>>() {
            @Override
            public void onChanged(DataResource<SeriesDetails> seriesDetailsDataResource) {

                if (seriesDetailsDataResource.getStatus() == SUCCESS) {

                    LiveData<DataResource<Ratings>> ratings =
                            moviesRepository.fetchRatings(seriesDetailsDataResource.getData().getTitle());

                    seriesDetails.removeSource(seriesDetailsLiveData);

                    seriesDetails.addSource(ratings, new Observer<DataResource<Ratings>>() {
                        @Override
                        public void onChanged(DataResource<Ratings> ratingsDataResource) {
                            {

                                if (ratingsDataResource.getStatus() == SUCCESS ||
                                        ratingsDataResource.getStatus() == ERROR) {

                                    movieDetails.removeSource(ratings);

                                    if (hasRatings(ratingsDataResource)) {

                                        Rating imdbRating =
                                                ratingsDataResource.getData().getRatings().get(0);

                                        if (imdbRating != null) {

                                            seriesDetailsDataResource.getData()
                                                    .setImdbRating(imdbRating.getValue());
                                        }

                                    }

                                    seriesDetails.setValue(seriesDetailsDataResource);


                                    if(ratingsDataResource.getStatus() == ERROR){

                                        Log.d(TAG, "onChanged: fetchRatings " +
                                                ratingsDataResource.getMessage());
                                    }

                                }
                            }
                        }
                    });

                }else{

                    // if DataSource status is loading or has error the ui is notified
                    // without fetch for ratings
                    seriesDetails.setValue(seriesDetailsDataResource);
                }
            }
        });

    }


    private Boolean hasRatings(DataResource<Ratings> ratings){

        return ratings.getData() != null && ratings.getData().getRatings() != null &&
                ratings.getData().getRatings().size() > 0;
    }

}
