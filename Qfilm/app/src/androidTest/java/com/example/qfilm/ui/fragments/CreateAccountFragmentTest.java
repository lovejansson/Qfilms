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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class CreateAccountFragmentTest {

    // tested
    private CreateAccountFragment createAccountFragment;

    // observable
    private MutableLiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> authOperationObservable;


    @Before
    public void setUp(){

        authOperationObservable = new MutableLiveData<>();

        when(Mocks.firebaseAuthViewModelMock.getAuthOperation()).thenReturn(authOperationObservable);

        createAccountFragment =  new CreateAccountFragment(new MyTestViewModelFactory());

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(createAccountFragment);
                    }
                });
    }


    @Test
    public void testVisibility_heading(){

        onView(withText(R.string.tv_heading_create_account)).check(matches(isDisplayed()));

    }


    @Test
    public void testCreateAccount_EmptyFields()  {

        onView(withId(R.id.btn_sign_up)).perform(click());

        onView(withId(R.id.layout_et_username)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));

        onView(withId(R.id.layout_et_password)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_required_field)));

    }


    @Test
    public void testCreateAccount_invalidPassword()  {

        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("Love"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        onView(withId(R.id.layout_et_password)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_password_length_requirement)));

    }


    @Test
    public void testCreateAccount_invalidEmail()  {

        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpanhotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("Love123"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_invalid_email_format)));

    }


    @Test
    public void testCreateAccount_validInput_butUserWithThatEmailAlreadyExists(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.createAccount("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException()).thenReturn(new FirebaseAuthUserCollisionException("error", "error"));


        // act
        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        // verify

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.account_with_email_already_exist)));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());


    }


    @Test
    public void testCreateAccount_validInput_InvalidEmailException(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.createAccount("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException()).thenReturn(new FirebaseAuthInvalidCredentialsException("error", "error"));

        // act
        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        // verify

        onView(withId(R.id.layout_et_email)).check(matches(CustomMatchers.textInputLayoutErrorTextMatcher(R.string.input_invalid_email_format)));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());


    }

    @Test
    public void testLogin_validInput_butOtherException(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.createAccount("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.error(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT, null));

                        return null;
                    }
                }
        );

        when(Mocks.firebaseAuthViewModelMock.getFirebaseAuthException()).thenReturn(new Exception("error"));

        // act
        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        // verify

        onView(withText(R.string.snackbar_unknown_error)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }

    @Test
    public void testLogin_validInput_success(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());


        // setup mock responses

        when(Mocks.firebaseAuthViewModelMock.createAccount("eja_limpan@hotmail.com", "love123")).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        authOperationObservable.setValue(DataResource.success(FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT));

                        return null;
                    }
                }
        );

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
        onView(withId(R.id.et_username)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.et_email)).perform(ViewActions.typeText("eja_limpan@hotmail.com"));

        onView(withId(R.id.et_password)).perform(ViewActions.typeText("love123"));

        onView(withId(R.id.fragment_create_account)).perform(closeSoftKeyboard());

        onView(withId(R.id.btn_sign_up)).perform(click());

        // verify

        onView(withText(R.string.email_verification_sent)).check(matches(isDisplayed()));

        Mocks.reset();

        MyIdlingResource.reset();

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

    }

}
