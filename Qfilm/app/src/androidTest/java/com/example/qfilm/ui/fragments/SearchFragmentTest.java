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
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;

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
import utils.MyTestViewModelFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.RESULT_ONE;

public class SearchFragmentTest {

    // tested
    SearchFragment searchFragment;

    // observable to return when calling getObservable methods from viewModel
    private MutableLiveData<DataResource<List<Result>>> observableSearchResults;

    // return value when search()
    private List<Result> searchResults;


    @Before
    public void setup(){

        searchFragment = new SearchFragment(new MyTestViewModelFactory());

        searchResults = new ArrayList<>();

        searchResults.add(RESULT_ONE);

        observableSearchResults = new MutableLiveData<>();

        when(Mocks.algoliaSearchViewModelMock.getQueryResult()).thenReturn(observableSearchResults);

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

    }


    @After
    public void tearDown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();
    }


    @Test
    public void search_success(){

        // setup viewModel response

        when(Mocks.algoliaSearchViewModelMock.search(eq("a"), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableSearchResults.setValue(DataResource.success(searchResults));

                return null;
            }

        });

        // start the activity and launch fragment

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(searchFragment);
                    }
                });

        // type "a"

        onView(withId(R.id.et_search)).perform(ViewActions.typeText("a"));

        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasViews(0, new ArrayList<>(Arrays.asList(
                R.id.iv_poster, R.id.tv_title)))));


    }

    @Test
    public void search_error(){

        // setup viewModel response

        when(Mocks.algoliaSearchViewModelMock.search(eq("a"), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableSearchResults.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableSearchResults.setValue(DataResource.success(searchResults));

                return null;
            }

        });

        // start the activity and launch fragment

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(searchFragment);
                    }
                });

        // type "a"

        onView(withId(R.id.et_search)).perform(ViewActions.typeText("a"));

        // verify

        onView(withId(R.id.rv_results)).check(matches(withEffectiveVisibility(GONE)));

        onView(withId(R.id.error_message)).check(matches(isDisplayed()));

        // try again

        onView(withId(R.id.btn_try_again)).perform(click());


        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasViews(0, new ArrayList<>(Arrays.asList(
                R.id.iv_poster, R.id.tv_title)))));


    }

}
