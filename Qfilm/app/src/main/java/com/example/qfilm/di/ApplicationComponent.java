package com.example.qfilm.di;

import com.example.qfilm.ui.activities.MainActivity;
import com.example.qfilm.ui.activities.SettingsActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component (modules = {AppExecutorModule.class, AppModule.class, RepositoryModule.class, RetrofitModule.class,
RoomModule.class, FireBaseModule.class, ViewModelFactoryModule.class})

public interface ApplicationComponent {

    void inject(MainActivity mainActivity);
    void inject(SettingsActivity settingsActivity);

}
