package com.example.qfilm;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.qfilm.di.AppModule;
import com.example.qfilm.di.ApplicationComponent;
import com.example.qfilm.di.DaggerApplicationComponent;
import com.example.qfilm.ui.utils.SettingsManager;


public class MainApplication extends Application {

    private static final String TAG = "MainApplication";

    private ApplicationComponent applicationComponent;

    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .build();

    }

    public void setApplicationComponent(ApplicationComponent applicationComponent) {

        if(applicationComponent != null){
            Log.d(TAG, "setApplicationComponent: " + applicationComponent.getClass().getSimpleName());
        }

        this.applicationComponent = applicationComponent;
    }

    public ApplicationComponent getApplicationComponent() {

        return applicationComponent;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(SettingsManager.setAppLanguage(base));
    }
}
