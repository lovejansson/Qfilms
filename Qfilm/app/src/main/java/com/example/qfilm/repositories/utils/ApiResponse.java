package com.example.qfilm.repositories.utils;

import java.io.IOException;
import retrofit2.Response;


/**  wrapper class for remote resource responses, according to
 android app architecture guide: https://developer.android.com/jetpack/guide  **/

public class ApiResponse<T> {

    public ApiResponse<T> make(Throwable error){

        if(error.getMessage() == null || error.getMessage().isEmpty()){

            return new ApiErrorResponse<>("Unkown error, check your internet connection");
        }
        return new ApiErrorResponse<>(error.getMessage());
    }

    public ApiResponse<T> make(Response<T> data){

        if(data.isSuccessful()) {

            return new ApiSuccessResponse<>(data.body());

        }
        else{
            String msg = "";
            try {
                msg = data.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ApiErrorResponse<>(msg);
        }
    }


    public ApiResponse<T> success(T data){

        return new ApiSuccessResponse<>(data);

    }


    public ApiResponse<T> error(Exception exception){

        return new ApiErrorResponse<>(exception.getMessage());
    }

    public class ApiSuccessResponse<T> extends ApiResponse<T>{

        private T body;

        ApiSuccessResponse(T body){
            this.body = body;

        }

        public T getBody(){
            return this.body;
        }
    }

    public class ApiErrorResponse<T> extends ApiResponse<T>{

        private String message;

        ApiErrorResponse(String message){
            this.message = message;

        }

        public String getMessage(){
            return this.message;
        }
    }

}
