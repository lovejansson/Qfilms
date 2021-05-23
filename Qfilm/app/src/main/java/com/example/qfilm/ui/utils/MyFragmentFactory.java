package com.example.qfilm.ui.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.Video;
import com.example.qfilm.ui.fragments.CollectionFragment;
import com.example.qfilm.ui.fragments.CollectionsFragment;
import com.example.qfilm.ui.fragments.CreateAccountFragment;
import com.example.qfilm.ui.fragments.DetailPageFragment;
import com.example.qfilm.ui.fragments.HomeFragment;
import com.example.qfilm.ui.fragments.ListingFragment;
import com.example.qfilm.ui.fragments.ProfileFragment;
import com.example.qfilm.ui.fragments.ResetPasswordFragment;
import com.example.qfilm.ui.fragments.SearchFragment;
import com.example.qfilm.ui.fragments.SignInEmailFragment;
import com.example.qfilm.ui.fragments.SignInFragment;
import com.example.qfilm.ui.fragments.TrailerFragment;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentGenres;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

/**
 *
 * creates all fragments, used to be able to pass dependencies to the constructor of fragment
 *
 * **/

public class MyFragmentFactory extends FragmentFactory {

    private static final String TAG = "MyFragmentFactory";

    private Video video;

    private FirebaseAuth firebaseAuth;

    private MediaType mediaType;

    private Genre genre;

    private Collection collection;

    private MyViewModelFactory myViewModelFactory;

    private Result result;


    @Inject
    public MyFragmentFactory(FirebaseAuth firebaseAuth, MyViewModelFactory myViewModelFactory) {
        this.firebaseAuth = firebaseAuth;
        this.myViewModelFactory = myViewModelFactory;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {

        Fragment fragment;

        Log.d(TAG, "instantiate: my factory");

        switch (className){

            case "HomeFragment":
                fragment = new HomeFragment(new ListingFragment(MediaType.MOVIE, myViewModelFactory), new ListingFragment(MediaType.SERIES, myViewModelFactory));
                break;

            case "ProfileFragment":
                fragment = new ProfileFragment(firebaseAuth, new CollectionsFragment(myViewModelFactory));
                break;

            case "ListingFragment":
                fragment = new ListingFragment(mediaType, myViewModelFactory);
                break;

            case "CollectionsFragment":
                fragment = new CollectionsFragment(myViewModelFactory);
                break;

            case "CollectionFragment":
                fragment = new CollectionFragment(collection, myViewModelFactory);
                break;

            case "SignInFragment":
                fragment = new SignInFragment(myViewModelFactory);
                break;

            case "ResetPasswordFragment":
                fragment = new ResetPasswordFragment(myViewModelFactory);
                break;

            case "TrailerFragment":
                fragment = new TrailerFragment(video);
                break;

            case "SignInEmailFragment":
                fragment = new SignInEmailFragment(myViewModelFactory);
                break;

            case "DetailPageFragment":
                fragment = new DetailPageFragment(result, firebaseAuth, myViewModelFactory);
                break;

            case "CreateAccountFragment":
                fragment = new CreateAccountFragment(myViewModelFactory);
                break;

            case "DialogFragmentGenres":
                fragment = new DialogFragmentGenres(genre, mediaType, myViewModelFactory);
                break;

            case "SearchFragment":
                fragment = new SearchFragment(myViewModelFactory);
                break;

            default:
                fragment =  super.instantiate(classLoader, className);
                break;

        }

        return fragment;

    }
}
