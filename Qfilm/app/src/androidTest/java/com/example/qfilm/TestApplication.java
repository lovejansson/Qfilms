package com.example.qfilm;

import com.example.qfilm.di.AppModule;
import com.example.qfilm.di.ApplicationComponent;
import com.example.qfilm.di.DaggerTestApplicationComponent;

public class TestApplication extends MainApplication {

   private ApplicationComponent applicationComponent;

    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerTestApplicationComponent.builder()
                .appModule(new AppModule(this))
                .build();



    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
