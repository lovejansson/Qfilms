package com.example.qfilm.repositories.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *  wrapper class for providing more information about a request for data, according to
 *  android app architecture guide: https://developer.android.com/jetpack/guide
 *
 *  It has 3 static methods for creating a DataResource: success, error and loading.
 *
 **/

public class DataResource<T> {

    @NonNull private final Status status;
    @Nullable private final T data;
    @Nullable private final String message;

    public enum Status{
        SUCCESS, ERROR, LOADING
    }

    private DataResource(@NonNull Status status, @Nullable T data, @Nullable String message){
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DataResource<T> success(@NonNull T data){
        return new DataResource<>(Status.SUCCESS, data, null);
    }

    public static <T> DataResource<T> error(@Nullable T data, @Nullable String message){
        return new DataResource<>(Status.ERROR, data, message);
    }

    public static <T> DataResource<T> loading(@Nullable T data){

        return new DataResource<>(Status.LOADING, data, null);

    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    // Override this to compare DataResource objects in unit tests.
    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == null) {

            return false;
        }
        else if(obj.getClass() != getClass()){
            return false;
        }
        else{

            DataResource other = (DataResource) obj;

            boolean result = false;

            if(status == Status.SUCCESS) {

                result = status.equals(other.getStatus()) &&
                        data.equals(other.getData());
            }

            else if(status == Status.ERROR) {

                if(data == null){
                    if(message == null){
                        result = status.equals(other.getStatus());
                    }else{
                        result = status.equals(other.getStatus()) && message.equals(other.getMessage());
                    }

                }else if(message == null){
                    result = status.equals(other.getStatus()) && data.equals(other.getData());
                }
                else{

                    result = status.equals(other.getStatus()) && data.equals(other.getData()) &&
                    message.equals(other.getMessage());
                }

            }

            else if(status == Status.LOADING){

                if(data == null){

                    result = status.equals(other.getStatus()) && other.getData() == null;
                }else{

                    result = status.equals(other.getStatus()) && data.equals(other.getData());
                }

            }

            return result;

        }

    }
}
