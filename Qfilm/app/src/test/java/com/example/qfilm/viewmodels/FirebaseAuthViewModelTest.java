package com.example.qfilm.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.qfilm.repositories.utils.DataResource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import utils.Mocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseAuthViewModelTest {

    // tested
    private FirebaseAuthViewModel firebaseAuthViewModel;

    /**
    A JUnit Test Rule that swaps the background executor used by the Architecture Components
    with a different one which executes each task synchronously. Used because you can't invoke
    observeForever on a background thread
     **/

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();


    // observer and observable

    private Observer observer;
    private LiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> authOperation;


    @Before
    public void setup() {

        firebaseAuthViewModel  = new FirebaseAuthViewModel(Mocks.firebaseAuthMock);

        observer = Mockito.mock(Observer.class);

        authOperation = firebaseAuthViewModel.getAuthOperation();

        authOperation.observeForever(observer);

        when(Mocks.firebaseAuthMock.getCurrentUser()).thenReturn(Mocks.firebaseUserMock);

        when(Mocks.taskMock.addOnCompleteListener(any(OnCompleteListener.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                ((OnCompleteListener)invocation.getArgument(0)).onComplete(Mocks.taskMock);
                return null;
            }
        });


    }


    @Test
    public void signInWithCredential_success(){

        // setup

        when(Mocks.firebaseAuthMock.signInWithCredential(any(AuthCredential.class))).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act

        firebaseAuthViewModel.signInWithCredential(Mockito.mock(AuthCredential.class));

        // verify
        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.SIGN_IN));


    }


    @Test
    public void signInWithCredential_error(){

        // setup

        when(Mocks.firebaseAuthMock.signInWithCredential(any(AuthCredential.class))).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(false);


        // act

        firebaseAuthViewModel.signInWithCredential(Mockito.mock(AuthCredential.class));

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));


    }


    @Test
    public void signInWithEmail_success(){

        // setup

        when(Mocks.firebaseUserMock.isEmailVerified()).thenReturn(true);
        when(Mocks.firebaseAuthMock.signInWithEmailAndPassword("test@hotmail.com", "test123")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act
        firebaseAuthViewModel.signInWithEmail("test@hotmail.com", "test123");

        // verify
        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.SIGN_IN));


    }


    @Test
    public void signInWithEmail_successButEmailNotVerified(){

        // setup

        when(Mocks.firebaseUserMock.isEmailVerified()).thenReturn(false);
        when(Mocks.firebaseAuthMock.signInWithEmailAndPassword("test@hotmail.com", "test123")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act

        firebaseAuthViewModel.signInWithEmail("test@hotmail.com", "test123");

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

        assertEquals(firebaseAuthViewModel.getFirebaseAuthException().getClass(), FirebaseAuthViewModel.EmailNotVerified.class);

    }


    @Test
    public void signInWithEmail_error(){

        // setup

        when(Mocks.firebaseAuthMock.signInWithEmailAndPassword("test@hotmail.com", "test123")).thenReturn(Mocks.taskMock);

        when(Mocks.taskMock.isSuccessful()).thenReturn(false);

        when(Mocks.taskMock.getException()).thenReturn(new FirebaseAuthInvalidCredentialsException(anyString(), anyString()));



        // act

        firebaseAuthViewModel.signInWithEmail("test@hotmail.com", "test123");

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

        assertEquals(firebaseAuthViewModel.getFirebaseAuthException().getClass(), FirebaseAuthInvalidCredentialsException.class);

    }


    @Test
    public void sendVerificationEmail_success(){

        // setup

        when(Mocks.firebaseUserMock.sendEmailVerification()).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act

        firebaseAuthViewModel.sendVerificationEmail();

        // verify
        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION));


    }


    @Test
    public void sendVerificationEmail_error(){

        // setup

        when(Mocks.firebaseUserMock.sendEmailVerification()).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(false);


        // act

        firebaseAuthViewModel.sendVerificationEmail();

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION, null));


    }

    @Test
    public void sendPasswordResetEmail_success(){

        // setup

        when(Mocks.firebaseAuthMock.sendPasswordResetEmail("test@hotmail.com")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act

        firebaseAuthViewModel.sendPasswordResetEmail("test@hotmail.com");

        // verify
        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.EMAIL_PASSWORD_RESET));


    }


    @Test
    public void sendPasswordResetEmail_error(){

        // setup

        when(Mocks.firebaseAuthMock.sendPasswordResetEmail("test@hotmail.com")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(false);


        // act

        firebaseAuthViewModel.sendPasswordResetEmail("test@hotmail.com");

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.EMAIL_PASSWORD_RESET, null));


    }

    @Test
    public void createAccount_success(){

        // setup

        when(Mocks.firebaseAuthMock.createUserWithEmailAndPassword("test@hotmail.com", "test123")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);


        // act

        firebaseAuthViewModel.createAccount("test@hotmail.com", "test123");

        // verify
        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT));


    }


    @Test
    public void createAccount_error(){

        // setup

        when(Mocks.firebaseAuthMock.createUserWithEmailAndPassword("test@hotmail.com", "test123")).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(false);
        when(Mocks.taskMock.getException()).thenReturn(new FirebaseAuthUserCollisionException(anyString(), anyString()));


        // act

        firebaseAuthViewModel.createAccount("test@hotmail.com", "test123");

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT, null));
        assertEquals(FirebaseAuthUserCollisionException.class, firebaseAuthViewModel.getFirebaseAuthException().getClass());


    }


    @Test
    public void updateUsername_success(){

        // setup

        when(Mocks.firebaseUserMock.updateProfile(any(UserProfileChangeRequest.class))).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(true);

        // act

        firebaseAuthViewModel.upDateUsername("new username");

        // verify

        verify(observer).onChanged(DataResource.success(FirebaseAuthViewModel.AuthOperation.UPDATE_USERNAME));
    }


    @Test
    public void updateUsername_error(){

        // setup

        when(Mocks.firebaseUserMock.updateProfile(any(UserProfileChangeRequest.class))).thenReturn(Mocks.taskMock);
        when(Mocks.taskMock.isSuccessful()).thenReturn(false);


        // act

        firebaseAuthViewModel.upDateUsername("new username");

        // verify
        verify(observer).onChanged(DataResource.error(FirebaseAuthViewModel.AuthOperation.UPDATE_USERNAME, null));


    }

}
