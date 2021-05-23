package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import utils.MyTestViewModelFactory;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;

import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class ResetPasswordFragmentTest {

    private static final String TAG = "ResetPasswordFragmentTe";

    // tested
    private ResetPasswordFragment resetPasswordFragment;

    // observable

   private MutableLiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> authOperationObservable;


    @Before
    public void setup() {

        authOperationObservable = new MutableLiveData<>();

        when(Mocks.firebaseAuthViewModelMock.getAuthOperation()).thenReturn(authOperationObservable);

        resetPasswordFragment = new ResetPasswordFragment(new MyTestViewModelFactory());

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(resetPasswordFragment);
                    }
                });
    }


    @Test
    public void testVisibility_headline(){

        onView(withText(R.string.tv_headline_reset_password)).check(matches(isDisplayed()));

    }



     @Test
    public void testSendResetEmail_EmptyEmailField()  {


        onView(withId(R.id.btn_send_email_reset_password)).perform(click());

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));


    }


    @Test
    public void testSendResetEmail_InvalidEmail() {


        onView(withId(R.id.et_email)).perform(ViewActions.typeText("Love"));

        onView(withId(R.id.btn_send_email_reset_password)).perform(click());

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_invalid_email_format)));


    }


    @Test
    public void testSendResetEmail_validEmail() {

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());


        // setup mock responses

       when(Mocks.firebaseAuthViewModelMock.sendPasswordResetEmail("eja_limpan@hotmail.com")).thenAnswer(
               new Answer<Object>() {
                   @Override
                   public Object answer(InvocationOnMock invocation) throws Throwable {

                       authOperationObservable.setValue(DataResource.success(FirebaseAuthViewModel.AuthOperation.EMAIL_PASSWORD_RESET));

                       return null;
                   }
               }
       );


        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.btn_send_email_reset_password)).perform(click());


        // verify snackBar

        onView(withText(R.string.snackbar_reset_password_email)).check(matches(isDisplayed()));


        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }


    @Test
    public void testSendResetEmail_noUserWithEmail() {

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.sendPasswordResetEmail("eja_limpan@hotmail.com")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.EMAIL_PASSWORD_RESET, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException()).thenReturn(new FirebaseAuthInvalidUserException("error", "error"));


        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.btn_send_email_reset_password)).perform(click());


        // verify errorText

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_user_no_exist)));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }


    @Test
    public void testSendResetEmail_OtherException() {

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.sendPasswordResetEmail("eja_limpan@hotmail.com")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.EMAIL_PASSWORD_RESET, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException()).thenReturn(new Exception("error"));

        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.btn_send_email_reset_password)).perform(click());

        // verify snackBar

        onView(withText(R.string.snackbar_unknown_error)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }

}
