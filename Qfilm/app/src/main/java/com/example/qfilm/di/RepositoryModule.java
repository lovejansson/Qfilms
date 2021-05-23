package com.example.qfilm.di;

import com.example.qfilm.data.sources.remote.OmdbInterface;
import com.example.qfilm.data.sources.remote.TmdbInterface;
import com.example.qfilm.data.sources.local.MovieDao;
import com.example.qfilm.repositories.utils.AppExecutors;
import com.example.qfilm.repositories.MoviesRepository;
import com.google.firebase.firestore.FirebaseFirestore;


import dagger.Module;
import dagger.Provides;


@Module
public class RepositoryModule {

    @Provides
    public static MoviesRepository provideMovieRepository(MovieDao movieDao,
                                                          TmdbInterface tmdbInterface,
                                                          OmdbInterface omdbInterface,
                                                          AppExecutors appExecutors, FirebaseFirestore firebaseFirestore){
        return new MoviesRepository(movieDao, tmdbInterface, omdbInterface, appExecutors, firebaseFirestore);

    }
}
