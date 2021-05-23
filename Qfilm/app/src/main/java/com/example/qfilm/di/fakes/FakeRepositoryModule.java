package com.example.qfilm.di.fakes;

import com.example.qfilm.repositories.MoviesRepository;

import dagger.Module;
import dagger.Provides;
import utils.Mocks;

@Module
public class FakeRepositoryModule {

    @Provides
    public static MoviesRepository provideMovieRepository(){
        return Mocks.moviesRepositoryMock;
    }

}
