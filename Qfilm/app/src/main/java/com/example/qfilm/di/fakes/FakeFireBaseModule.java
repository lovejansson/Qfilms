package com.example.qfilm.di.fakes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.functions.FirebaseFunctions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import utils.Mocks;

import static utils.Mocks.firebaseAuthMock;
import static utils.Mocks.firestoreMock;

@Module
public class FakeFireBaseModule {

    @Provides
    public static FirebaseAuth provideFireBaseAuth(){

        return Mocks.firebaseAuthMock;
    }


    @Provides
    public static FirebaseUser provideFireBaseUser(){
        return Mocks.firebaseUserMock;
    }


    @Provides
    public static FirebaseFirestore provideFirebaseFirestore(){

        return Mocks.firestoreMock;
    }

    @Provides
    @Singleton
    public static FirebaseFunctions provideFirebaseFunctions(){
        return Mocks.firebaseFunctionsMock;
    }
}
