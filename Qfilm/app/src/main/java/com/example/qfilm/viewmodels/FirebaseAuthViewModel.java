package com.example.qfilm.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qfilm.repositories.utils.DataResource;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.UserProfileChangeRequest;


public class FirebaseAuthViewModel extends ViewModel {

    private static final String TAG = "FirebaseAuthViewModel";

    private FirebaseAuth firebaseAuth;

    private MutableLiveData<DataResource<AuthOperation>> authOperation;

    public Exception getFirebaseAuthException() {
        return firebaseAuthException;
    }

    private Exception firebaseAuthException;

    public enum AuthOperation{
        SIGN_IN,
        CREATE_ACCOUNT,
        SIGN_OUT,
        EMAIL_VERIFICATION,
        EMAIL_PASSWORD_RESET,
        UPDATE_USERNAME
    }


    public FirebaseAuthViewModel(FirebaseAuth firebaseAuth){

        this.firebaseAuth = firebaseAuth;
        authOperation = new MutableLiveData<>();

    }


    public MutableLiveData<DataResource<AuthOperation>> getAuthOperation() {
        return authOperation;
    }


    public void signInWithCredential(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            authOperation.setValue(DataResource.success(AuthOperation.SIGN_IN));

                        } else {

                            authOperation.setValue(DataResource.error(AuthOperation.SIGN_IN, null));

                        }
                    }
                });
    }


    public Void signInWithEmail(String email, String password){


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(!firebaseAuth.getCurrentUser().isEmailVerified()){

                                firebaseAuthException = new EmailNotVerified(email);

                                authOperation.setValue(DataResource.error(AuthOperation.SIGN_IN, null));
                            }else{

                                authOperation.setValue(DataResource.success(AuthOperation.SIGN_IN));
                            }


                        } else {

                            firebaseAuthException = task.getException();

                            authOperation.setValue(DataResource.error(AuthOperation.SIGN_IN, null));
                        }

                    }
                });

        return null;
    }


    public Void sendVerificationEmail() {

        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            authOperation.setValue(DataResource.success(AuthOperation.EMAIL_VERIFICATION));


                        }else{

                            authOperation.setValue(DataResource.error(AuthOperation.EMAIL_VERIFICATION, null));
                        }
                    }
                });

        return null;
    }


    public Void sendPasswordResetEmail(String email){

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            authOperation.setValue(DataResource.success(AuthOperation.EMAIL_PASSWORD_RESET));


                        }else{

                            authOperation.setValue(DataResource.error(AuthOperation.EMAIL_PASSWORD_RESET, null));

                        }

                    }
                });

        return null;
    }


    public Void createAccount(String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            authOperation.setValue(DataResource.success(AuthOperation.CREATE_ACCOUNT));


                        } else {

                            firebaseAuthException = task.getException();

                            authOperation.setValue(DataResource.error(AuthOperation.CREATE_ACCOUNT, null));

                        }

                    }


                });

        return null;
    }


    public void upDateUsername(String username) {

        if(firebaseAuth.getCurrentUser() != null) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            firebaseAuth.getCurrentUser().updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                authOperation.setValue(DataResource.success(AuthOperation.UPDATE_USERNAME));
                            }else{

                                authOperation.setValue(DataResource.error(AuthOperation.UPDATE_USERNAME, null));
                            }

                        }
                    });
        }
    }


    public static class EmailNotVerified extends Exception {
        public EmailNotVerified(String errorMessage) {
            super(errorMessage);
        }
    }

}
