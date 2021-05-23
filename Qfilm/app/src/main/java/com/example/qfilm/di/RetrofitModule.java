package com.example.qfilm.di;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.qfilm.data.sources.remote.OmdbInterface;
import com.example.qfilm.data.sources.remote.TmdbInterface;
import com.example.qfilm.utils.Constants;
import com.example.qfilm.repositories.utils.LiveDataCallAdapterFactory;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class RetrofitModule {


    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        return logging;

    }


    @Provides
    @Singleton
    @Named("tmdb")
    public static Interceptor provideApiKeyInterceptorTmdb(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                HttpUrl oldUrl = oldRequest.url();

                // updating url
                HttpUrl.Builder urlBuilder = oldUrl.newBuilder();
                HttpUrl newUrl = urlBuilder.addQueryParameter("api_key", Constants.TMDB_API_KEY).build();

                //updating request
                Request.Builder requestBuilder = oldRequest.newBuilder();
                Request newRequest = requestBuilder.url(newUrl).build();

                return chain.proceed(newRequest);
            }};
    }


    @Provides @Named("omdb")
    @Singleton
    public static Interceptor provideApiKeyInterceptorOmdb(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                HttpUrl oldUrl = oldRequest.url();

                // updating url
                HttpUrl.Builder urlBuilder = oldUrl.newBuilder();
                HttpUrl newUrl = urlBuilder.addQueryParameter("apikey", Constants.OMDB_API_KEY).build();

                //updating request
                Request.Builder requestBuilder = oldRequest.newBuilder();
                Request newRequest = requestBuilder.url(newUrl).build();

                return chain.proceed(newRequest);
            }};
    }


    @Provides
    @Named("tmdb")
    @Singleton
    public static OkHttpClient provideOkHttpClientTmdb(HttpLoggingInterceptor httpLoggingInterceptor, @Named("tmdb") Interceptor apiKeyInterceptor){
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }


    @Provides
    @Named("omdb")
    @Singleton
    public static OkHttpClient provideOkHttpClientOmdb(HttpLoggingInterceptor httpLoggingInterceptor, @Named("omdb") Interceptor apiKeyInterceptor){
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }




    @Provides
    @Named("tmdb")
    @Singleton
    public static Retrofit provideRetrofitTmdb(@Named("tmdb") OkHttpClient okHttpClient){

        return new Retrofit.Builder()
                .baseUrl(Constants.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Java objects with Gson
                .addCallAdapterFactory(new LiveDataCallAdapterFactory()) // Convert Call Object with data to LiveData<ApiResponse<T>>
                .client(okHttpClient)
                .build();

    }


    @Provides
    @Named("omdb")
    @Singleton
    public static Retrofit provideRetrofitOmdb(@Named("omdb") OkHttpClient okHttpClient){

        return new Retrofit.Builder()
                .baseUrl(Constants.OMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Java objects with Gson
                .addCallAdapterFactory(new LiveDataCallAdapterFactory()) // Convert Call Object with data to LiveData<ApiResponse<T>>
                .client(okHttpClient)
                .build();

    }


    @Provides
    @Singleton
    public static TmdbInterface provideTmdbInterface(@Named("tmdb") Retrofit retrofit){
        return retrofit.create(TmdbInterface.class);
    }


    @Provides
    @Singleton
    public static OmdbInterface provideOmdbInterface(@Named("omdb") Retrofit retrofit){
        return retrofit.create(OmdbInterface.class);
    }

}
