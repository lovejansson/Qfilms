package com.example.qfilm.utils;

import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    private static final String TAG = "Constants";

    public static String TMDB_API_KEY = "2cb1658a1165dde7479be49601eb1658";

    public static String OMDB_API_KEY ="b422dfb1";

    public static String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

    public static String OMDB_BASE_URL = "https://www.omdbapi.com/";

    public static String ALGOLIA_API_KEY = "3471f8ddff29f8f1c55c29f2431faab4";

    public static String ALGOLIA_APP_ID = "F2JPNKMMX3";


    public static List<Integer> GENRES_TO_EXCLUDE =  new ArrayList<>(Arrays.asList(10766, 10763, 9648,
            10751, 10762, 10767, 10768, 37, 10770, 10752));

    public static String ROOM_DATABASE_NAME = "movies";

    public static int TIME_LIMIT_DETAILS = 60 * 60 * 24 * 2;

    public static int TIME_LIMIT_RESULT = 60 * 60 * 24 * 2;

    public static int TIME_LIMIT_GENRE = 60 * 60 * 24 * 100; // genres will probably not change that much

    public static int PAGE_SIZE = 20;

    public static void initializeConstants() throws JSONException {

        FirebaseRemoteConfig firebaseRemoteConfig = null;
        try {
         firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        }
        catch(Exception e){
            Log.e(TAG, "Constants: " + e.getMessage());
        }

            
        if(firebaseRemoteConfig != null && firebaseRemoteConfig.getAll().size()  > 0){

            TMDB_BASE_URL = firebaseRemoteConfig.getString("base_url_tmdb");
            GENRES_TO_EXCLUDE = convertJsonToIntegerList(firebaseRemoteConfig.getString("genres_to_exclude"));
            TIME_LIMIT_RESULT = (int) firebaseRemoteConfig.getLong("time_limit_result");
            TIME_LIMIT_GENRE = (int) firebaseRemoteConfig.getLong("time_limit_genre");
            TIME_LIMIT_DETAILS = (int) firebaseRemoteConfig.getLong("time_limit_details");

        }

        TMDB_API_KEY = "2cb1658a1165dde7479be49601eb1658";

    }


    private static List<Integer> convertJsonToIntegerList(String genreString) throws JSONException {

        JSONArray jsonArray = new JSONArray(genreString);

        List<Integer> ids = new ArrayList<>();

        if (jsonArray != null) {

            for (int i=0; i<jsonArray.length(); i++){

                ids.add(jsonArray.getInt(i));
            }
        }

        return ids;
    }


}
