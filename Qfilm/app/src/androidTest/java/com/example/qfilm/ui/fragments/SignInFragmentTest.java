package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;

import utils.Mocks;
import utils.MyTestViewModelFactory;


import org.junit.Before;
import org.junit.Test;
import qfilm.TestFragmentActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class SignInFragmentTest {

    // tested
    private SignInFragment signInFragment = new SignInFragment(new MyTestViewModelFactory());


    @Before
    public void setUp(){
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(signInFragment);
                    }
                });

        MutableLiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> observable = new MutableLiveData<>();

        when(Mocks.firebaseAuthViewModelMock.getAuthOperation()).thenReturn(observable);
    }



    @Test
    public void TestVisibilities() {

        // verify

        onView(withId(R.id.btn_settings)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.ic_settings)));

        onView(withText(R.string.tv_heading_sign_in)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_sign_in_email)).check(matches(withText(R.string.btn_sign_in_email)));

        onView(withId(R.id.btn_sign_in_google)).check(matches(withText(R.string.btn_sign_in_google)));

        onView(withId(R.id.btn_sign_in_facebook)).check(matches(withText(R.string.btn_sign_in_facebook)));


    }

}
