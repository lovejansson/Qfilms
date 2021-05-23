package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import com.example.qfilm.R;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;

import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import utils.MyTestViewModelFactory;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class HomeFragmentTest {



    private MutableLiveData<DataResource<List<Result>>> observableResults;


    @Before
    public void setUp(){

        setupMocks();

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(new HomeFragment(

                                new ListingFragment(MediaType.MOVIE, new MyTestViewModelFactory()),
                                new ListingFragment(MediaType.SERIES, new MyTestViewModelFactory())));
                    }
                });
    }


    private void setupMocks() {

        List<Result> results = new ArrayList<>();

        for(int i = 0; i < 10; ++i){

            Result result = new Result();
            result.setTitle(String.valueOf(i));
            result.setPopularity((double) i);
            result.setResultId(i);
            results.add(result);
        }

        observableResults = new MutableLiveData<>();

        when(Mocks.resultWithGenreViewModelMock
                .fetchResultsWithGenre(any(MediaType.class), any(Genre.class), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableResults.setValue(DataResource.success(results));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.getResultsObservable()).thenReturn(observableResults);

    }


    @After
    public void tearDown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();

    }


    @Test
    public void TestVisibilities() {

        // verify

        onView(withId(R.id.container_tab_fragment)).check(matches(isDisplayed()));

        onView(withId(R.id.tabs)).check(matches(isDisplayed()));

        Context context = ApplicationProvider.getApplicationContext();

        onView(withId(R.id.tabs)).check(matches(CustomMatchers.labelsInTabLayoutMatcher(
                Arrays.asList(context.getString(R.string.tab_movies), context.getString(R.string.tab_series)))));

        onView(withId(R.id.fragment_listing)).check(matches(isDisplayed()));

    }

}