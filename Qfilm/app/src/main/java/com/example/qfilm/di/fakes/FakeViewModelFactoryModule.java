package com.example.qfilm.di.fakes;

import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import dagger.Module;
import dagger.Provides;
import utils.MyTestViewModelFactory;

@Module
public class FakeViewModelFactoryModule {

    @Provides
    public static MyViewModelFactory provideViewModelFactory(){
        return new MyTestViewModelFactory();
    }

}


