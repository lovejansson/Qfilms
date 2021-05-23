package utils;

import android.util.Log;

import androidx.test.espresso.idling.CountingIdlingResource;

public class MyIdlingResource {

    private static CountingIdlingResource countingIdlingResource = null;

    private static final String TAG = "MyIdlingResource";
    
    public static CountingIdlingResource getInstance(){


        if(countingIdlingResource == null){

            countingIdlingResource = new CountingIdlingResource("woop");

        }

        return countingIdlingResource;


    }


    public static void increment(){

        if(countingIdlingResource != null){

            countingIdlingResource.increment();
        }

    }

    public static void decrement(){

        if(countingIdlingResource != null && !countingIdlingResource.isIdleNow()){

            countingIdlingResource.decrement();

        }

    }

    public static void reset(){

        countingIdlingResource = new CountingIdlingResource("woop");
    }
}
