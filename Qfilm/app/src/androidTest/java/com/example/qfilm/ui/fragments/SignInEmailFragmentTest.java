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

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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

public class SignInEmailFragmentTest {

    // tested
    private SignInEmailFragment signInEmailFragment;

    // observable
    private MutableLiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> authOperationObservable;


    @Before
    public void setup() {

        authOperationObservable = new MutableLiveData<>();

        when(Mocks.firebaseAuthViewModelMock.getAuthOperation()).thenReturn(authOperationObservable);

        signInEmailFragment = new SignInEmailFragment(new MyTestViewModelFactory());

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(signInEmailFragment);
                    }
                });
    }


    @Test
    public void testVisibility_headingAndDividerLine(){

        onView(withText(R.string.tv_heading_sign_in)).check(matches(isDisplayed()));
        onView(withId(R.id.divider_line_two)).check(matches(isDisplayed()));

    }


    @Test
    public void testLogin_EmptyFields()  {


        onView(withId(R.id.btn_sign_in)).perform(click());

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));

        onView(withId(R.id.layout_et_password)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));

    }


    @Test
    public void testLogin_invalidPassword()  {

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("Love"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        onView(withId(R.id.layout_et_password)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_password_length_requirement)));

    }


    @Test
    public void testLogin_invalidEmail()  {

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpanhotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("Love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_invalid_email_format)));

    }


    @Test
    public void testLogin_validInput_butWrongPassword(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.signInWithEmail("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException())
                .thenReturn(new FirebaseAuthInvalidCredentialsException("error", "error"));

        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        // verify

        onView(withId(R.id.layout_et_password)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_wrong_password)));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());


    }


    @Test
    public void testLogin_validInput_butNoSuchUserExists(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.signInWithEmail("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException())
                .thenReturn(new FirebaseAuthInvalidUserException("error", "error"));

        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        // verify

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_user_no_exist)));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());


    }


    @Test
    public void testLogin_validInput_butEmailIsNotVerified_resendSuccess(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.signInWithEmail("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException())
                .thenReturn(new FirebaseAuthViewModel.EmailNotVerified("error"));


        when(Mocks.firebaseAuthViewModelMock.sendVerificationEmail()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.success(FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION));

                        return null;
                    }
                }
        );



        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        // verify

        onView(withText(R.string.email_address_not_verified)).check(matches(isDisplayed()));


        // resend email verification

        onView(withText(R.string.snackBar_action_resend)).perform(click());

        // verify new snackbar is showing

        onView(withText(R.string.email_verification_sent)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }


    @Test
    public void testLogin_validInput_butEmailIsNotVerified_resendError(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.signInWithEmail("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException())
                .thenReturn(new FirebaseAuthViewModel.EmailNotVerified("error"));


        when(Mocks.firebaseAuthViewModelMock.sendVerificationEmail()).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION, null));

                        return null;
                    }
                }
        );



        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        // verify

        onView(withText(R.string.email_address_not_verified)).check(matches(isDisplayed()));


        // resend email verification

        onView(withText(R.string.snackBar_action_resend)).perform(click());

        // verify new snackbar is showing

        onView(withText(R.string.snackbar_unknown_error)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }



    @Test
    public void testLogin_validInput_butOtherException(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.signInWithEmail("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.SIGN_IN, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException())
                .thenReturn(new Exception("error"));
        // act

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        // verify

        onView(withText(R.string.snackbar_unknown_error)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }

}
