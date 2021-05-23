package com.example.qfilm.di;


import com.example.qfilm.di.fakes.FakeFireBaseModule;
import com.example.qfilm.di.fakes.FakeRepositoryModule;
import com.example.qfilm.di.fakes.FakeViewModelFactoryModule;
import com.example.qfilm.ui.activities.MainActivity;
import com.example.qfilm.ui.activities.SettingsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppExecutorModule.class, AppModule.class, FakeRepositoryModule.class, RetrofitModule.class,
        RoomModule.class, FakeFireBaseModule.class, FakeViewModelFactoryModule.class})
@Singleton
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(SettingsActivity settingsActivity);
    void inject(MainActivity mainActivity);

}

