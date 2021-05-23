package com.example.qfilm.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class FireBaseModule {


    @Provides
    @Singleton
    public static FirebaseAuth provideFireBaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFireStore(){
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseFunctions provideFirebaseFunctions(){
        return FirebaseFunctions.getInstance("europe-west2");
    }
}
