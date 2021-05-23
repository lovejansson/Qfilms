package com.example.qfilm.repositories.utils;

import androidx.lifecycle.LiveData;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The default return values from Retrofit API is a "Call" object which contains the data.
 * This class is used to instead wrap the data in LiveData.
 */

public class LiveDataCallAdapter<T> implements CallAdapter<T, LiveData<ApiResponse<T>>> {

    private Type responseType;

    public LiveDataCallAdapter(Type responseType){
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<T>> adapt(final Call<T> call) {

        // convert and return Call object to LiveData with a ApiResponse object
        return new LiveData<ApiResponse<T>>() {
            @Override
            protected void onActive() {

                super.onActive();

                final ApiResponse apiResponse = new ApiResponse();

                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {

                        postValue(apiResponse.make(response));

                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {

                        postValue(apiResponse.make(t));

                    }
                });
            }
        };
    }
}
