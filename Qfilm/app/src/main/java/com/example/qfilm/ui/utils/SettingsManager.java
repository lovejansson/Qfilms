package com.example.qfilm.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.qfilm.R;

import java.util.Locale;

public class SettingsManager {


    public static Context setAppLanguage(Context context){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences("com.example.qfilm", Context.MODE_PRIVATE);

        String language = sharedPreferences.getString("language", "English");

        if(language.equals("English")){

            return context;
        }

        Locale locale = new Locale(language.substring(0, 2));

        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = new Configuration(resources.getConfiguration());

        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);

    }


    public static void setTheme(Context context){

       SharedPreferences sharedPreferences =
               context.getSharedPreferences("com.example.qfilm", Context.MODE_PRIVATE);

       int theme = sharedPreferences.getInt("theme", R.style.Theme_Qfilm_AppTheme_Dark);

       context.setTheme(theme);

    }
}
