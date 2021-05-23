package com.example.qfilm.di;

import android.app.Application;

import androidx.room.Room;

import com.example.qfilm.data.sources.local.MovieDao;
import com.example.qfilm.data.sources.local.MoviesRoomDatabase;
import com.example.qfilm.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {


    @Provides
    @Singleton
    public static MoviesRoomDatabase provideMoviesRoomDatabase(Application application){
        return Room.databaseBuilder(application.getApplicationContext(),
                MoviesRoomDatabase.class, Constants.ROOM_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }


    @Provides
    @Singleton
    public static MovieDao provideMovieDao(MoviesRoomDatabase moviesRoomDatabase){
        return moviesRoomDatabase.movieDao();
    }

}
