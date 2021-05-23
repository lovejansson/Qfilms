package com.example.qfilm.viewmodels.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.algolia.search.saas.Client;
import com.example.qfilm.repositories.MoviesRepository;
import com.example.qfilm.viewmodels.AlgoliaSearchViewModel;
import com.example.qfilm.viewmodels.DetailsViewModel;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.ResultWithGenreViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;


/**
 *
 * creating viewModels to be able to provide dependencies through constructor
 *
 * **/

public class MyViewModelFactory implements ViewModelProvider.Factory {


    private FirebaseFirestore firebaseFirestore;

    private MoviesRepository moviesRepository;

    private FirebaseAuth firebaseAuth;

    private Client client;

    private FirebaseFunctions firebaseFunctions;


    public MyViewModelFactory(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore, FirebaseFunctions firebaseFunctions,
                              MoviesRepository moviesRepository, Client client) {

        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.firebaseFunctions = firebaseFunctions;
        this.moviesRepository = moviesRepository;
        this.client = client;
    }

    public MyViewModelFactory() {
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(FireStoreViewModel.class)){

            return (T) new FireStoreViewModel(firebaseAuth, firebaseFirestore, firebaseFunctions);

        }else if(modelClass.isAssignableFrom(DetailsViewModel.class)){

            return (T) new DetailsViewModel(moviesRepository);

        }else if(modelClass.isAssignableFrom(ResultWithGenreViewModel.class)){

            return (T) new ResultWithGenreViewModel(moviesRepository);

        }else if(modelClass.isAssignableFrom(FirebaseAuthViewModel.class)){

           return (T) new FirebaseAuthViewModel(firebaseAuth);


        }else if(modelClass.isAssignableFrom(AlgoliaSearchViewModel.class)){

            return (T) new AlgoliaSearchViewModel(client);
        }

        throw new IllegalArgumentException("ViewModel not found");
    }

}
