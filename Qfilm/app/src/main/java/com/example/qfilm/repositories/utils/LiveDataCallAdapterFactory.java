package com.example.qfilm.repositories.utils;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;


/**
 * The default return values from Retrofit API is a "Call" object which contains the data.
 * This factory class is provided when creating the Retrofit instance so that it can create
 * the LiveDataCallAdapter.
 */

public class LiveDataCallAdapterFactory extends CallAdapter.Factory{

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        // makes sure that the returnType is LiveData

        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        // makes sure that the LiveData wrappes an ApiResponse

        Type apiResponseType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);

        Type rawApiResponseType = CallAdapter.Factory.getRawType(apiResponseType);


        if(rawApiResponseType != ApiResponse.class){
            throw new IllegalArgumentException("type must be ApiResponse");
        }

        // makes sure that ApiResponse is parameterized so that it has value/data

        if(!(apiResponseType instanceof ParameterizedType)){
            throw new IllegalArgumentException("type must be parameterized");

        }

        // finally creates the LiveDataCallAdapter

        Type responseType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) apiResponseType);

        return new LiveDataCallAdapter<Type>(responseType);

    }
}
