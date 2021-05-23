package com.example.qfilm.di;

import com.example.qfilm.repositories.utils.AppExecutors;
import com.example.qfilm.repositories.utils.MainThreadExecutor;

import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;

@Module
public class AppExecutorModule {

    @Provides
    AppExecutors provideAppExecutors(){
        return new AppExecutors(Executors.newSingleThreadExecutor(),
                new MainThreadExecutor(), Executors.newSingleThreadExecutor());
    }
}
