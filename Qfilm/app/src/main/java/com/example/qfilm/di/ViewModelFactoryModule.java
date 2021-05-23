package com.example.qfilm.di;


import com.algolia.search.saas.Client;
import com.example.qfilm.repositories.MoviesRepository;
import com.example.qfilm.utils.Constants;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelFactoryModule {

    @Provides
    @Singleton
    public static MyViewModelFactory provideViewModelFactory(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore,
                                                             FirebaseFunctions firebaseFunctions, MoviesRepository moviesRepository){

            Client client = new Client(Constants.ALGOLIA_APP_ID, Constants.ALGOLIA_API_KEY);

            return new MyViewModelFactory(firebaseAuth, firebaseFirestore, firebaseFunctions, moviesRepository, client);

    }

}
