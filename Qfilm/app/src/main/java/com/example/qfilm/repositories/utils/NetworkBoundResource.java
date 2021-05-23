package com.example.qfilm.repositories.utils;


import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

/**
 *
 * This class is used to fetch data and while doing so choose between local and remote data
 * depending on certain conditions.
 *
 * The class has 4 abstract functions; saveCallResult, shouldFetch,
 * fetchDataFromDb and createCall, which are implemented in different fetch functions in 'MoviesRepository'.
 *
 *
 * This is according to android app architecture guide: https://developer.android.com/jetpack/guide
 **/


public abstract class NetworkBoundResource<LocalResultType, RemoteResultType> {

    // MediatorLiveData is used to observe other LiveData, in this case from local resource (Android Room)
    // and remote resource (TMDB API / OMDB API / FireStore).

    private MediatorLiveData<DataResource<LocalResultType>> results = new MediatorLiveData<>();

    // Provides executors to perform queries on background thread
    private AppExecutors appExecutors;

    public NetworkBoundResource(AppExecutors appExecutors){

        this.appExecutors = appExecutors;

        final LiveData<LocalResultType> localResult = fetchDataFromDb();

        results.addSource(localResult, new Observer<LocalResultType>() {
            @Override
            public void onChanged(LocalResultType result) {

                results.removeSource(localResult);

                if(shouldFetch(result)){

                    // if remote API call is needed the ui is notified with loading status
                    results.setValue(DataResource.loading(null));

                    fetchDataFromNetwork(localResult);

                }else{
                    results.addSource(localResult, new Observer<LocalResultType>() {
                        @Override
                        public void onChanged(LocalResultType newValue) {

                            if(results.getValue() != newValue){
                                results.setValue(DataResource.success(newValue));
                            }
                        }
                    });
                }
            }
        });
    }


    private void fetchDataFromNetwork(LiveData<LocalResultType> localResult) {

        /**
         *
         * fetches data from remote API.
         *
         * If successful ApiResponse: saves data in local database and re-fetch it from the local database
         * (According to single source of truth principle). then new data is set to 'results' with DataResource.success status.
         *
         * If error ApiResponse: old data is set to the results with DataResource.error status.
         *
         * **/

        final LiveData<ApiResponse<RemoteResultType>> apiResponse = createCall();

        results.addSource(apiResponse, new Observer<ApiResponse<RemoteResultType>>() {
            @Override
            public void onChanged(final ApiResponse<RemoteResultType> remoteResult) {

                results.removeSource(apiResponse);

                if (remoteResult instanceof ApiResponse.ApiSuccessResponse) {

                    appExecutors.getBackgroundThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            saveCallResult((RemoteResultType) ((ApiResponse.ApiSuccessResponse) remoteResult).getBody());


                            appExecutors.getMainThread().execute(new Runnable() {
                                @Override
                                public void run() {

                                    results.addSource(fetchDataFromDb(), new Observer<LocalResultType>() {
                                        @Override
                                        public void onChanged(LocalResultType newLocalResult) {

                                            results.setValue(DataResource.success(newLocalResult));

                                        }
                                    });
                                }
                            });
                        }

                    });

                } else if (remoteResult instanceof ApiResponse.ApiErrorResponse) {

                    final LiveData<LocalResultType> localResult = fetchDataFromDb();

                    results.addSource(localResult, new Observer<LocalResultType>() {
                        @Override
                        public void onChanged(LocalResultType localResult) {

                            results.setValue(DataResource.error(localResult, ((ApiResponse.ApiErrorResponse)remoteResult).getMessage()));

                        }
                    });

                }

            }
        });

    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RemoteResultType response);

    @MainThread
    protected abstract Boolean shouldFetch(@Nullable LocalResultType data);

    @MainThread
    protected abstract LiveData<LocalResultType> fetchDataFromDb();

    @MainThread
    protected abstract LiveData<ApiResponse<RemoteResultType>> createCall();


   public final LiveData<DataResource<LocalResultType>> getAsLiveData(){

       return results;
   }

}
